package com.transaction.transactionmanager.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionsRequest {

    @NotNull
    List<String> transactions;
    @NotNull
    @DecimalMin(value = "0.00", message = "creditLimit must be higher than 0.00")
    @JsonProperty("creditLimit")
    BigDecimal creditLimit;
}
