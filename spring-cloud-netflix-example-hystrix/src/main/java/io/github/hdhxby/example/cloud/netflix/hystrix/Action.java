package io.github.hdhxby.example.cloud.netflix.hystrix;

@FunctionalInterface
public interface Action<R> {
    R apply();
}
