package com.expensesplitter.expense_splitter_backend.controller;

import jakarta.validation.Valid;
import com.expensesplitter.expense_splitter_backend.model.dto.request.CreateExpenseRequest;
import com.expensesplitter.expense_splitter_backend.model.dto.request.UpdateExpenseRequest;
import com.expensesplitter.expense_splitter_backend.model.dto.response.ApiResponse;
import com.expensesplitter.expense_splitter_backend.model.dto.response.ExpenseResponse;
import com.expensesplitter.expense_splitter_backend.model.entity.Expense;
import com.expensesplitter.expense_splitter_backend.model.entity.ExpenseParticipant;
import com.expensesplitter.expense_splitter_backend.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    private ExpenseResponse convertToDto(Expense expense) {
        ExpenseResponse dto = new ExpenseResponse();
        dto.setId(expense.getId());
        dto.setAmount(expense.getAmount());
        dto.setDescription(expense.getDescription());
        dto.setPaidBy(expense.getPaidBy().getName());
        dto.setCreatedAt(expense.getCreatedAt());
        dto.setUpdatedAt(expense.getUpdatedAt());
        dto.setCategory(expense.getCategory());

        List<ExpenseResponse.ParticipantShareResponse> participantDtos = expense.getParticipants().stream()
            .map(p -> {
                ExpenseResponse.ParticipantShareResponse psr = new ExpenseResponse.ParticipantShareResponse();
                psr.setName(p.getPerson().getName());
                psr.setShareAmount(p.getShareAmount());
                psr.setShareType(p.getShareType());
                psr.setSharePercentage(p.getSharePercentage());
                return psr;
            }).collect(Collectors.toList());
        dto.setParticipants(participantDtos);
        return dto;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(@Valid @RequestBody CreateExpenseRequest request) {
        Expense expense = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(convertToDto(expense), "Expense created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAllExpenses() {
        List<ExpenseResponse> expenseResponses = expenseService.getAllExpenses().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(expenseResponses, "Successfully retrieved all expenses"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(@PathVariable Long id) {
        Expense expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(ApiResponse.success(convertToDto(expense), "Successfully retrieved expense"));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(@PathVariable Long id, @Valid @RequestBody UpdateExpenseRequest request) {
        Expense updatedExpense = expenseService.updateExpense(id, request);
        return ResponseEntity.ok(ApiResponse.success(convertToDto(updatedExpense), "Expense updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully"));
    }
}
