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

    public Mono<Account> update(Account account) {
        return find(account.getName())
                .switchIfEmpty(Mono.error(AccountNotExistingException::new))
                .doOnNext(storedAccount -> assertTreasuryPropertyUnchanged(account, storedAccount))
                .flatMap(storedAccount -> {
                    Account updatedAccount = Account.builder()
                            .name(account.getName())
                            .currency(account.getCurrency())
                            .balance(account.getBalance())
                            .treasury(storedAccount.getTreasury())
                            .build();
                    return operations.opsForValue().set(account.getName(), updatedAccount);
                }).map(unused -> account);
    }

    private void assertTreasuryPropertyUnchanged(Account account, Account storedAccount) {
        if(account.getTreasury() != null && account.getTreasury() != storedAccount.getTreasury()) {
            throw new TreasuryPropertyCannotBeModifiedException();
        }
    }


}
