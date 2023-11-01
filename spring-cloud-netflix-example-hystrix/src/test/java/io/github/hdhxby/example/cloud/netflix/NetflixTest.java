package io.github.hdhxby.example.cloud.netflix;

import static org.junit.Assert.*;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.client.IClient;
import com.netflix.client.IResponse;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.hystrix.contrib.javanica.command.AbstractHystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.HystrixCommandBuilder;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.Target;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.hystrix.HystrixFeign;
import io.github.hdhxby.example.cloud.HystrixApplication;
import io.github.hdhxby.example.cloud.netflix.feign.HystrixdClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestFactory;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.ribbon.apache.RibbonApacheHttpRequest;
import org.springframework.cloud.netflix.ribbon.apache.RibbonLoadBalancingHttpClient;
import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.route.apache.HttpClientRibbonCommandFactory;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HystrixApplication.class})
public class NetflixTest {

    String SERVICE_ID = "world";

    @Autowired
    private EurekaClient client;

    @Autowired
    private EurekaDiscoveryClient discoveryClient;

    @Autowired
    private SpringClientFactory clientFactory;

    @Autowired
    private RibbonLoadBalancerClient ribbonLoadBalancerClient;

    @Autowired
    private LoadBalancerRequestFactory requestFactory;

    @Autowired
    private LoadBalancerInterceptor loadBalancerInterceptor;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CachingSpringLoadBalancerFactory cachingSpringLoadBalancerFactory;

    @Autowired
    private FeignContext feignContext;

    @Autowired
    private HystrixdClient hystrixdClient;

    /**
     * Eureka 服务发现
     */
    @Test
    public void tesEurekaClient(){
        assertNotNull(client instanceof CloudEurekaClient);
        Applications applications = client.getApplications();
        assertNotNull(applications);
        Application application =  client.getApplication(SERVICE_ID);
        assertNotNull(application);
        List<InstanceInfo> instances = application.getInstances();
        assertTrue(!instances.isEmpty());
    }

    /**
     * Cloud 服务发现
     */
    @Test
    public void testEurekaDiscoveryClient(){
        assertTrue(discoveryClient instanceof DiscoveryClient);
        List<String> services = discoveryClient.getServices();
        assertTrue(!services.isEmpty());
    }

    /**
     * Eureka 负载均衡
     */
    @Test
    public void testILoadBalancer(){
        ILoadBalancer loadBalancer = clientFactory.getLoadBalancer(SERVICE_ID);
        assertTrue(loadBalancer instanceof ZoneAwareLoadBalancer);
        Server server = loadBalancer.chooseServer(SERVICE_ID);
        assertNotNull(server);
    }

    /**
     * Eureka 负载均衡
     * @throws Exception
     */
    @Test
    public void testIClient() throws Exception {
        IClient client =  clientFactory.getClient(SERVICE_ID, IClient.class);
        assertTrue(client instanceof RibbonLoadBalancingHttpClient);
        ILoadBalancer loadBalancer = clientFactory.getLoadBalancer(SERVICE_ID);
        ((RibbonLoadBalancingHttpClient) client).setLoadBalancer(loadBalancer);

        RibbonCommandContext context = new RibbonCommandContext(SERVICE_ID,"GET","/api/world?name=world",true,new HttpHeaders(),new LinkedMultiValueMap(),
                new ServletInputStreamWrapper(null), new ArrayList<>(),-1l,null);
        IResponse response = client.execute(new RibbonApacheHttpRequest(context)
                .replaceUri(((RibbonLoadBalancingHttpClient) client).reconstructURIWithServer(
                        ((RibbonLoadBalancingHttpClient) client).getLoadBalancer()
                                .chooseServer(context.getServiceId()),
                        context.uri()))
                ,null);
//        assertEquals("hello world.", new String(((RibbonApacheHttpResponse) response).getInputStream().readAllBytes())); // fixme jdk1.8兼容
    }

    /**
     * Cloud 负载均衡
     */
    @Test
    public void testLoadBalancerClient(){
        assertTrue(ribbonLoadBalancerClient instanceof ServiceInstanceChooser);
        assertTrue(ribbonLoadBalancerClient instanceof LoadBalancerClient);

        RestTemplate restTemplate = new RestTemplateBuilder()
                .additionalInterceptors(((request, body, execution) -> ribbonLoadBalancerClient
                            .execute(SERVICE_ID,requestFactory.createRequest(request,body,execution))))
                .build();

        ResponseEntity<String> response = restTemplate.getForEntity(URI.create("http://world/api/world?name=world"),String.class);
        assertEquals("hello world.", response.getBody());
    }

    /**
     * 负载均衡拦截器
     * LoadBalancerInterceptor
     */
    @Test
    public void testRestTemplateBuilder(){
        // 手动添加拦截器
        RestTemplate restTemplate = new RestTemplateBuilder()
                .additionalInterceptors(loadBalancerInterceptor)
                .build();
        ResponseEntity<String> response = restTemplate.getForEntity(URI.create("http://world/api/world?name=world"),String.class);
        assertEquals("hello world.", response.getBody());
    }

