package com.mohith.moneymanager.service;

import com.mohith.moneymanager.dto.CategoryDto;
import com.mohith.moneymanager.entity.CategoryEntity;
import com.mohith.moneymanager.entity.ProfileEntity;
import com.mohith.moneymanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    //Sava Categories
    public CategoryDto saveCategory(CategoryDto dto){
        ProfileEntity profile = profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(dto.getName(), profile.getId())){
            throw new RuntimeException("Category With This name Already Exist");
        }

        CategoryEntity categoryEntity = toEntity(dto, profile);
        categoryEntity = categoryRepository.save(categoryEntity);
        return toDto(categoryEntity);
    }

    //Get Categories for current Profile
    public List<CategoryDto> getCategoriesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDto).toList();
    }

    // Get Categories by type for current User
    public List<CategoryDto> getCategoriesByTypeForCurrentUser(String type){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByTypeAndProfileId(type,profile.getId());
        return categories.stream().map(this::toDto).toList();
    }

    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category Not Found Or Not Accessable"));
        existingCategory.setName(categoryDto.getName());
        existingCategory.setIcon(categoryDto.getIcon());
        existingCategory.setType(categoryDto.getType());
        existingCategory = categoryRepository.save(existingCategory);
        return toDto(existingCategory);
    }

    private CategoryEntity toEntity(CategoryDto categoryDto, ProfileEntity profile){
        return CategoryEntity.builder()
                .name(categoryDto.getName())
                .type(categoryDto.getType())
                .icon(categoryDto.getIcon())
                .profile(profile)
                .build();
    }

    private CategoryDto toDto(CategoryEntity entity){
        return CategoryDto.builder()
                .id(entity.getId())
                .profileId(entity.getProfile() != null ? entity.getProfile().getId() : null)
                .name(entity.getName())
                .type(entity.getType())
                .icon(entity.getIcon())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
