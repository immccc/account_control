package com.immccc.technest.account;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

@ResponseStatus(PRECONDITION_FAILED)
class AccountAlreadyExistingException extends RuntimeException{

    AccountAlreadyExistingException(String accountName) {
        super(String.format("Account with name %s already exists", accountName));
    }
}
