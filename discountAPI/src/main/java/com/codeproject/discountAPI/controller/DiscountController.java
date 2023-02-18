package com.codeproject.discountAPI.controller;

import com.codeproject.discountAPI.domain.DiscountRawRequest;
import com.codeproject.discountAPI.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/discount")
public class DiscountController {


    @Autowired
    DiscountService discountService;

    @GetMapping("")
    public ArrayList<Object> getAllDiscounts() throws ExecutionException, InterruptedException {

        return discountService.getAllDiscountViews();
    }

    @PostMapping("")
    public String addNewDiscount(@RequestBody DiscountRawRequest discountRawRequest) throws ExecutionException, InterruptedException {

        return discountService.addDiscount(discountRawRequest);
    }

    @GetMapping("/code")
    public Object getDiscountByCode(@RequestHeader String discountCode) throws ExecutionException, InterruptedException {

        return discountService.getDiscountByCode(discountCode);
    }

    @DeleteMapping()
    public void deleteDiscountByCode(@RequestHeader String discountCode){

        discountService.deleteDiscount(discountCode);
    }

}
