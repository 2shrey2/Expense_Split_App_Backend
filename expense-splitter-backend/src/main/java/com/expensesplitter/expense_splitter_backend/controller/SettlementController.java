package com.expensesplitter.expense_splitter_backend.controller;

import com.expensesplitter.expense_splitter_backend.model.dto.response.ApiResponse;
import com.expensesplitter.expense_splitter_backend.model.dto.response.BalanceResponse;
import com.expensesplitter.expense_splitter_backend.model.dto.response.SettlementResponse;
import com.expensesplitter.expense_splitter_backend.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    @Autowired
    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> getSettlements() {
        List<SettlementResponse> settlements = settlementService.getSettlements();
        return ResponseEntity.ok(ApiResponse.success(settlements, "Successfully retrieved settlements"));
    }

    @GetMapping("/balances")
    public ResponseEntity<ApiResponse<List<BalanceResponse>>> getBalances() {
        List<BalanceResponse> balances = settlementService.calculateBalances();
        return ResponseEntity.ok(ApiResponse.success(balances, "Successfully retrieved balances"));
    }
}
