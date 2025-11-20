package com.mohith.moneymanager.service;

import com.mohith.moneymanager.dto.ExpenseDto;
import com.mohith.moneymanager.dto.IncomeDto;
import com.mohith.moneymanager.dto.RecentTransactionDto;
import com.mohith.moneymanager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class DashBoardService {
    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final ProfileService profileService;

    public Map<String, Object> getDashBoardData(){
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> returnMap = new LinkedHashMap<>();
        List<ExpenseDto> expenseDtoList = expenseService.getLatest5ExpensesForCurrentUser();
        List<IncomeDto> incomeDtoList = incomeService.getLatest5IncomesForCurrentUser();
        List<RecentTransactionDto> recentTransactions = Stream.concat(
                expenseDtoList.stream().map(expense -> RecentTransactionDto.builder()
                        .id(expense.getId())
                        .profileId(profile.getId())
                        .icon(expense.getIcon())
                        .name(expense.getName())
                        .amount(expense.getAmount())
                        .date(expense.getDate())
                        .createdAt(expense.getCreatedAt())
                        .updatedAt(expense.getUpdatedAt())
                        .type("expense")
                        .build()),
                incomeDtoList.stream().map(income ->RecentTransactionDto.builder()
                        .id(income.getId())
                        .profileId(profile.getId())
                        .icon(income.getIcon())
                        .name(income.getName())
                        .amount(income.getAmount())
                        .date(income.getDate())
                        .createdAt(income.getCreatedAt())
                        .updatedAt(income.getUpdatedAt())
                        .type("income")
                        .build())
        ).sorted((a,b) -> {
            int compare = b.getDate().compareTo(a.getDate());
            if(compare == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null){
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            }
            return compare;
        }).toList();
        returnMap.put("totalBalance", incomeService.getTotalIncomeForCurrentUser()
                .subtract(expenseService.getTotalExpenseForCurrentUser()));
        returnMap.put("totalIncome",incomeService.getTotalIncomeForCurrentUser());
        returnMap.put("totalExpense", expenseService.getTotalExpenseForCurrentUser());
        returnMap.put("recentIncome", incomeService.getLatest5IncomesForCurrentUser());
        returnMap.put("recentExpense", expenseService.getLatest5ExpensesForCurrentUser());
        returnMap.put("recentTransaction", recentTransactions);
        return  returnMap;
    }
}
