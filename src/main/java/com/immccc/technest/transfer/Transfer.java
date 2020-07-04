package com.immccc.technest.transfer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder(builderClassName = "TransferBuilder", toBuilder = true)
@JsonDeserialize(builder = Transfer.TransferBuilder.class)
class Transfer {

    @JsonPOJOBuilder(withPrefix = "")
    public static class TransferBuilder {
    }

    String from;
    String to;
    BigDecimal amount;
}

