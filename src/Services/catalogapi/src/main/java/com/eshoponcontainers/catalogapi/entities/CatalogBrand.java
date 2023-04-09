package com.eshoponcontainers.catalogapi.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table(name = "CatalogBrand")
public class CatalogBrand {
    
    @Id
    private int id;

    private String brand;
}
