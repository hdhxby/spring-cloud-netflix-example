package io.github.hdhxby.example.cloud.netflix.loadbalancer;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;

public class ThinkRule implements IRule {

    private ILoadBalancer loadBalancer;

    public ThinkRule(ILoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public Server choose(Object o) {
        return loadBalancer.getAllServers().stream().findFirst().get();
    }

    @Override
    public void setLoadBalancer(ILoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public ILoadBalancer getLoadBalancer() {
        return loadBalancer;
    }
}
