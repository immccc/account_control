package com.immccc.technest.account;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final ReactiveRedisOperations<String, Account> operations;

    public Mono<Account> find(String name) {
        return operations.opsForValue().get(name);
    }

    public Mono<Account> create(Account account) {
        return operations.opsForValue()
                .setIfAbsent(account.getName(), account).map(success -> {
                    if(!success) {
                        throw new AccountAlreadyExistingException(account.getName());
                    }

                    return account;
                });
    }


}