    /**
     * 自动负载均衡
     * @see org.springframework.cloud.client.loadbalancer.LoadBalanced
     */
    @Test
    public void testRestTemplate(){
        // 添加了LoadBalanced注解的会自动添加拦截器
        ResponseEntity<String> response = restTemplate.getForEntity(URI.create("http://world/api/world?name=world"),String.class);
        assertEquals("hello world.", response.getBody());
    }

    /**
     * @see org.springframework.cloud.openfeign.EnableFeignClients
     */
    @Test
    public void testFeign() throws Exception {
        Feign feign = Feign.builder()
                .contract(feignContext.getInstance(SERVICE_ID, Contract.class))
                .decoder(feignContext.getInstance(SERVICE_ID, Decoder.class))
                .encoder(feignContext.getInstance(SERVICE_ID, Encoder.class))
                .client(new LoadBalancerFeignClient((Client) NetflixTest.class.getClassLoader()
                        .loadClass("feign.Client$Default").getConstructor(SSLSocketFactory.class, HostnameVerifier.class).newInstance(null,null),
                        cachingSpringLoadBalancerFactory,clientFactory))
                .build();
        HystrixdClient hystrixdClient = feign.newInstance(new Target.HardCodedTarget<>(HystrixdClient.class,"world","http://world"));
        ResponseEntity<String> response = hystrixdClient.world("world",0L);
        assertEquals("hello world.", response.getBody());
    }

    /**
     * @see org.springframework.cloud.openfeign.EnableFeignClients
     * @see org.springframework.cloud.openfeign.FeignClientsRegistrar
     * @see org.springframework.cloud.openfeign.FeignClientFactoryBean
     */
    @Test
    public void testFeignClient(){
        // feign.hystrix.enabled = false
        ResponseEntity<String> response = hystrixdClient.world("world",0L);
        assertEquals("hello world.", response.getBody());
    }

    /**
     * 断路
     * @see org.springframework.cloud.openfeign.EnableFeignClients
     * @see org.springframework.cloud.openfeign.FeignClientsRegistrar
     * @see org.springframework.cloud.openfeign.FeignClientFactoryBean
     */
    @Test
    public void testFeignClient2() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(100);
        AbstractHystrixCommand abstractHystrixCommand = new AbstractHystrixCommand<ResponseEntity<String>>(HystrixCommandBuilder.builder()
                .build()) {
            @Override
            protected ResponseEntity<String> run() throws Exception {
                latch.countDown();
                return hystrixdClient.world("world",0L);
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for(int i = 0;i< 100;i++) {
            executorService.submit(() -> {
                abstractHystrixCommand.execute();
            });
        }
        latch.await();
    }

    /**
     * @see org.springframework.cloud.openfeign.EnableFeignClients
     */
    @Test
    public void testHystrixFeign() throws Exception {
        Feign feign = HystrixFeign.builder()
                .contract(feignContext.getInstance(SERVICE_ID, Contract.class))
                .decoder(feignContext.getInstance(SERVICE_ID, Decoder.class))
                .encoder(feignContext.getInstance(SERVICE_ID, Encoder.class))
                .client(new LoadBalancerFeignClient((Client) NetflixTest.class.getClassLoader().loadClass("feign.Client$Default").getConstructor(SSLSocketFactory.class, HostnameVerifier.class).newInstance(null,null),
                        cachingSpringLoadBalancerFactory,clientFactory))
                .build();
        HystrixdClient hystrixdClient = feign.newInstance(new Target.HardCodedTarget<>(HystrixdClient.class,"world","http://world"));
        hystrixdClient.world("builder",0L);
    }

    /**
     * Eureka 负载均衡
     * @throws Exception
     */
    @Test
    public void testIClient1() throws Exception {
        IClient client =  clientFactory.getClient(SERVICE_ID, IClient.class);
        assertTrue(client instanceof RibbonLoadBalancingHttpClient);
        ILoadBalancer loadBalancer = clientFactory.getLoadBalancer(SERVICE_ID);
        ((RibbonLoadBalancingHttpClient) client).setLoadBalancer(loadBalancer);

        RibbonCommandContext context = new RibbonCommandContext(SERVICE_ID,"GET","/api/world?name=world",true,new HttpHeaders(),new LinkedMultiValueMap(),
                new ServletInputStreamWrapper(null), new ArrayList<>(),-1l,null);
//        IResponse response = client.execute(new RibbonApacheHttpRequest(conte        IResponse response = client.execute(new RibbonApacheHttpRequest(context)
//                .replaceUri(((RibbonLoadBalancingHttpClient) client).reconstructURIWithServer(
//                        ((RibbonLoadBalancingHttpClient) client).getLoadBalancer()
//                                .chooseServer(context.getServiceId()),
//                        context.uri()))
//                ,null);
        ZuulProperties zuulProperties = new ZuulProperties();
        HttpClientRibbonCommandFactory commandFactory = new HttpClientRibbonCommandFactory(clientFactory,zuulProperties);
        ClientHttpResponse response = commandFactory.create(context).execute();
//        assertEquals("hello world.", new String(response.getBody().readAllBytes())); // fixme jdk1.8兼容
    }
}
