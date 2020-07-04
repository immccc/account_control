package com.immccc.technest.account;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.stream.Stream;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


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

    @Test
    @DisplayName("Should update account")
    void update() {
        Account updatedAccount = givenAccount(ACCOUNT_BALANCE, ACCOUNT_CURRENCY, false);

        doReturn(Mono.empty()).when(valueOperations).get(ACCOUNT_NAME);

        service.update(updatedAccount)
                .doOnSuccess(unused -> Assertions.fail("Unexpected success"))
                .doOnError(exception -> assertThat(exception).isInstanceOf(AccountNotExistingException.class));
    }

    @Test
    @DisplayName("Should update account fail because account does not exist")
    void updateNotExisting() {

        Currency updatedCurrency = Currency.getInstance("EUR");
        Currency storedCurrency = Currency.getInstance("USD");

        Account updatedAccount = givenAccount(TEN, updatedCurrency, null);
        Account storedAccount = givenAccount(ONE, storedCurrency, false);

        doReturn(Mono.just(storedAccount)).when(valueOperations).get(ACCOUNT_NAME);

        service.update(updatedAccount)
                .doOnSuccess(account -> {
                    assertThat(account.getName()).isEqualTo(ACCOUNT_NAME);
                    assertThat(account.getBalance()).isEqualTo(updatedAccount.getBalance());
                    assertThat(account.getTreasury()).isEqualTo(storedAccount.getTreasury());
                    verify(valueOperations).set(ACCOUNT_NAME, account);
                })
                .doOnError(unused -> Assertions.fail("Unexpected exception"));
    }


    static Stream<Arguments> updateTreasuryTestParametersProvider() {
        return Stream.of(
                arguments(true, true, null),
                arguments(null, true, null),
                arguments(true, false, "com.immccc.technest.account.TreasuryPropertyCannotBeModifiedException")
        );
    }
    @SneakyThrows
    @DisplayName("Update on treasury field")
    @ParameterizedTest(name = "when new value is {0} and stored value is {1}, an exception {2} should be thrown")
    @MethodSource("updateTreasuryTestParametersProvider")
    void updateTreasuryField(Boolean treasuryValueUpdated, Boolean treasuryValueStored, String expectedExceptionName) {

        Account accountUpdated = givenAccount(ACCOUNT_BALANCE, ACCOUNT_CURRENCY, treasuryValueUpdated);
        Account accountStored = givenAccount(ACCOUNT_BALANCE, ACCOUNT_CURRENCY, treasuryValueStored);

        doReturn(Mono.just(accountStored)).when(valueOperations).get(ACCOUNT_NAME);

        Class<?> exceptionClass = expectedExceptionName == null
                ? null : Class.forName(expectedExceptionName);

        service.update(accountUpdated)
                .doOnSuccess(unused -> assertThat(expectedExceptionName).isNull())
                .doOnError(exception -> assertThat(exception).isInstanceOf(exceptionClass));
    }

    private Account givenAccount(BigDecimal balance, Currency currency, Boolean treasury) {
        return Account.builder()
                .name(ACCOUNT_NAME)
                .balance(balance)
                .currency(currency)
                .treasury(treasury)
                .build();
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