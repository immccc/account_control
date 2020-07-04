package com.immccc.technest.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RestController
@RequestMapping("transfers")
class TransferController {

    private final TransferService service;

    @PostMapping
    @ResponseStatus(CREATED)
    Mono<BigDecimal> transfer(@RequestBody Transfer transfer) {
        return service.transfer(transfer);
    }
}
