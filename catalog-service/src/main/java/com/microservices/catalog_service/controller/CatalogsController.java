package com.microservices.catalog_service.controller;

import java.util.List;

import com.microservices.catalog_service.dto.request.CatalogsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.microservices.catalog_service.service.CatalogsService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/catalogs")
@RequiredArgsConstructor
@Slf4j
public class CatalogsController {

    @Autowired
    private final CatalogsService catalogsService;

    // Get a list of all catalogs
    @GetMapping
    public Flux<List<CatalogsDto>> findAll() {
        log.info("CatalogsDto List, controller; fetch all categories");
        return catalogsService.findAll();
    }

    // Get detailed information of a specific catalogs
    @GetMapping("/{catalogId}")
    public ResponseEntity<CatalogsDto> findById(@PathVariable("catalogId")
                                               @NotBlank(message = "Input must not be blank!")
                                               @Valid final String catalogId) {
        log.info("CatalogsDto, resource; fetch catalogs by id");
        return ResponseEntity.ok(catalogsService.findById(Integer.parseInt(catalogId)));
    }
    // Create a new catalogs
    @PostMapping
    public ResponseEntity<CatalogsDto> save(@RequestBody
                                           @NotNull(message = "Input must not be NULL!")
                                           @Valid final CatalogsDto catalogDto) {
        log.info("CatalogsDto, resource; save catalogs");
        return ResponseEntity.ok(catalogsService.save(catalogDto));
    }

    // Update information of all catalogs
    @PutMapping
    public ResponseEntity<CatalogsDto> update(@RequestBody
                                             @NotNull(message = "Input must not be NULL!")
                                             @Valid final CatalogsDto catalogDto) {
        log.info("CatalogsDto, resource; update catalogs");
        return ResponseEntity.ok(catalogsService.update(catalogDto));
    }


    // Update information of a catalogs:
    @PutMapping("/{catalogId}")
    public ResponseEntity<CatalogsDto> update(@PathVariable("catalogId")
                                             @NotBlank(message = "Input must not be blank!")
                                             @Valid final String catalogId,
                                             @RequestBody
                                             @NotNull(message = "Input must not be NULL!")
                                             @Valid final CatalogsDto catalogDto) {
        log.info("CatalogsDto, resource; update catalogs with catalogId");
        return ResponseEntity.ok(catalogsService.update(Integer.parseInt(catalogId), catalogDto));
    }

    // Delete a catalogs
    @DeleteMapping("/{catalogId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("catalogId") final String catalogId) {
        log.info("Boolean, resource; delete catalogs by id");
        catalogsService.deleteById(Integer.parseInt(catalogId));
        return ResponseEntity.ok(true);
    }

}