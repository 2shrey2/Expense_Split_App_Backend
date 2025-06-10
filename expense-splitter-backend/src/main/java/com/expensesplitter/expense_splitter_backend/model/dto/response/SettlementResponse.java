package com.expensesplitter.expense_splitter_backend.model.dto.response;

import java.math.BigDecimal;

public class SettlementResponse {
    private String fromPerson;
    private String toPerson;
    private BigDecimal amount;

    public SettlementResponse(String fromPerson, String toPerson, BigDecimal amount) {
        this.fromPerson = fromPerson;
        this.toPerson = toPerson;
        this.amount = amount;
    }

    // Getters and Setters
    public String getFromPerson() { return fromPerson; }
    public void setFromPerson(String fromPerson) { this.fromPerson = fromPerson; }
    public String getToPerson() { return toPerson; }
    public void setToPerson(String toPerson) { this.toPerson = toPerson; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
