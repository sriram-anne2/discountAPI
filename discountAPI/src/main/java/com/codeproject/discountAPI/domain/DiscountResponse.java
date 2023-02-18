package com.codeproject.discountAPI.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class DiscountResponse {

    public String discountCode;
    public double finalCost;
}
