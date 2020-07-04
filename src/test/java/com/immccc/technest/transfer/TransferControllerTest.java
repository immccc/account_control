package com.immccc.technest.transfer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TransferService transferService;

    @Test
    @DisplayName("POST")
    void transfer() {
        Transfer transfer = Transfer.builder()
                .amount(TEN)
                .from("account1")
                .to("account2")
                .build();

        doReturn(Mono.just(transfer.getAmount())).when(transferService)
                .transfer(transfer);

        WebTestClient.BodySpec<BigDecimal, ?> bodySpec = webTestClient.post()
                .uri("/transfers")
                .contentType(APPLICATION_JSON)
                .bodyValue(transfer)
                .exchange()
                .expectStatus().isEqualTo(CREATED)
                .expectBody(BigDecimal.class);

        assertThat(bodySpec.returnResult().getResponseBody()).isEqualTo(transfer.getAmount());
    }


}