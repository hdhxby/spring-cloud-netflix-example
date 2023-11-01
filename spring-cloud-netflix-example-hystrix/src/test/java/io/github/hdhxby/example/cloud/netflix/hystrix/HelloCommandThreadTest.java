package io.github.hdhxby.example.cloud.netflix.hystrix;

import com.netflix.hystrix.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class HelloCommandThreadTest {

    private static final Logger log = LoggerFactory.getLogger(HelloCommand.class);
    /**
     * 线程池
     */
    @Test
    public void thread() throws ExecutionException, InterruptedException {
        HelloCommand helloCommand = new HelloCommand(HystrixCommand
                .Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("helloworld"))
                .andCommandPropertiesDefaults(HystrixCommandProperties
                        .Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)));
        log.debug(helloCommand
                .execute());
    }

}
