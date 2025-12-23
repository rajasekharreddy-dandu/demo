package com.microservices.catalog_service.service.impl;

import java.util.List;
import java.util.Optional;

import com.microservices.catalog_service.dto.request.CatalogsDto;
import com.microservices.catalog_service.dto.request.CategoryDto;
import com.microservices.catalog_service.entity.Catalog;
import com.microservices.catalog_service.exception.wrapper.CatalogNotFoundException;
import com.microservices.catalog_service.helper.CatalogMappingHelper;
import com.microservices.catalog_service.repository.CatalogsRepository;
import com.microservices.catalog_service.service.CatalogsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


import com.microservices.catalog_service.entity.Category;

import com.microservices.catalog_service.exception.wrapper.CategoryNotFoundException;

import com.microservices.catalog_service.repository.CategoryRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@Service
public class CatalogsServiceImpl implements CatalogsService {

    @Autowired
    private final CatalogsRepository catalogsRepository;

    @Autowired
    private final CategoryRepository categoryRepository;

    @Override
    public Flux<List<CatalogsDto>> findAll() {
        log.info("CatalogsDto List, service, fetch all catalogs");
        return Flux.defer(() -> {
                    List<CatalogsDto> catalogDtos = catalogsRepository.findAll()
                            .stream()
                            .map(CatalogMappingHelper::map)
                            .distinct()
                            .toList();
                    return Flux.just(catalogDtos);
                })
                .onErrorResume(throwable -> {
                    log.error("Error while fetching catalogs: " + throwable.getMessage());
                    return Flux.empty();
                });
    }

    @Override
    public CatalogsDto findById(Integer catalogId) {
        log.info("CatalogsDto, service; fetch catalog by id");
        return catalogsRepository.findById(catalogId)
                .map(CatalogMappingHelper::map)
                .orElseThrow(() -> new CatalogNotFoundException(String.format("Catalogs with id[%d] not found", catalogId)));

    }

    @Override
    public CatalogsDto save(CatalogsDto catalogDto) {
        log.info("CatalogsDto, service; save catalog");
        try {

            if (catalogsRepository.existsBySku(catalogDto.getSku())) {
                throw new RuntimeException("Catalogs with SKU already exists: " + catalogDto.getSku());
            }

            // Handle category creation or retrieval
            Category category = getOrCreateCategory(catalogDto.getCategoryDto());

            // Map DTO to entity
            Catalog catalog = CatalogMappingHelper.map(catalogDto);
            catalog.setCategory(category);
            // Save catalog
            Catalog savedCatalogs = catalogsRepository.save(catalog);
            return CatalogMappingHelper.map(savedCatalogs);
        } catch (DataIntegrityViolationException e) {
            log.error("Error saving catalog: Data integrity violation", e);
            throw new CatalogNotFoundException("Error saving catalog: Data integrity violation", e);
        } catch (RuntimeException e) {
            log.error("Error saving catalog", e);
            throw new CatalogNotFoundException("Error saving catalog", e);
        }

    }




    @Override
    public CatalogsDto update(CatalogsDto catalogDto) {
        log.info("CatalogsDto, service; update catalog");

        Catalog existingCatalogs = catalogsRepository.findById(catalogDto.getCatalogId())
                .orElseThrow(() -> new CatalogNotFoundException("Catalogs not found with id: " + catalogDto.getCatalogId()));


        BeanUtils.copyProperties(catalogDto, existingCatalogs, "catalogId", "category");

        // Handle category update
        if (catalogDto.getCategoryDto() != null && catalogDto.getCategoryDto().getCategoryId() != null) {
            Category category = categoryRepository.findByCategoryId(catalogDto.getCategoryDto().getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + catalogDto.getCategoryDto().getCategoryId()));
            existingCatalogs.setCategory(category);
        }
        Catalog updatedCatalogs = catalogsRepository.save(existingCatalogs);
        return CatalogMappingHelper.map(updatedCatalogs);
    }


    @Override
    public CatalogsDto update(Integer catalogId, CatalogsDto catalogDto) {
        log.info("CatalogsDto, service; update catalog with catalogId");

        // Check Catalogs Exists in DB
        Catalog existingCatalogs = catalogsRepository.findById(catalogId)
                .orElseThrow(() -> new CatalogNotFoundException("Catalogs not found with id: " + catalogId));

        // Update Catalogs using BeanUtils.copyProperties
        BeanUtils.copyProperties(catalogDto, existingCatalogs, "catalogId", "category");

        // Handle category update
        if (catalogDto.getCategoryDto() != null && catalogDto.getCategoryDto().getCategoryId() != null) {
            Category category = categoryRepository.findByCategoryId(catalogDto.getCategoryDto().getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + catalogDto.getCategoryDto().getCategoryId()));
            existingCatalogs.setCategory(category);
        }

        // Save to Database
        Catalog updatedCatalogs = catalogsRepository.save(existingCatalogs);
        return CatalogMappingHelper.map(updatedCatalogs);
    }

    @Override
    public void deleteById(Integer catalogId) {
        log.info("Void, service; delete catalog by id");
        this.catalogsRepository.delete(CatalogMappingHelper.map(this.findById(catalogId)));
    }

    private Category getOrCreateCategory(CategoryDto dto) {
        // Step 1: Handle parent category if present
        Category parentCategory = null;
        if (dto.getParentCategoryDto() != null) {
            parentCategory = getOrCreateCategory(dto.getParentCategoryDto());
        }

        // Step 2: Check if main category exists 
        Optional<Category> existingCategory = categoryRepository.findByCategoryTitle(dto.getCategoryTitle());
        Category category;
        if (existingCategory.isPresent()) {
            category = existingCategory.get();
        } else {
            category = Category.builder()
                    .categoryTitle(dto.getCategoryTitle())
                    .imageUrl(dto.getImageUrl())
                    .parentCategory(parentCategory)
                    .build();
            category = categoryRepository.save(category);
        }

        // Step 3: Handle subcategories if present
        if (dto.getSubCategoriesDtos() != null && !dto.getSubCategoriesDtos().isEmpty()) {
            for (CategoryDto subDto : dto.getSubCategoriesDtos()) {
                Optional<Category> existingSub = categoryRepository.findByCategoryTitle(subDto.getCategoryTitle());
                if (existingSub.isEmpty()) {
                    Category subCategory = Category.builder()
                            .categoryTitle(subDto.getCategoryTitle())
                            .imageUrl(subDto.getImageUrl())
                            .parentCategory(category)
                            .build();
                    categoryRepository.save(subCategory);
                }
            }
        }
        return category;
    }


}

