package io.github.hdhxby.example.cloud.netflix.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class ActionCommand<R> extends HystrixCommand<R> {
    private Action<R> command;
    private Action<R> fallback;

    public ActionCommand() {
        this("fallbackCommandGroup");
    }

    public ActionCommand(String key){
        super(HystrixCommandGroupKey.Factory.asKey(key));
    }

    public ActionCommand(Setter setter) {
        super(setter);
    }


    public ActionCommand<R> command(Action<R> command) {
        this.command = command;
        return this;
    }

    public ActionCommand<R> fallback(Action<R> fallback) {
        this.fallback = fallback;
        return this;
    }

    @Override
    protected R run() throws Exception {
        return command.apply();
    }


    @Override
    protected R getFallback() {
        return fallback.apply();
    }
}