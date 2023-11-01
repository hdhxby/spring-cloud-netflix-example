package io.github.hdhxby.example.cloud.netflix.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class FallBackCommand extends HystrixCommand<String> {

    public FallBackCommand() {
        this("fallbackCommandGroup");
    }

    public FallBackCommand(String key){
        super(HystrixCommandGroupKey.Factory.asKey(key));
    }

    public FallBackCommand(Setter setter) {
        super(setter);
    }

    @Override
    protected String run() throws Exception {
        int i = 1/0;
        return "run";
    }

    @Override
    protected String getFallback() {
        return "fallback";
    }
}