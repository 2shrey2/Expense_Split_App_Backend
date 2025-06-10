package com.expensesplitter.expense_splitter_backend.repository;

import com.expensesplitter.expense_splitter_backend.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByName(String name);
    boolean existsByName(String name);
}
