package com.expensesplitter.expense_splitter_backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Expense entity representing a single expense that needs to be split among people
 * Each expense has an amount, description, and the person who paid for it
 */
@Entity
@Table(name = "expenses")
public class Expense extends BaseEntity {
    
    @NotNull(message = "Expense amount cannot be null")
    @Positive(message = "Expense amount must be positive")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @NotBlank(message = "Expense description cannot be blank")
    @Size(min = 1, max = 255, message = "Expense description must be between 1 and 255 characters")
    @Column(name = "description", nullable = false, length = 255)
    private String description;
    
    // Many expenses can be paid by one person
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_id", nullable = false)
    @NotNull(message = "Paid by person cannot be null")
    private Person paidBy;
    
    // One expense can have many participants
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore // Avoid circular reference in JSON serialization
    private List<ExpenseParticipant> participants = new ArrayList<>();
    
    // Optional: Category for expense (for future enhancement)
    @Column(name = "category", length = 50)
    private String category;
    
    // Constructors
    public Expense() {}
    
    public Expense(BigDecimal amount, String description, Person paidBy) {
        this.amount = amount;
        this.description = description;
        this.paidBy = paidBy;
    }
    
    public Expense(BigDecimal amount, String description, Person paidBy, String category) {
        this.amount = amount;
        this.description = description;
        this.paidBy = paidBy;
        this.category = category;
    }
    
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
    
    public Person getPaidBy() {
        return paidBy;
    }
    
    public void setPaidBy(Person paidBy) {
        this.paidBy = paidBy;
    }
    
    public List<ExpenseParticipant> getParticipants() {
        return participants;
    }
    
    public void setParticipants(List<ExpenseParticipant> participants) {
        this.participants = participants;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    // Helper methods for managing relationships
    public void addParticipant(ExpenseParticipant participant) {
        participants.add(participant);
        participant.setExpense(this);
    }
    
    public void removeParticipant(ExpenseParticipant participant) {
        participants.remove(participant);
        participant.setExpense(null);
    }
    
    public void clearParticipants() {
        participants.forEach(participant -> participant.setExpense(null));
        participants.clear();
    }
    
    // Business logic methods
    public BigDecimal getTotalParticipantAmount() {
        return participants.stream()
                .map(ExpenseParticipant::getShareAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public int getParticipantCount() {
        return participants.size();
    }
    
    @Override
    public String toString() {
        return "Expense{" +
                "id=" + getId() +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", paidBy=" + (paidBy != null ? paidBy.getName() : "null") +
                ", category='" + category + '\'' +
                ", participantCount=" + participants.size() +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}