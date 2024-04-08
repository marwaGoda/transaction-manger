package com.transaction.transactionmanager.booking;

import java.util.List;
import reactor.core.publisher.Mono;

public interface TransactionsService {

    Mono<List<TransactionDTO>> validateTransactions(TransactionsRequest request);

}
