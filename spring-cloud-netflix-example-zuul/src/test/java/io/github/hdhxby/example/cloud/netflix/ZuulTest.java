package io.github.hdhxby.example.cloud.netflix;

import com.netflix.client.IClient;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import io.github.hdhxby.example.cloud.ZuulApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.ribbon.apache.RibbonLoadBalancingHttpClient;
import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.route.apache.HttpClientRibbonCommandFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ZuulApplication.class})
public class ZuulTest {

    String SERVICE_ID = "world";

    @Autowired
    private SpringClientFactory clientFactory;

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
