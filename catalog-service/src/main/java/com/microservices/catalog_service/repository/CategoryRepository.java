package com.microservices.catalog_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.microservices.catalog_service.entity.Category;

public interface CategoryRepository extends JpaRepository<Category,Integer > {

    Page<Category> findAll(Pageable pageable);

    Page<Category> findByCategoryTitleContaining(String categoryTitle, Pageable pageable);

    Optional<Category> findByCategoryId(Integer categoryId);

    Optional<Category> findByCategoryTitle(String categoryTitle);

    List<Category> findByParentCategory_CategoryId(Integer parentCategoryId);



}

