package com.immccc.technest.account;

import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

@ResponseStatus(PRECONDITION_FAILED)
class TreasuryAccountWithNoEnoughFoundsException extends RuntimeException {

    public TreasuryAccountWithNoEnoughFoundsException(BigDecimal amount, String name) {
        super(String.format(
                "Can´t transfer %s from %s because is a treasury account with no enough funds",
                amount.toString(), name));
    }
}
