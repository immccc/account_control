package com.immccc.technest.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(AccountController.class)
class AccountControllerTest {

    private static final String ACCOUNT_NAME = "account_name";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccountService accountService;

    static Stream<Arguments> createParametersProvider() {
        return Stream.of(
                arguments(null, CREATED),
                arguments(new AccountAlreadyExistingException(ACCOUNT_NAME), PRECONDITION_FAILED)
        );
    }

    @DisplayName("POST")
    @ParameterizedTest(name = "When exception is {0}, http status is {1}")
    @MethodSource("createParametersProvider")
     void create(Exception exception, HttpStatus expectedHttpStatus) {
        Account account = givenAccount();

        if(exception != null) {
            doThrow(exception).when(accountService).create(account);
        }

        WebTestClient.ResponseSpec responseSpec = webTestClient.post()
                .uri("/accounts")
                .accept(APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                .expectStatus().isEqualTo(expectedHttpStatus);

        if(exception == null) {
            responseSpec.expectBody(Account.class);
        }
    }

    private Account givenAccount() {
        return Account.builder()
                .name(ACCOUNT_NAME)
                .build();
    }


}