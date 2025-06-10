package com.expensesplitter.expense_splitter_backend.model.dto.response;

import com.expensesplitter.expense_splitter_backend.model.entity.ShareType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ExpenseResponse {
    private Long id;
    private BigDecimal amount;
    private String description;
    private String paidBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String category;
    private List<ParticipantShareResponse> participants;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPaidBy() { return paidBy; }
    public void setPaidBy(String paidBy) { this.paidBy = paidBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public List<ParticipantShareResponse> getParticipants() { return participants; }
    public void setParticipants(List<ParticipantShareResponse> participants) { this.participants = participants; }

    public static class ParticipantShareResponse {
        private String name;
        private BigDecimal shareAmount;
        private ShareType shareType;
        private BigDecimal sharePercentage;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getShareAmount() { return shareAmount; }
        public void setShareAmount(BigDecimal shareAmount) { this.shareAmount = shareAmount; }
        public ShareType getShareType() { return shareType; }
        public void setShareType(ShareType shareType) { this.shareType = shareType; }
        public BigDecimal getSharePercentage() { return sharePercentage; }
        public void setSharePercentage(BigDecimal sharePercentage) { this.sharePercentage = sharePercentage; }
    }
}
