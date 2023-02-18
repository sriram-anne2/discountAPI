package com.codeproject.discountAPI.controller;

import com.codeproject.discountAPI.domain.Item;
import com.codeproject.discountAPI.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    ItemService itemService;

    @PostMapping("")
    public String createNewItem(@RequestBody Item item) throws ExecutionException, InterruptedException {

        return itemService.createNewItem(item).id;
    }

    @GetMapping("")
    public ArrayList<Item> getAllItems() throws ExecutionException, InterruptedException {

        return itemService.getAllItems();
    }

    @GetMapping("/id")
    public Item getItemById(@RequestHeader String itemId) throws ExecutionException, InterruptedException {

        return itemService.getItemById(itemId);
    }
}
