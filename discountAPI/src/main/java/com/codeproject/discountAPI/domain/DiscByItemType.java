package com.codeproject.discountAPI.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class DiscByItemType extends Discount{
    public ItemType itemType;
}
