package com.expensesplitter.expense_splitter_backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * ExpenseParticipant entity representing the relationship between an expense and a person
 * This entity stores how much each person owes for a specific expense
 */
@Entity
@Table(name = "expense_participants")
public class ExpenseParticipant extends BaseEntity {
    
    // Many participants can belong to one expense
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    @NotNull(message = "Expense cannot be null")
    private Expense expense;
    
    // Many participations can belong to one person
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    @NotNull(message = "Person cannot be null")
    private Person person;
    
    // The amount this person owes for this expense
    @NotNull(message = "Share amount cannot be null")
    @Positive(message = "Share amount must be positive")
    @Column(name = "share_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal shareAmount;
    
    // Type of share: EQUAL, PERCENTAGE, EXACT
    @Enumerated(EnumType.STRING)
    @Column(name = "share_type", nullable = false, length = 20)
    private ShareType shareType = ShareType.EQUAL;
    
    // For percentage-based splits (0-100)
    @Column(name = "share_percentage", precision = 5, scale = 2)
    private BigDecimal sharePercentage;
    
    // Constructors
    public ExpenseParticipant() {}
    
    public ExpenseParticipant(Expense expense, Person person, BigDecimal shareAmount, ShareType shareType) {
        this.expense = expense;
        this.person = person;
        this.shareAmount = shareAmount;
        this.shareType = shareType;
    }
    
    // Getters and Setters
    public Expense getExpense() {
        return expense;
    }
    
    public void setExpense(Expense expense) {
        this.expense = expense;
    }
    
    public Person getPerson() {
        return person;
    }
    
    public void setPerson(Person person) {
        this.person = person;
    }
    
    public BigDecimal getShareAmount() {
        return shareAmount;
    }
    
    public void setShareAmount(BigDecimal shareAmount) {
        this.shareAmount = shareAmount;
    }
    
    public ShareType getShareType() {
        return shareType;
    }
    
    public void setShareType(ShareType shareType) {
        this.shareType = shareType;
    }
    
    public BigDecimal getSharePercentage() {
        return sharePercentage;
    }
    
    public void setSharePercentage(BigDecimal sharePercentage) {
        this.sharePercentage = sharePercentage;
    }
    
    @Override
    public String toString() {
        return "ExpenseParticipant{" +
                "id=" + getId() +
                ", expense=" + (expense != null ? expense.getId() : "null") +
                ", person=" + (person != null ? person.getName() : "null") +
                ", shareAmount=" + shareAmount +
                ", shareType=" + shareType +
                ", sharePercentage=" + sharePercentage +
                '}';
    }
}