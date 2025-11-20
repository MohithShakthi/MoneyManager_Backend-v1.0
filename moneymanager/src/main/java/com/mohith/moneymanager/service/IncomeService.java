package com.mohith.moneymanager.service;

import com.mohith.moneymanager.dto.IncomeDto;
import com.mohith.moneymanager.entity.CategoryEntity;
import com.mohith.moneymanager.entity.IncomeEntity;
import com.mohith.moneymanager.entity.ProfileEntity;
import com.mohith.moneymanager.repository.CategoryRepository;
import com.mohith.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;


    // Adds new Income to db
    public IncomeDto addIncome(IncomeDto dto){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findByIdAndProfileId(dto.getCategoryId(),profile.getId())
                .orElseThrow(() -> new RuntimeException("Category Not Found or not accessible"));
        IncomeEntity incomeEntity = toEntity(dto,profile,category);
        incomeEntity = incomeRepository.save(incomeEntity);
        return toDto(incomeEntity);
    }

    // Retrieves all Income for current month/based on the start date and end date
    public List<IncomeDto> getCurrentMonthIncomeForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(this::toDto).toList();
    }

    //delete expense by id for current user
    public void deleteIncome(Long incomeId){
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity entity = incomeRepository.findById(incomeId)
                .orElseThrow(() ->new RuntimeException("Income not found"));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized Delete Request");
        }
        incomeRepository.delete(entity);
    }

    // get 5 recent Incomes for current user
    public List<IncomeDto> getLatest5IncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();
    }

    // get total income for current user
    public BigDecimal getTotalIncomeForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total: BigDecimal.ZERO;
    }

    //filtering incomes
    public List<IncomeDto> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDto).toList();
    }

    private IncomeEntity toEntity(IncomeDto incomeDto, ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
                .name(incomeDto.getName())
                .icon(incomeDto.getIcon())
                .date(incomeDto.getDate())
                .amount(incomeDto.getAmount())
                .profile(profile)
                .category(category)
                .build();
    }

    public IncomeDto toDto(IncomeEntity entity){
        return IncomeDto.builder()
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
