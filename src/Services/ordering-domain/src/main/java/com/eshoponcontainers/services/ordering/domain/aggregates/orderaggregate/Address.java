package com.eshoponcontainers.services.ordering.domain.aggregates.orderaggregate;

import java.util.Arrays;
import java.util.Collection;

import com.eshoponcontainers.services.ordering.domain.ValueObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Address extends ValueObject {

    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    @Override
    protected Collection<Object> getEqualityComponents() {
        return Arrays.asList("Street", "City", "State", "Country", "ZipCode");
    }

}
