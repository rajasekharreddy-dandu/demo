package com.microservices.catalog_service.service;

import com.microservices.catalog_service.dto.request.CatalogsDto;
import reactor.core.publisher.Flux;
import java.util.List;


public interface CatalogsService {
    //    List<CatalogsDto> findAll();
    Flux<List<CatalogsDto>> findAll();

    CatalogsDto findById(final Integer catalogId);

    CatalogsDto save(final CatalogsDto catalogDto);

    CatalogsDto update(final CatalogsDto catalogDto);

    CatalogsDto update(final Integer catalogId, final CatalogsDto catalogDto);

    void deleteById(final Integer catalogId);
}

