package com.microservices.catalog_service.helper;

import com.microservices.catalog_service.dto.request.CatalogsDto;
import com.microservices.catalog_service.dto.request.CategoryDto;
import com.microservices.catalog_service.entity.Category;
import com.microservices.catalog_service.entity.Catalog;

public interface CatalogMappingHelper {
    static CatalogsDto mapSubCategories(final Catalog catalog) {
        return CatalogsDto.builder()
                .catalogId(catalog.getCatalogId())
                .catalogTitle(catalog.getCatalogTitle())
                .imageUrl(catalog.getImageUrl())
                .sku(catalog.getSku())
                .priceUnit(catalog.getPriceUnit())
                .quantity(catalog.getQuantity())
                .categoryDto(CategoryMappingHelper.mapCategory(catalog.getCategory(), true))
                .build();
    }

    static CatalogsDto map(final Catalog catalog) {
        return CatalogsDto.builder()
                .catalogId(catalog.getCatalogId())
                .catalogTitle(catalog.getCatalogTitle())
                .imageUrl(catalog.getImageUrl())
                .sku(catalog.getSku())
                .priceUnit(catalog.getPriceUnit())
                .quantity(catalog.getQuantity())
                .categoryDto(
                        CategoryDto.builder()
                                .categoryId(catalog.getCategory().getCategoryId())
                                .categoryTitle(catalog.getCategory().getCategoryTitle())
                                .imageUrl(catalog.getCategory().getImageUrl())
                                .build())
                .build();
    }

    static Catalog map(final CatalogsDto catalogsDto) {
        return Catalog.builder()
                .catalogId(catalogsDto.getCatalogId())
                .catalogTitle(catalogsDto.getCatalogTitle())
                .imageUrl(catalogsDto.getImageUrl())
                .sku(catalogsDto.getSku())
                .priceUnit(catalogsDto.getPriceUnit())
                .quantity(catalogsDto.getQuantity())
                .category(
                        Category.builder()
                                .categoryId(catalogsDto.getCategoryDto().getCategoryId())
                                .categoryTitle(catalogsDto.getCategoryDto().getCategoryTitle())
                                .imageUrl(catalogsDto.getCategoryDto().getImageUrl())
                                .build())
                .build();
    }



}


