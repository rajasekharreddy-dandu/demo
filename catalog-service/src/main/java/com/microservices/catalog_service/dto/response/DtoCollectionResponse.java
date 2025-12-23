package com.microservices.catalog_service.dto.response;

//•	package com.microservices.catalog_service.dto.response.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DtoCollectionResponse<T> {
    private Collection<T> collection;
}

