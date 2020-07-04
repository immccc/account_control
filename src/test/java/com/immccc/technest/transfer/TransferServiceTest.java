package com.immccc.technest.transfer;

import com.immccc.technest.account.AccountService;
import com.immccc.technest.transfer.Transfer;
import com.immccc.technest.transfer.TransferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    private static final BigDecimal TRANSFER_AMOUNT = BigDecimal.TEN;
    private static final String ACCOUNT_FROM = "account_from";
    private static final String ACCOUNT_TO = "account_to";

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransferService transferService;

    @Test
    @DisplayName("Should perfom a transfer")
    void transfer() {

        Transfer transfer = Transfer.builder()
                .amount(TRANSFER_AMOUNT)
                .from(ACCOUNT_FROM)
                .to(ACCOUNT_TO)
                .build();

        transferService.transfer(transfer);

        verify(accountService).transfer(TRANSFER_AMOUNT, ACCOUNT_FROM, ACCOUNT_TO);
    }

}