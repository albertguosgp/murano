package com.murano.oms.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.shareddata.SharedData;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class VertxConfig {
    private Vertx vertx;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Bean
    @DependsOn(value = "vertx")
    public EventBus createEventBus() {
        EventBus eventBus = vertx.eventBus();
        return eventBus;
    }

    @Bean(name = "vertx")
    public Vertx createVertx() throws InterruptedException {
        ClusterManager clusterManager = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(clusterManager);

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                vertx = res.result();
                countDownLatch.countDown();
                log.info("Clustered vertx created successfully" + vertx);
            } else {
                log.error("Failed to cluster vertx " + res.cause());
            }
        });
        boolean isVertxClustered = countDownLatch.await(30, TimeUnit.SECONDS);
        if (!isVertxClustered) {
            throw new RuntimeException("Failed to vertx cluster");
        }

        return vertx;
    }
}
