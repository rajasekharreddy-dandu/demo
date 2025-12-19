package com.microservices.catalog_service.helper;

import com.microservices.catalog_service.dto.request.CategoryDto;
import com.microservices.catalog_service.entity.Category;


import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface CategoryMappingHelper {

    static CategoryDto map(final Category category) {
        final var parentCategory = Optional.ofNullable(category.getParentCategory())
                .orElseGet(Category::new);
        return CategoryDto.builder()
                .categoryId(category.getCategoryId())
                .categoryTitle(category.getCategoryTitle())
                .imageUrl(category.getImageUrl())
                .parentCategoryDto(
                        CategoryDto.builder()
                                .categoryId(parentCategory.getCategoryId())
                                .categoryTitle(parentCategory.getCategoryTitle())
                                .imageUrl(parentCategory.getImageUrl())
                                .build()
                )
                .build();
    }

    static Category map(CategoryDto categoryDto) {
        final var parentCategoryDto = Optional.ofNullable(categoryDto.getParentCategoryDto())
                .orElseGet(CategoryDto::new);
        return Category.builder()
                .categoryId(categoryDto.getCategoryId())
                .categoryTitle(categoryDto.getCategoryTitle())
                .imageUrl(categoryDto.getImageUrl())
                .parentCategory(Category.builder()
                        .categoryId(parentCategoryDto.getCategoryId())
                        .categoryTitle(parentCategoryDto.getCategoryTitle())
                        .imageUrl(parentCategoryDto.getImageUrl())
                        .build())
                .build();
    }



    static CategoryDto mapCategory(Category category, boolean includeSubCategories) {
        if (category == null) return null;

        Set<CategoryDto> subCategories = null;
        if (includeSubCategories && category.getSubCategories() != null) {
            subCategories = category.getSubCategories().stream()
                    .map(sub -> mapCategory(sub, false)) // avoid deep recursion
                    .collect(Collectors.toSet());
        }

        return CategoryDto.builder()
                .categoryId(category.getCategoryId())
                .categoryTitle(category.getCategoryTitle())
                .imageUrl(category.getImageUrl())
                // .parentCategoryDto(mapCategory(category.getParentCategory(), false)) // avoid deep recursion
                .parentCategoryDto(map(category.getParentCategory())) // avoid deep recursion
                .subCategoriesDtos(subCategories)
                .build();
    }

    static Category mapCategoryDto(CategoryDto dto) {
        if (dto == null) return null;

        return Category.builder()
                .categoryId(dto.getCategoryId())
                .categoryTitle(dto.getCategoryTitle())
                .imageUrl(dto.getImageUrl())
                .parentCategory(mapCategoryDto(dto.getParentCategoryDto()))
                .build();
    }

}

