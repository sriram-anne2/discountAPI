package com.codeproject.discountAPI.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class DiscountRawRequest {
    public String code;
    public double rate;
    public DiscountType discountType;
    public ItemType itemType;
    public String itemId;
    public int itemCount;
    public double applyAfterCost;
}
