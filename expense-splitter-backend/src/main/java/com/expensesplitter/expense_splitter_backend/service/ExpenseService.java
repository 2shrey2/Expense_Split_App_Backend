package com.expensesplitter.expense_splitter_backend.service;

import com.expensesplitter.expense_splitter_backend.exception.InvalidInputException;
import com.expensesplitter.expense_splitter_backend.exception.ResourceNotFoundException;
import com.expensesplitter.expense_splitter_backend.model.dto.request.CreateExpenseRequest;
import com.expensesplitter.expense_splitter_backend.model.dto.request.UpdateExpenseRequest;
import com.expensesplitter.expense_splitter_backend.model.entity.Expense;
import com.expensesplitter.expense_splitter_backend.model.entity.ExpenseParticipant;
import com.expensesplitter.expense_splitter_backend.model.entity.Person;
import com.expensesplitter.expense_splitter_backend.model.entity.ShareType;
import com.expensesplitter.expense_splitter_backend.repository.ExpenseParticipantRepository;
import com.expensesplitter.expense_splitter_backend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final PersonService personService;
    private final ExpenseParticipantRepository expenseParticipantRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, PersonService personService, ExpenseParticipantRepository expenseParticipantRepository) {
        this.expenseRepository = expenseRepository;
        this.personService = personService;
        this.expenseParticipantRepository = expenseParticipantRepository;
    }

    @Transactional
    public Expense createExpense(CreateExpenseRequest request) {
        Person paidBy = personService.findOrCreatePerson(request.getPaidBy());
        Expense expense = new Expense(request.getAmount(), request.getDescription(), paidBy, request.getCategory());
        
        List<ExpenseParticipant> participants = new ArrayList<>();
        BigDecimal totalSharesCalculated = BigDecimal.ZERO;
        int equalShareCount = 0;

        for (CreateExpenseRequest.ParticipantShareDto shareDto : request.getParticipants()) {
            Person participantPerson = personService.findOrCreatePerson(shareDto.getName());
            ExpenseParticipant participant = new ExpenseParticipant();
            participant.setPerson(participantPerson);
            participant.setExpense(expense);
            participant.setShareType(shareDto.getShareType());

            if (shareDto.getShareType() == ShareType.EXACT) {
                if (shareDto.getValue() == null || shareDto.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidInputException("Exact share value must be positive for person: " + shareDto.getName());
                }
                participant.setShareAmount(shareDto.getValue());
                totalSharesCalculated = totalSharesCalculated.add(shareDto.getValue());
            } else if (shareDto.getShareType() == ShareType.PERCENTAGE) {
                if (shareDto.getValue() == null || shareDto.getValue().compareTo(BigDecimal.ZERO) <= 0 || shareDto.getValue().compareTo(new BigDecimal("100")) > 0) {
                    throw new InvalidInputException("Percentage share value must be between 0 and 100 for person: " + shareDto.getName());
                }
                BigDecimal shareAmount = request.getAmount().multiply(shareDto.getValue()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                participant.setShareAmount(shareAmount);
                participant.setSharePercentage(shareDto.getValue());
                totalSharesCalculated = totalSharesCalculated.add(shareAmount);
            } else if (shareDto.getShareType() == ShareType.EQUAL) {
                equalShareCount++;
            } else {
                 throw new InvalidInputException("Invalid share type for person: " + shareDto.getName());
            }
            participants.add(participant);
        }

        if (equalShareCount > 0) {
            BigDecimal remainingAmount = request.getAmount().subtract(totalSharesCalculated);
            if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidInputException("Sum of exact/percentage shares exceeds total expense amount.");
            }
            if (remainingAmount.compareTo(BigDecimal.ZERO) == 0 && equalShareCount > 0){
                 throw new InvalidInputException("Cannot have EQUAL shares if exact/percentage shares already sum up to total amount.");
            }
            BigDecimal equalShareAmount = remainingAmount.divide(new BigDecimal(equalShareCount), 2, RoundingMode.HALF_UP);
            for (ExpenseParticipant p : participants) {
                if (p.getShareType() == ShareType.EQUAL) {
                    p.setShareAmount(equalShareAmount);
                    totalSharesCalculated = totalSharesCalculated.add(equalShareAmount);
                }
            }
        }
        
        // Validate total shares sum up to expense amount (with a small tolerance for rounding)
        if (totalSharesCalculated.subtract(request.getAmount()).abs().compareTo(new BigDecimal("0.01").multiply(new BigDecimal(request.getParticipants().size()))) > 0) {
            throw new InvalidInputException("Sum of participant shares (" + totalSharesCalculated + ") does not match total expense amount (" + request.getAmount() + ").");
        }

        expense.setParticipants(participants);
        return expenseRepository.save(expense);
    }

    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAllSorted();
    }

    @Transactional(readOnly = true)
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
    }

    @Transactional
    public Expense updateExpense(Long id, UpdateExpenseRequest request) {
        Expense expense = getExpenseById(id);

        // Update fields if present
        if (request.getAmount() != null) {
            expense.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            expense.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            expense.setCategory(request.getCategory());
        }
        if (request.getPaidBy() != null && !Objects.equals(expense.getPaidBy().getName(), request.getPaidBy())) {
            Person newPaidBy = personService.findOrCreatePerson(request.getPaidBy());
            expense.setPaidBy(newPaidBy);
        }

        // If new participants are provided, replace the old ones.
        if (request.getParticipants() != null && !request.getParticipants().isEmpty()) {
            // First, delete the old participants from the repository
            expenseParticipantRepository.deleteAll(expense.getParticipants());
            // Then, clear the collection from the expense entity
            expense.getParticipants().clear();

            List<ExpenseParticipant> newParticipants = new ArrayList<>();
            BigDecimal totalSharesCalculated = BigDecimal.ZERO;
            int equalShareCount = 0;
            // Use the potentially updated amount for calculations
            BigDecimal currentExpenseAmount = expense.getAmount();

            for (CreateExpenseRequest.ParticipantShareDto shareDto : request.getParticipants()) {
                Person participantPerson = personService.findOrCreatePerson(shareDto.getName());
                ExpenseParticipant participant = new ExpenseParticipant();
                participant.setPerson(participantPerson);
                participant.setExpense(expense);
                participant.setShareType(shareDto.getShareType());

                if (shareDto.getShareType() == ShareType.EXACT) {
                    if (shareDto.getValue() == null || shareDto.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new InvalidInputException("Exact share value must be positive for person: " + shareDto.getName());
                    }
                    participant.setShareAmount(shareDto.getValue());
                    totalSharesCalculated = totalSharesCalculated.add(shareDto.getValue());
                } else if (shareDto.getShareType() == ShareType.PERCENTAGE) {
                    if (shareDto.getValue() == null || shareDto.getValue().compareTo(BigDecimal.ZERO) <= 0 || shareDto.getValue().compareTo(new BigDecimal("100")) > 0) {
                        throw new InvalidInputException("Percentage share value must be between 0 and 100 for person: " + shareDto.getName());
                    }
                    BigDecimal shareAmount = currentExpenseAmount.multiply(shareDto.getValue()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    participant.setShareAmount(shareAmount);
                    participant.setSharePercentage(shareDto.getValue());
                    totalSharesCalculated = totalSharesCalculated.add(shareAmount);
                } else if (shareDto.getShareType() == ShareType.EQUAL) {
                    equalShareCount++;
                } else {
                    throw new InvalidInputException("Invalid share type for person: " + shareDto.getName());
                }
                newParticipants.add(participant);
            }

            if (equalShareCount > 0) {
                BigDecimal remainingAmount = currentExpenseAmount.subtract(totalSharesCalculated);
                if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
                    throw new InvalidInputException("Sum of exact/percentage shares exceeds total expense amount.");
                }
                if (remainingAmount.compareTo(BigDecimal.ZERO) == 0 && equalShareCount > 0) {
                    throw new InvalidInputException("Cannot have EQUAL shares if exact/percentage shares already sum up to total amount.");
                }
                BigDecimal equalShareAmount = remainingAmount.divide(new BigDecimal(equalShareCount), 2, RoundingMode.HALF_UP);
                for (ExpenseParticipant p : newParticipants) {
                    if (p.getShareType() == ShareType.EQUAL) {
                        p.setShareAmount(equalShareAmount);
                        totalSharesCalculated = totalSharesCalculated.add(equalShareAmount);
                    }
                }
            }

            // Validate total shares sum up to expense amount (with a small tolerance for rounding)
            if (totalSharesCalculated.subtract(currentExpenseAmount).abs().compareTo(new BigDecimal("0.01").multiply(new BigDecimal(request.getParticipants().size()))) > 0) {
                throw new InvalidInputException("Sum of participant shares (" + totalSharesCalculated + ") does not match total expense amount (" + currentExpenseAmount + ").");
            }
            expense.setParticipants(newParticipants);
        }

        return expenseRepository.save(expense);
    }

    @Transactional
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }
}
