package com.microservices.catalog_service.repository;


import com.microservices.catalog_service.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;




public interface CatalogsRepository extends JpaRepository<Catalog, Integer >{

    boolean existsBySku(String sku);

}

