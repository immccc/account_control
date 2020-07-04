package com.immccc.technest.account;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("accounts")
class AccountController {

    private final AccountService service;

    @GetMapping("/{name}")
    Mono<Account> find(@PathVariable String name) {
        return service.find(name)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND)));
    }

    @ResponseStatus(CREATED)
    @PostMapping
    Mono<Account> create(@RequestBody Account account) {
        return service.create(account);
    }

    @PutMapping
    Mono<Account> update(@RequestBody Account account) {
        return service.update(account);
    }

}
