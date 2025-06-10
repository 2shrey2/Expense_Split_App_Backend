package com.expensesplitter.expense_splitter_backend.model.entity;

import com.expensesplitter.expense_splitter_backend.model.entity.Person;
import com.expensesplitter.expense_splitter_backend.model.entity.Expense;
import com.expensesplitter.expense_splitter_backend.model.entity.ExpenseParticipant;
import com.expensesplitter.expense_splitter_backend.model.entity.ShareType;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for entity creation and relationships
 */
@SpringBootTest
public class EntityTest {
    
    @Test
    public void testPersonCreation() {
        Person person = new Person("Shantanu");
        
        assertNotNull(person);
        assertEquals("Shantanu", person.getName());
        assertNotNull(person.getPaidExpenses());
        assertNotNull(person.getParticipations());
        assertTrue(person.getPaidExpenses().isEmpty());
        assertTrue(person.getParticipations().isEmpty());
    }
    
    @Test
    public void testExpenseCreation() {
        Person payer = new Person("Sanket");
        BigDecimal amount = new BigDecimal("600.00");
        Expense expense = new Expense(amount, "Dinner at restaurant", payer);
        
        assertNotNull(expense);
        assertEquals(amount, expense.getAmount());
        assertEquals("Dinner at restaurant", expense.getDescription());
        assertEquals(payer, expense.getPaidBy());
        assertNotNull(expense.getParticipants());
        assertTrue(expense.getParticipants().isEmpty());
    }
    
    @Test
    public void testExpenseParticipantCreation() {
        Person payer = new Person("Om");
        Person participant = new Person("Shantanu");
        BigDecimal expenseAmount = new BigDecimal("300.00");
        BigDecimal shareAmount = new BigDecimal("150.00");
        
        Expense expense = new Expense(expenseAmount, "Petrol", payer);
        ExpenseParticipant expenseParticipant = new ExpenseParticipant(
            expense, participant, shareAmount, ShareType.EQUAL
        );
        
        assertNotNull(expenseParticipant);
        assertEquals(expense, expenseParticipant.getExpense());
        assertEquals(participant, expenseParticipant.getPerson());
        assertEquals(shareAmount, expenseParticipant.getShareAmount());
        assertEquals(ShareType.EQUAL, expenseParticipant.getShareType());
    }
    
    @Test
    public void testRelationshipManagement() {
        Person payer = new Person("Shantanu");
        Person participant1 = new Person("Sanket");
        Person participant2 = new Person("Om");
        
        BigDecimal expenseAmount = new BigDecimal("500.00");
        Expense expense = new Expense(expenseAmount, "Movie Tickets", payer);
        
        // Add participants
        BigDecimal shareAmount = new BigDecimal("166.67"); // 500/3 ≈ 166.67
        
        ExpenseParticipant participation1 = new ExpenseParticipant(
            expense, participant1, shareAmount, ShareType.EQUAL
        );
        ExpenseParticipant participation2 = new ExpenseParticipant(
            expense, participant2, shareAmount, ShareType.EQUAL
        );
        ExpenseParticipant participation3 = new ExpenseParticipant(
            expense, payer, shareAmount, ShareType.EQUAL
        );
        
        expense.addParticipant(participation1);
        expense.addParticipant(participation2);
        expense.addParticipant(participation3);
        
        assertEquals(3, expense.getParticipantCount());
        assertEquals(new BigDecimal("500.01"), expense.getTotalParticipantAmount()); // Due to rounding
    }
    
    @Test
    public void testShareTypeEnum() {
        assertEquals("Equal Split", ShareType.EQUAL.getDescription());
        assertEquals("Percentage Split", ShareType.PERCENTAGE.getDescription());
        assertEquals("Exact Amount Split", ShareType.EXACT.getDescription());
    }
}