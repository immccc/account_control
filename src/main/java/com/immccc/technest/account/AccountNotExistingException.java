package com.immccc.technest.account;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

@ResponseStatus(PRECONDITION_FAILED)
class AccountNotExistingException extends RuntimeException {

    AccountNotExistingException() {
        super("Account does not exist");
    }

}
