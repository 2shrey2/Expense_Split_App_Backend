package com.expensesplitter.expense_splitter_backend.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import com.expensesplitter.expense_splitter_backend.model.entity.ShareType;

import java.math.BigDecimal;
import java.util.List;

public class CreateExpenseRequest {

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 255, message = "Description can be at most 255 characters")
    private String description;

    @NotBlank(message = "PaidBy person's name cannot be blank")
    private String paidBy;

    @NotEmpty(message = "Participants list cannot be empty")
    private List<@Valid ParticipantShareDto> participants;

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

    public List<ParticipantShareDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantShareDto> participants) {
        this.participants = participants;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static class ParticipantShareDto {
        @NotBlank(message = "Participant name cannot be blank")
        private String name;

        @NotNull(message = "Share type cannot be null")
        private ShareType shareType;

        // Required for EXACT and PERCENTAGE, calculated for EQUAL
        private BigDecimal value; // Can be exact amount or percentage

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ShareType getShareType() {
            return shareType;
        }

        public void setShareType(ShareType shareType) {
            this.shareType = shareType;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }
}
