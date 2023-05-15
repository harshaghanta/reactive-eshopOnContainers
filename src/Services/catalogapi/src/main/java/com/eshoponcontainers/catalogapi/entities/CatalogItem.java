package com.eshoponcontainers.catalogapi.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "Catalog")
public class CatalogItem {

    public static final CatalogItem NULL = new NullCatalogItem();
    
    @Id
    private Integer id;

    @Column(value = "Name")
    private String name;

    @Column(value = "Description")
    private String description;

    @Column(value = "Price")
    private Double price;

    @Column(value = "PictureFileName")
    private String pictureFileName;

    @Column(value = "AvailableStock")
    private int availableStock;

    @Column(value = "RestockThreshold")
    private int restockThreshold;

    @Column(value = "MaxStockThreshold")
    private int maxStockThreshold;

    @Column(value = "OnReorder")
    private boolean onReorder;

    @Column(value= "CatalogBrandId")
    private Integer catalogBrandId;
    
    @Column(value= "CatalogTypeId")
    private Integer catalogTypeId;

    @Transient
    private String pictureUri;

    public void fillProductUrl(String picBaseUrl) {
        this.pictureUri = picBaseUrl.replace("[0]", this.id.toString());
    }
}
