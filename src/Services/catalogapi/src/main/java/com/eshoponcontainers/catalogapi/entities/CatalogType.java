package com.eshoponcontainers.catalogapi.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table(name = "CatalogType")
public class CatalogType {
    
    @Id
    @Column(value = "Id")
    private Integer id;

    @Column(value = "Type")
    private String type;
}