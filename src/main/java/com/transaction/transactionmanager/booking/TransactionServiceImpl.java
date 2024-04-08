package com.transaction.transactionmanager.booking;

import static java.math.BigDecimal.ZERO;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.transaction.transactionmanager.exception.ApplicationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionsService {

    public static final String COMMA = ",";
    public static final int TRANSACTION_FIELDS_COUNT = 5;

    @Override
    public Mono<List<TransactionDTO>> validateTransactions(TransactionsRequest request) {
        return Mono.fromCallable(() -> {
            Map<String, BigDecimal> consumerTotal = new HashMap<>();
            List<TransactionDTO> rejectedTransactions = new ArrayList<>();
            if (request.getTransactions() == null) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "Transactions list shouldn't be empty");
            }
            if (request.getCreditLimit() == null) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "Credit limit shouldn't be empty");
            }
            for (String transaction : request.getTransactions()) {
                processTransaction(transaction, consumerTotal, rejectedTransactions, request.getCreditLimit());
            }

            return rejectedTransactions;
        });
    }

    private void processTransaction(
            String transaction,
            Map<String, BigDecimal> consumerTotal,
            List<TransactionDTO> rejectedTransactions,
            BigDecimal creditLimit) {

        validateTransactionFormat(transaction);

        String[] fields = transaction.split(COMMA);
        String consumerEmail = fields[2];
        BigDecimal amount = new BigDecimal(fields[3]);

        BigDecimal total = consumerTotal.getOrDefault(consumerEmail, ZERO).add(amount);

        if (total.compareTo(creditLimit) > 0) {
            rejectedTransactions.add(createTransactionDTO(fields));
        } else {
            consumerTotal.put(consumerEmail, total);
        }
    }


    private void validateTransactionFormat(String transaction) {
        if (isBlank(transaction)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Transaction string shouldn't be blank");
        }

        String[] fields = transaction.split(COMMA);
        if (fields.length != TRANSACTION_FIELDS_COUNT) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid transaction format");
        }

        try {
            new BigDecimal(fields[3]); // Validate amount as a number
        } catch (NumberFormatException e) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid transaction amount");
        }
    }

    private TransactionResponse buildResponse(List<TransactionDTO> rejectedTransactions) {
        return TransactionResponse.builder()
                .rejectedTransactions(rejectedTransactions)
                .build();
    }

    private TransactionDTO createTransactionDTO(String[] fields) {
        // Assuming fields[0] = first name, fields[1] = last name,  field[2] = email, field[3] = amount, fields[4] = transaction number
        return TransactionDTO.builder()
                .firstName(fields[0])
                .lastName(fields[1])
                .emailId(fields[2])
                .transactionAmount(fields[3])
                .transactionNumber(fields[4])
                .build();
    }
}
