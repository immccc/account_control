package com.immccc.technest.account;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.Optional;

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

    public Mono<BigDecimal> transfer(BigDecimal amount, String fromAccountName, String toAccountName) {

        Mono<Account> from = find(fromAccountName)
                .switchIfEmpty(Mono.error(AccountNotExistingException::new))
                .doOnNext(storedAccount -> assertCanTransfer(amount, storedAccount))
                .flatMap(storedAccount -> addAmount(amount.negate(), storedAccount));

        Mono<Account> to = find(toAccountName)
                .switchIfEmpty(Mono.error(AccountNotExistingException::new))
                .flatMap(storedAccount-> addAmount(amount, storedAccount));

        return Flux.zip(from, to).single().map(accounts -> amount)
                .publishOn(Schedulers.single());
    }

    private Mono<Account> addAmount(BigDecimal amount, Account account) {
        Account updatedAccount = Account.builder()
                .name(account.getName())
                .currency(account.getCurrency())
                .balance(account.getBalance().add(amount))
                .treasury(account.getTreasury())
                .build();
        return operations.opsForValue().set(account.getName(), updatedAccount)
                .map(unused -> account);
    }

    private void assertTreasuryPropertyUnchanged(Account account, Account storedAccount) {
        if(account.getTreasury() != null && account.getTreasury() != storedAccount.getTreasury()) {
            throw new TreasuryPropertyCannotBeModifiedException();
        }
    }

    private void assertCanTransfer(BigDecimal amount, Account account) {

        boolean fromTreasuryAccount = Optional.ofNullable(account.getTreasury()).orElse(false);

        if(fromTreasuryAccount
                && account.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new TreasuryAccountWithNoEnoughFoundsException(amount, account.getName());
        }
    }
}
