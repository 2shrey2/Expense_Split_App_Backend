package com.expensesplitter.expense_splitter_backend.model.dto.response;

import java.math.BigDecimal;

public class BalanceResponse {
    private String personName;
    private BigDecimal balance; // Positive if owed, negative if owes

    public BalanceResponse(String personName, BigDecimal balance) {
        this.personName = personName;
        this.balance = balance;
    }

    // Getters and Setters
    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
