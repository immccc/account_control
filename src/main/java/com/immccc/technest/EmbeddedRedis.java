package com.immccc.technest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
class EmbeddedRedis {

    @Value("${spring.redis.port}")
    private int port;

    private RedisServer server;

    @PostConstruct
    void start() {
        server = new RedisServer(port);
        server.start();
    }

    @PreDestroy
    void stop() {
        server.stop();
    }
}
