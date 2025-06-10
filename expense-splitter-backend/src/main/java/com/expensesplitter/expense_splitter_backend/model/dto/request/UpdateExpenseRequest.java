package com.expensesplitter.expense_splitter_backend.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public class UpdateExpenseRequest {

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Size(max = 255, message = "Description can be at most 255 characters")
    private String description;

    private String paidBy; // Name of the person who paid

    @Valid private List<CreateExpenseRequest.ParticipantShareDto> participants;

    private String category;

    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public List<CreateExpenseRequest.ParticipantShareDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<CreateExpenseRequest.ParticipantShareDto> participants) {
        this.participants = participants;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
