package com.transaction.transactionmanager.booking;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/transactions", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class TransactionsController {

    private final TransactionsService transactionsService;

    @PostMapping("/validate")
    public Mono<TransactionResponse> validateTransactions(
            @RequestBody @Validated Mono<TransactionsRequest> request) {
        return request.flatMap(transactionsService::validateTransactions)
                .map(rejectedTransactions -> TransactionResponse.builder()
                        .rejectedTransactions(rejectedTransactions)
                        .build());
    }
}
