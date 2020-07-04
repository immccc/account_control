package com.immccc.technest.account;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RestController
@RequestMapping("accounts")
class AccountController {

    private final AccountService service;

    @ResponseStatus(CREATED)
    @PostMapping
    Mono<Account> create(@RequestBody Account account) {
        return service.create(account);
    }


}
