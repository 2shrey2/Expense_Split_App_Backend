package com.expensesplitter.expense_splitter_backend.service;

import com.expensesplitter.expense_splitter_backend.model.dto.response.BalanceResponse;
import com.expensesplitter.expense_splitter_backend.model.dto.response.SettlementResponse;
import com.expensesplitter.expense_splitter_backend.model.entity.Expense;
import com.expensesplitter.expense_splitter_backend.model.entity.ExpenseParticipant;
import com.expensesplitter.expense_splitter_backend.model.entity.Person;
import com.expensesplitter.expense_splitter_backend.repository.ExpenseRepository;
import com.expensesplitter.expense_splitter_backend.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SettlementService {

    private final ExpenseRepository expenseRepository;
    private final PersonRepository personRepository;

    @Autowired
    public SettlementService(ExpenseRepository expenseRepository, PersonRepository personRepository) {
        this.expenseRepository = expenseRepository;
        this.personRepository = personRepository;
    }

    @Transactional(readOnly = true)
    public List<BalanceResponse> calculateBalances() {
        Map<String, BigDecimal> balances = new HashMap<>();
        List<Person> allPersons = personRepository.findAll();
        List<Expense> allExpenses = expenseRepository.findAll();

        // Ensure all persons are included
        for (Person person : allPersons) {
            balances.put(person.getName(), BigDecimal.ZERO);
        }

        for (Expense expense : allExpenses) {
            if (expense.getPaidBy() != null) {
                String paidByName = expense.getPaidBy().getName();
                balances.put(paidByName, balances.getOrDefault(paidByName, BigDecimal.ZERO).add(expense.getAmount()));
            }

            for (ExpenseParticipant participant : expense.getParticipants()) {
                if (participant.getPerson() != null) {
                    String participantName = participant.getPerson().getName();
                    balances.put(participantName,
                        balances.getOrDefault(participantName, BigDecimal.ZERO).subtract(participant.getShareAmount()));
                }
            }
        }

        // Return all balances, rounded to 2 decimals
        return balances.entrySet().stream()
            .map(entry -> new BalanceResponse(entry.getKey(), entry.getValue().setScale(2, RoundingMode.HALF_UP)))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SettlementResponse> getSettlements() {
        List<BalanceResponse> balances = calculateBalances();
        List<SettlementResponse> settlements = new ArrayList<>();

        Map<String, BigDecimal> owes = new HashMap<>();
        Map<String, BigDecimal> owed = new HashMap<>();

        for (BalanceResponse balance : balances) {
            if (balance.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                owes.put(balance.getPersonName(), balance.getBalance().abs());
            } else if (balance.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                owed.put(balance.getPersonName(), balance.getBalance());
            }
        }

        List<Map.Entry<String, BigDecimal>> sortedOwes = new ArrayList<>(owes.entrySet());
        sortedOwes.sort(Map.Entry.comparingByValue());

        List<Map.Entry<String, BigDecimal>> sortedOwed = new ArrayList<>(owed.entrySet());
        sortedOwed.sort(Map.Entry.comparingByValue());

        int i = 0, j = 0;
        while (i < sortedOwes.size() && j < sortedOwed.size()) {
            Map.Entry<String, BigDecimal> currentOwes = sortedOwes.get(i);
            Map.Entry<String, BigDecimal> currentOwed = sortedOwed.get(j);

            BigDecimal amountToSettle = currentOwes.getValue().min(currentOwed.getValue()).setScale(2, RoundingMode.HALF_UP);

            if (amountToSettle.compareTo(BigDecimal.ZERO) > 0) {
                 settlements.add(new SettlementResponse(currentOwes.getKey(), currentOwed.getKey(), amountToSettle));
            }

            currentOwes.setValue(currentOwes.getValue().subtract(amountToSettle));
            currentOwed.setValue(currentOwed.getValue().subtract(amountToSettle));

            if (currentOwes.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                i++;
            }
            if (currentOwed.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                j++;
            }
        }
        return settlements;
    }
}
