package com.codeproject.discountAPI.controller;

import com.codeproject.discountAPI.domain.Item;
import com.codeproject.discountAPI.domain.ItemType;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebMvcTest(ItemController.class)
class ItemControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemController itemController;

    @Test
    void createNewItem() throws Exception{

        Item item = new Item();
        item.id = "123";
        item.cost = 50;
        item.itemType = ItemType.Clothes;

        given(itemController.createNewItem(item)).willReturn(item.id);

        mockMvc.perform(MockMvcRequestBuilders.
                        post("/item").
                        content(new Gson().toJson(item)).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void getAllItems() throws Exception{

        Item item = new Item();
        item.id = "123";
        item.cost = 50;
        item.itemType = ItemType.Clothes;

        ArrayList<Item> allItems = new ArrayList<>();
        allItems.add(item);

        given(itemController.getAllItems()).willReturn(allItems);

        mockMvc.perform(MockMvcRequestBuilders.get("/item")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.content().string(new Gson().toJson(allItems)));

    }

    @Test
    void getItemById() throws Exception{

        Item item = new Item();
        item.id = "123";
        item.cost = 50;
        item.itemType = ItemType.Clothes;


        given(itemController.getItemById(item.getId())).willReturn(item);

        mockMvc.perform(MockMvcRequestBuilders.get("/item/id").header("itemId", "123")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.content().string(new Gson().toJson(item)));

    }
}