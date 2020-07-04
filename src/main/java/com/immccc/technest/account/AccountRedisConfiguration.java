package com.immccc.technest.account;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
class AccountRedisConfiguration {

    private final RedisConnectionFactory connectionFactory;

    @Bean
    ReactiveRedisTemplate<String, Account> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {

        Jackson2JsonRedisSerializer<Account> serializer = new Jackson2JsonRedisSerializer<>(Account.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Account> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Account> context = builder.value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
