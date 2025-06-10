package com.expensesplitter.expense_splitter_backend.repository;

import com.expensesplitter.expense_splitter_backend.model.entity.Expense;
import com.expensesplitter.expense_splitter_backend.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByPaidBy(Person paidBy);

    @Query("SELECT e FROM Expense e ORDER BY e.createdAt DESC")
    List<Expense> findAllSorted();
}
