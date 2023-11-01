package io.github.hdhxby.example.cloud.netflix.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//@Component
public class WorldSemaphoreCommand extends HystrixCommand<String> {

    private RestTemplate restTemplate = new RestTemplate();

    public WorldSemaphoreCommand(){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("worldSemaphoreCommandGroup"))
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)));
    }

    @Override
    protected String run() throws Exception {
        return restTemplate.getForObject("http://localhost:8082/api/say",String.class);
    }

    @Override
    protected String getFallback() {
        return super.getFallback();
    }
}
