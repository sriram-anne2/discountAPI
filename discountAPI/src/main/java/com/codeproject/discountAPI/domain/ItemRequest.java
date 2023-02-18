package com.codeproject.discountAPI.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ItemRequest {

    public String itemId;
    public int itemQuantity;
}
