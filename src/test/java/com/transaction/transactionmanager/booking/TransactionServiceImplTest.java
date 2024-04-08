package com.transaction.transactionmanager.booking;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.transaction.transactionmanager.exception.ApplicationException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ComponentScan(basePackages = "com.transaction.transactionmanager.booking")
@TestInstance(PER_CLASS)
public class TransactionServiceImplTest {

    @Autowired
    private TransactionsService transactionsService;

    @Mock
    private TransactionsRequest transactionsRequest;

    @Test
    public void testValidateTransactions_AllValid() {
        // Given
        List<String> transactions = Arrays.asList(
                "John,Doe,john@doe.com,100,TR0001",
                "Jane,Doe,jane@doe.com,50,TR0002"
        );
        when(transactionsRequest.getTransactions()).thenReturn(transactions);
        when(transactionsRequest.getCreditLimit()).thenReturn(new BigDecimal(200));

        // When
        Mono<List<TransactionDTO>> result = transactionsService.validateTransactions(transactionsRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(List::isEmpty)
                .expectComplete()
                .verify();
    }

    @Test
    public void testValidateTransactions_NegativeCase_InvalidTransactionFormat() {
        // Given
        List<String> transactions = Arrays.asList(
                "John,Doe,john@doe.com,100",
                "Jane,Doe,jane@doe.com,50,TR0002"
        );
        when(transactionsRequest.getTransactions()).thenReturn(transactions);
        when(transactionsRequest.getCreditLimit()).thenReturn(new BigDecimal(200));

        // When
        Mono<List<TransactionDTO>> result = transactionsService.validateTransactions(transactionsRequest);

        // Then
        StepVerifier.create(result)
                .expectError(ApplicationException.class)
                .verify();
    }

    @Test
    public void testValidateTransactions_NegativeCase_BlankTransactionString() {
        // Given
        List<String> transactions = Arrays.asList(
                "John,Doe,john@doe.com,100,TR0001",
                "",
                "Jane,Doe,jane@doe.com,50,TR0002"
        );
        when(transactionsRequest.getTransactions()).thenReturn(transactions);
        when(transactionsRequest.getCreditLimit()).thenReturn(new BigDecimal(200));

        // When
        Mono<List<TransactionDTO>> result = transactionsService.validateTransactions(transactionsRequest);

        // Then
        StepVerifier.create(result)
                .expectError(ApplicationException.class)
                .verify();
    }

    @Test
    public void testValidateTransactions_NegativeCase_InvalidTransactionAmount() {
        // Given
        List<String> transactions = Arrays.asList(
                "John,Doe,john@doe.com,100,TR0001",
                "Jane,Doe,jane@doe.com,invalidAmount,TR0002"
        );
        when(transactionsRequest.getTransactions()).thenReturn(transactions);
        when(transactionsRequest.getCreditLimit()).thenReturn(new BigDecimal(200));

        // When
        Mono<List<TransactionDTO>> result = transactionsService.validateTransactions(transactionsRequest);

        // Then
        StepVerifier.create(result)
                .expectError(ApplicationException.class)
                .verify();
    }

    @Test
    public void testValidateTransactions_RejectedTransactions() {
        // Given
        List<String> transactions = Arrays.asList(
                "John,Doe,john@doe.com,150,TR0001",
                "John,Doe,john@doe.com,100,TR0002"
        );
        when(transactionsRequest.getTransactions()).thenReturn(transactions);
        when(transactionsRequest.getCreditLimit()).thenReturn(new BigDecimal(200));

        // When
        Mono<List<TransactionDTO>> result = transactionsService.validateTransactions(transactionsRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(rejectedTransaction -> rejectedTransaction.size() == 1)
                .expectComplete()
                .verify();
    }

    @Test
    public void testValidateTransactions_NegativeCase_NullRequest() {
        // Given
        when(transactionsRequest.getTransactions()).thenReturn(null);
        when(transactionsRequest.getCreditLimit()).thenReturn(null);

        // When
        Mono<List<TransactionDTO>> result = transactionsService.validateTransactions(transactionsRequest);

        // Then
        StepVerifier.create(result)
                .expectError(ApplicationException.class)
                .verify();
    }

    @Test
    public void testEmptyTransactions() {
        TransactionsRequest request = new TransactionsRequest();
        request.setTransactions(List.of());
        request.setCreditLimit(BigDecimal.ZERO);
        Mono<List<TransactionDTO>> result = transactionsService.validateTransactions(request);
        StepVerifier.create(result)
                .expectNextMatches(List::isEmpty)
                .expectComplete()
                .verify();
    }

    @Test
    public void testSingleTransactionWithinCreditLimit() {
        TransactionsRequest request = new TransactionsRequest();
        request.setTransactions(List.of("John,Doe,john@doe.com,100,TR0001"));
        request.setCreditLimit(new BigDecimal(200));
        Mono<List<TransactionDTO>> result = transactionsService.validateTransactions(request);
        StepVerifier.create(result)
                .expectNextMatches(List::isEmpty)
                .expectComplete()
                .verify();
    }

    @Test
    public void testSingleTransactionExceedsCreditLimit() {
        TransactionsRequest request = new TransactionsRequest();
        request.setTransactions(List.of("Jane,Doe,jane@doe.com,201,TR0001"));
        request.setCreditLimit(new BigDecimal(200));
        Mono<List<TransactionDTO>> result = transactionsService.validateTransactions(request);
        StepVerifier.create(result)
                .expectNextMatches(rejected -> rejected.size() == 1 &&
                        rejected.getFirst().getTransactionNumber().equals("TR0001"))
                .expectComplete()
                .verify();
    }

    @Test
    public void testMultipleTransactionsExceedsCreditLimit() {
        TransactionsRequest request = new TransactionsRequest();
        request.setTransactions(Arrays.asList(
                "John,Doe,john@doe.com,100,TR0001",
                "Jane,Doe,jane@doe.com,150,TR0002",
                "Jane,Doe,jane@doe.com,100,TR0003"
        ));
        request.setCreditLimit(new BigDecimal(200));
        Mono<List<TransactionDTO>> result = transactionsService.validateTransactions(request);
        StepVerifier.create(result)
                .expectNextMatches(rejected -> rejected.size() == 1 &&
                        rejected.getFirst().getTransactionNumber().equals("TR0003"))
                .expectComplete()
                .verify();
    }

    @Test
    public void testMultipleTransactionsSecondExceedsCreditLimit() {
        TransactionsRequest request = new TransactionsRequest();
        request.setTransactions(Arrays.asList(
                "John,Doe,john@doe.com,100,TR0001",
                "Jane,Doe,jane@doe.com,201,TR0002",
                "Jane,Doe,jane@doe.com,100,TR0003"
        ));
        request.setCreditLimit(new BigDecimal(200));
        Mono<List<TransactionDTO>> result = transactionsService.validateTransactions(request);
        StepVerifier.create(result)
                .expectNextMatches(rejected -> rejected.size() == 1 &&
                        rejected.getFirst().getTransactionNumber().equals("TR0002"))
                .expectComplete()
                .verify();
    }

    @Test
    public void testMultipleTransactionsAllExceedsCreditLimit() {
        TransactionsRequest request = new TransactionsRequest();
        request.setTransactions(Arrays.asList(
                "John,Doe,john@doe.com,150,TR0001",
                "Jane,Doe,jane@doe.com,201,TR0002",
                "Jane,Doe,jane@doe.com,100,TR0003"
        ));
        request.setCreditLimit(new BigDecimal(50));
        Mono<List<TransactionDTO>> result = transactionsService.validateTransactions(request);
        StepVerifier.create(result)
                .expectNextMatches(rejected -> rejected.size() == 3
                        && rejected.stream().map(TransactionDTO::getTransactionNumber)
                        .allMatch(number ->
                                Arrays.asList("TR0001", "TR0002", "TR0003").contains(number)))
                .expectComplete()
                .verify();
    }
}
