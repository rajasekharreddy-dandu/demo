package com.microservices.catalog_service.entity;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"category"})
@Data
@Builder
@Entity
@Table(name = "catalogs")
@EntityListeners(AuditingEntityListener.class)
public final class Catalog extends AbstractMappedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "catalog_id", unique = true, nullable = false, updatable = false)
    private Integer catalogId;

    @Column(name = "catalog_title" ,unique = true)
    private String catalogTitle;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(unique = true)
    private String sku;

    @Column(name = "price_unit", columnDefinition = "decimal")
    private Double priceUnit;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

}

