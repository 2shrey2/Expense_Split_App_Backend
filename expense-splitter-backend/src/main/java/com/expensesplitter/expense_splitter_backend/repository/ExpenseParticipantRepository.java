package com.expensesplitter.expense_splitter_backend.repository;

import com.expensesplitter.expense_splitter_backend.model.entity.ExpenseParticipant;
import com.expensesplitter.expense_splitter_backend.model.entity.Person;
import com.expensesplitter.expense_splitter_backend.model.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, Long> {
    List<ExpenseParticipant> findByExpense(Expense expense);
    List<ExpenseParticipant> findByPerson(Person person);
}
