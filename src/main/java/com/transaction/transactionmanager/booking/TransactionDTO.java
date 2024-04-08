package com.transaction.transactionmanager.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO implements Serializable {

    @JsonProperty("Transaction Number")
    private String transactionNumber;

    @JsonProperty("First Name")
    private String firstName;

    @JsonProperty("Last Name")
    private String lastName;

    @JsonProperty("Email Id")
    private String emailId;

    @JsonProperty("Transaction Amount")
    private String transactionAmount;
}
