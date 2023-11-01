package io.github.hdhxby.example.cloud.netflix.ribbon;

import com.netflix.client.ClientFactory;
import com.netflix.client.http.HttpRequest;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.*;
import com.netflix.niws.client.http.RestClient;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ribbon原生调用
 * @author lixiaobin
 * @version 2.0.0
 * @since 2.0.0
 */
public class RibbonbonTest {

    @Test
    public void main() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("helloword-service.ribbon.listOfServers","localhost:8082");

        RestClient restClient = (RestClient) ClientFactory.getNamedClient("helloword-service");
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri("/api/say")
            .build();

        for(int i=0;i<10;i++){
            System.out.println(restClient
                .executeWithLoadBalancer(httpRequest)
                .getEntity(String.class));
        }
    }

    @Test
    public void load(){
        ILoadBalancer iLoadBalancer = new BaseLoadBalancer();
        iLoadBalancer.addServers(Stream.of(new Server("localhost",8080),
                new Server("localhost",8081))
                .collect(Collectors.toList()));

        for(int i=0;i<10;i++){
            System.out.println(iLoadBalancer.chooseServer(null));
        }
    }


    @Test
    public void rule(){
        ILoadBalancer iLoadBalancer = new BaseLoadBalancer();
        ((BaseLoadBalancer)iLoadBalancer).setRule(new IRule() {
            @Override
            public Server choose(Object key) {
                return iLoadBalancer.getAllServers()
                        .stream()
                        .findFirst().get();
            }

            @Override
            public void setLoadBalancer(ILoadBalancer lb) {

            }

            @Override
            public ILoadBalancer getLoadBalancer() {
                return null;
            }
        });

        iLoadBalancer.addServers(Stream.of(new Server("localhost",8080),
            new Server("localhost",8081))
            .collect(Collectors.toList()));

        for(int i=0;i<10;i++){
            System.out.println(iLoadBalancer.chooseServer(null));
        }
    }


    @Test
    public void ping(){
        ILoadBalancer iLoadBalancer = new BaseLoadBalancer();

        iLoadBalancer.addServers(Stream.of(new Server("localhost",8080),
            new Server("localhost",8081))
            .collect(Collectors.toList()));

        ((BaseLoadBalancer) iLoadBalancer).setPing(new IPing() {
            @Override
            public boolean isAlive(Server server) {
                return true;
            }
        });
        ((BaseLoadBalancer) iLoadBalancer).setPingInterval(1);
        for(int i=0;i<10;i++){
            System.out.println(iLoadBalancer.chooseServer(null));
        }
    }
}
