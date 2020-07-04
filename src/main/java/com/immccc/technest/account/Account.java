package com.immccc.technest.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;

@Value
@Builder(builderClassName = "AccountBuilder", toBuilder = true)
@JsonDeserialize(builder = Account.AccountBuilder.class)
class Account {

    @JsonPOJOBuilder(withPrefix = "")
    static class AccountBuilder {
    }

    @EqualsAndHashCode.Include
    String name;
    Currency currency;
    BigDecimal balance;

    Boolean treasury;
}
