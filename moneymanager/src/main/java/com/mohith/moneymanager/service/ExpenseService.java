package com.mohith.moneymanager.service;

import com.mohith.moneymanager.dto.ExpenseDto;
import com.mohith.moneymanager.entity.CategoryEntity;
import com.mohith.moneymanager.entity.ExpenseEntity;
import com.mohith.moneymanager.entity.ProfileEntity;
import com.mohith.moneymanager.repository.CategoryRepository;
import com.mohith.moneymanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    // Adds new expense to db
    public ExpenseDto addExpense(ExpenseDto dto){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findByIdAndProfileId(dto.getCategoryId(), profile.getId())
                .orElseThrow(() -> new RuntimeException("Category Not Found or not accessible"));
        ExpenseEntity expenseEntity = toEntity(dto,profile,category);
        expenseEntity = expenseRepository.save(expenseEntity);
        return toDto(expenseEntity);
    }

    // Retrieves all expenses for current month/based on the start date and end date
    public List<ExpenseDto> getCurrentMonthExpenseForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(this::toDto).toList();
    }

    //delete expense by id for current user
    public void deleteExpense(Long expenseId){
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity entity = expenseRepository.findById(expenseId)
                .orElseThrow(() ->new RuntimeException("Expense not found"));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized Delete Request");
        }
        expenseRepository.delete(entity);
    }

    // get 5 most recent expenses
    public List<ExpenseDto> getLatest5ExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();
    }

    // get total expenses for current user
    public BigDecimal getTotalExpenseForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    //filtering expenses
    public List<ExpenseDto> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDto).toList();
    }

    //Notifications
    public List<ExpenseDto> getExpensesForUserOnDate(Long profileId, LocalDate date) {
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDate(profileId, date);
        return list.stream().map(this::toDto).toList();
    }

    private ExpenseEntity toEntity(ExpenseDto expenseDto, ProfileEntity profile, CategoryEntity category){
        return ExpenseEntity.builder()
                .name(expenseDto.getName())
                .icon(expenseDto.getIcon())
                .date(expenseDto.getDate())
                .amount(expenseDto.getAmount())
                .profile(profile)
                .category(category)
                .build();
    }

    public ExpenseDto toDto(ExpenseEntity entity){
        return ExpenseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
                .date(entity.getDate())
                .amount(entity.getAmount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
