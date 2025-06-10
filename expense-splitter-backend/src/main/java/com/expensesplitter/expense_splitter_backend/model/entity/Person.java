package com.expensesplitter.expense_splitter_backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Person entity representing individuals who participate in expense splitting
 * People are automatically created when they are mentioned in expenses
 */
@Entity
@Table(name = "persons")
public class Person extends BaseEntity {
    
    @NotBlank(message = "Person name cannot be blank")
    @Size(min = 1, max = 100, message = "Person name must be between 1 and 100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;
    
    // One person can pay for many expenses
    @OneToMany(mappedBy = "paidBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Avoid circular reference in JSON serialization
    private List<Expense> paidExpenses = new ArrayList<>();
    
    // One person can participate in many expenses
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Avoid circular reference in JSON serialization
    private List<ExpenseParticipant> participations = new ArrayList<>();
    
    // Constructors
    public Person() {}
    
    public Person(String name) {
        this.name = name;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Expense> getPaidExpenses() {
        return paidExpenses;
    }
    
    public void setPaidExpenses(List<Expense> paidExpenses) {
        this.paidExpenses = paidExpenses;
    }
    
    public List<ExpenseParticipant> getParticipations() {
        return participations;
    }
    
    public void setParticipations(List<ExpenseParticipant> participations) {
        this.participations = participations;
    }
    
    // Helper methods for managing relationships
    public void addPaidExpense(Expense expense) {
        paidExpenses.add(expense);
        expense.setPaidBy(this);
    }
    
    public void removePaidExpense(Expense expense) {
        paidExpenses.remove(expense);
        expense.setPaidBy(null);
    }
    
    public void addParticipation(ExpenseParticipant participation) {
        participations.add(participation);
        participation.setPerson(this);
    }
    
    public void removeParticipation(ExpenseParticipant participation) {
        participations.remove(participation);
        participation.setPerson(null);
    }
    
    @Override
    public String toString() {
        return "Person{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}