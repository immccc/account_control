package com.immccc.technest.transfer;

import com.immccc.technest.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
class TransferService {

    private final AccountService accountService;

    public Mono<BigDecimal> transfer(Transfer transfer) {
        return accountService.transfer(transfer.getAmount(), transfer.getFrom(), transfer.getTo());
    }
}
