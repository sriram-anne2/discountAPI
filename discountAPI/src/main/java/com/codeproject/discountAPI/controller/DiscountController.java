package com.codeproject.discountAPI.controller;

import com.codeproject.discountAPI.domain.Discount;
import com.codeproject.discountAPI.domain.DiscountResponse;
import com.codeproject.discountAPI.domain.ItemRequest;
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
    public ArrayList<Discount> getAllDiscounts() throws ExecutionException, InterruptedException {

        return discountService.getAllDiscounts();
    }

    @PostMapping("")
    public Discount addNewDiscount(@RequestBody Discount discount) throws ExecutionException, InterruptedException {

        return discountService.createNewDiscount(discount);
    }

    @GetMapping("/code")
    public Discount getDiscountByCode(@RequestHeader String discountCode) throws ExecutionException, InterruptedException {

        return discountService.getDiscountByCode(discountCode);
    }

    @DeleteMapping()
    public void deleteDiscountByCode(@RequestHeader String discountCode){

        discountService.deleteDiscount(discountCode);
    }

    @GetMapping("/best")
    public DiscountResponse calculateBestDiscount(@RequestBody ArrayList<ItemRequest> itemRequests) throws ExecutionException, InterruptedException {

        return discountService.calculateBestDiscount(itemRequests);
    }

}
