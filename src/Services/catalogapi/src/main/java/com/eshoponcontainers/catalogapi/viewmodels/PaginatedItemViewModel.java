package com.eshoponcontainers.catalogapi.viewmodels;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginatedItemViewModel<T> {
    
    private Integer pageIndex;
    private Integer pageSize;
    private Long count;
    private List<T> data;
}
