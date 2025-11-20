package com.mohith.moneymanager.controller;

import com.mohith.moneymanager.dto.IncomeDto;
import com.mohith.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDto> addIncome(@RequestBody IncomeDto incomeDto){
        IncomeDto savedDto = incomeService.addIncome(incomeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDto>> getIncomeList(){
        List<IncomeDto> list = incomeService.getCurrentMonthIncomeForCurrentUser();
        return ResponseEntity.ok(list);
    }


    @DeleteMapping("/{incomeId}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long incomeId){
        incomeService.deleteIncome(incomeId);
        return ResponseEntity.noContent().build();
    }
}
