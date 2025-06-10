package com.expensesplitter.expense_splitter_backend.model.entity;

/**
 * Enum representing different ways expenses can be split among participants
 */
public enum ShareType {
    
    /**
     * Split the expense equally among all participants
     * Each person pays: total_amount / number_of_participants
     */
    EQUAL("Equal Split"),
    
    /**
     * Split the expense based on specified percentages
     * Each person pays: (their_percentage / 100) * total_amount
     */
    PERCENTAGE("Percentage Split"),
    
    /**
     * Split the expense based on exact amounts specified for each participant
     * Each person pays the exact amount specified for them
     */
    EXACT("Exact Amount Split");
    
    private final String description;
    
    ShareType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return description;
    }
}