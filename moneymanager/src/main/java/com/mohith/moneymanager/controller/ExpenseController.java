package com.mohith.moneymanager.controller;

import com.mohith.moneymanager.dto.ExpenseDto;
import com.mohith.moneymanager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDto> addIncome(@RequestBody ExpenseDto expenseDto){
        ExpenseDto savedDto = expenseService.addExpense(expenseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getExpenseList(){
        List<ExpenseDto> list = expenseService.getCurrentMonthExpenseForCurrentUser();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId){
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }
}
