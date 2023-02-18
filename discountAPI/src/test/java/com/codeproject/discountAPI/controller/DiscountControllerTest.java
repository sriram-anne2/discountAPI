package com.codeproject.discountAPI.controller;

import com.codeproject.discountAPI.domain.*;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebMvcTest(DiscountController.class)
class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscountController discountController;

    @MockBean
    private ItemController itemController;

    @Test
    void getAllDiscounts() throws Exception{

        Discount discount = new Discount();
        discount.code = "ABC";
        discount.rate = 25.0;
        discount.applyAfterCost = 120.0;

        Discount discount2 = new Discount();
        discount2.code = "CDE";
        discount2.rate = 15.0;
        discount2.itemCount = 3.0;
        discount2.itemId = "123";

        ArrayList<Discount> allDiscounts = new ArrayList<>();
        allDiscounts.add(discount);
        allDiscounts.add(discount2);

        given(discountController.getAllDiscounts()).willReturn(allDiscounts);

        mockMvc.perform(MockMvcRequestBuilders.get("/discount")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.content().string(new Gson().toJson(allDiscounts)));


    }

    @Test
    void getDiscountByCode() throws Exception{

        Discount discount = new Discount();
        discount.code = "ABC";
        discount.rate = 25.0;
        discount.applyAfterCost = 120.0;

        given(discountController.getDiscountByCode(discount.getCode())).willReturn(discount);

        mockMvc.perform(MockMvcRequestBuilders.get("/discount/code").header("discountCode", discount.getCode())).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.content().string(new Gson().toJson(discount)));


    }

    @Test
    void calculateBestDiscount() throws Exception{

        Item item = new Item();
        item.id = "123";
        item.cost = 50;
        item.itemType = ItemType.Clothes;

        Item item2 = new Item();
        item2.id = "456";
        item2.cost = 300;
        item2.itemType = ItemType.Electronics;

        Discount discount = new Discount();
        discount.code = "ABC";
        discount.rate = 10.0;
        discount.itemType = ItemType.Clothes;

        Discount discount2 = new Discount();
        discount2.code = "CDE";
        discount2.rate = 15.0;
        discount2.applyAfterCost = 100.0;

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.itemId = item.getId();
        itemRequest.itemQuantity = 1;

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.itemId = item2.getId();
        itemRequest2.itemQuantity = 1;

        ArrayList<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        itemRequests.add(itemRequest2);


        DiscountResponse discountResponse = new DiscountResponse();
        discountResponse.finalCost = 305;
        discountResponse.discountCode = "CDE";

        String jsonResp = new Gson().toJson(discountResponse);

        given(discountController.calculateBestDiscount(itemRequests)).willReturn(discountResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/discount/best").
                        content(new Gson().toJson(itemRequests)).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(MockMvcResultMatchers.status().isOk());
////                andExpect(MockMvcResultMatchers.content().string(jsonResp));
//
//        MockHttpServletResponse resp = result.getResponse();
//        String actualResp = result.getResponse().getContentAsString();
//
//        Assert.assertEquals(jsonResp, actualResp);


    }
}