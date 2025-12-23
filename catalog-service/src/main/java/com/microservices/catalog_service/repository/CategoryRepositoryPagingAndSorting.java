package com.microservices.catalog_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
//import com.microservices.catalog_service.model.RefreshToken;

import java.util.Optional;

import com.microservices.catalog_service.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CategoryRepositoryPagingAndSorting extends PagingAndSortingRepository<Category, Integer> {

    @Query("SELECT c FROM Category c")
    Page<Category> findAllPagedAndSortedCategories(Pageable pageable);

}


