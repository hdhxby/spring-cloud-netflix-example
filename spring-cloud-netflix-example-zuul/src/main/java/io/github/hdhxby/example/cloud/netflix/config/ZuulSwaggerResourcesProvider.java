package io.github.hdhxby.example.cloud.netflix.config;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.CompositeRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Component
public class ZuulSwaggerResourcesProvider implements SwaggerResourcesProvider {

    private DiscoveryClient discoveryClient;
    private RouteLocator routeLocator;

    public ZuulSwaggerResourcesProvider(DiscoveryClient discoveryClient, RouteLocator routeLocator) {
        this.discoveryClient = discoveryClient;
        this.routeLocator = routeLocator;
    }

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public List<SwaggerResource> get() {
        return routeLocator.getRoutes().stream()
                .filter(route -> route.isPrefixStripped())
                .map(route -> {
                    SwaggerResource swaggerResource = new SwaggerResource();
                    swaggerResource.setSwaggerVersion("3.0.3");
                    swaggerResource.setName(route.getId());
                    swaggerResource.setUrl(route.getFullPath().replace("**","v3/api-docs"));
                    return swaggerResource;
                }).collect(Collectors.toList());
    }
}
