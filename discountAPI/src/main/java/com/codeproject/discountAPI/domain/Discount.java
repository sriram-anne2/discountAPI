package com.codeproject.discountAPI.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Discount {
    public String code;
    public Double rate;
    public ItemType itemType;
    public String itemId;
    public Double itemCount;
    public Double applyAfterCost;

}

