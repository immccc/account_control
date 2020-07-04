package com.immccc.technest.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Currency;

import static java.math.BigDecimal.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final String ACCOUNT_NAME = "account_name";
    private static final BigDecimal ACCOUNT_BALANCE = ONE;
    private static final Currency ACCOUNT_CURRENCY = Currency.getInstance("EUR");

    @Mock
    ReactiveRedisOperations<String, Account> operations;

    @Mock
    ReactiveValueOperations<String, Account> valueOperations;

    @InjectMocks
    private AccountService service;

    @BeforeEach
    public void init() {
        doReturn(valueOperations).when(operations).opsForValue();
    }


    @Test
    @DisplayName("Should find an account")
    void find() {
        Account expectedAccount = givenAccount();

        doReturn(Mono.just(expectedAccount)).when(valueOperations).get(expectedAccount.getName());

        service.find(ACCOUNT_NAME)
                .subscribe(account -> assertThat(account).isNotNull());
    }


    @Test
    @DisplayName("Should create account when it does not exist")
    void create() {
        Account account = givenAccount();

        doReturn(Mono.just(true)).when(valueOperations).setIfAbsent(ACCOUNT_NAME, account);

        Mono<Account> expectedAccount = service.create(account);

        expectedAccount.subscribe(retrievedAccount -> assertThat(account).isNotNull());
    }

    @Test
    @DisplayName("Should throw an exception when creating an account that already exists")
    void createAlreadyExisting() {
        Account account = givenAccount();

        doReturn(Mono.just(false)).when(valueOperations).setIfAbsent(ACCOUNT_NAME, account);

        service.create(account)
                .doOnError(exception -> assertThat(exception)
                .isInstanceOf(AccountAlreadyExistingException.class));
    }

    private Account givenAccount() {
        return Account.builder()
                .name(ACCOUNT_NAME)
                .balance(ACCOUNT_BALANCE)
                .currency(ACCOUNT_CURRENCY)
                .treasury(false)
                .build();
    }

}