package com.codeproject.discountAPI;

import com.codeproject.discountAPI.domain.*;
import com.codeproject.discountAPI.service.DiscountService;
import com.codeproject.discountAPI.service.ItemService;
import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class DiscountApiApplication {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		SpringApplication.run(DiscountApiApplication.class, args);

//		ItemService itemService = new ItemService();
//		DiscountService discountService = new DiscountService();
//
//		Item item = new Item();
//		item.cost = 25;
//		item.id = "test1";
//		item.itemType = ItemType.Clothes;
//
//		itemService.createNewItem(item);
//
//		DiscByItemType discByItemType = new DiscByItemType();
//
//		discByItemType.code = "DiscItemType1";
//		discByItemType.rate = 15;
//		discByItemType.discountType = DiscountType.ByItemType;
//		discByItemType.itemType = ItemType.Clothes;
//
//		DiscByCountOfItem discByCountOfItem = new DiscByCountOfItem();
//		discByCountOfItem.code = "DiscCountItem1";
//		discByCountOfItem.rate = 23;
//		discByCountOfItem.discountType = DiscountType.ByCountOfItems;
//		discByCountOfItem.itemCount = 5;
//		discByCountOfItem.itemId = "test1";
//
//		DiscByTotalCost discByTotalCost = new DiscByTotalCost();
//		discByTotalCost.code = "DiscTotalCost1";
//		discByTotalCost.rate = 20;
//		discByTotalCost.discountType = DiscountType.ByTotalCost;
//		discByTotalCost.applyAfterCost = 80;
//
//		discountService.createNewDiscount(discByItemType);
//		discountService.createNewDiscount(discByCountOfItem);
//		discountService.createNewDiscount(discByTotalCost);
//
//		ArrayList<Object> discountViews = discountService.getAllDiscountViews();
//
//		System.out.println(discountViews.size());
//
//		Object discount = discountService.getDiscountByCode("DiscTotalCost1");
//		System.out.println(new Gson().toJson(discount));
//
//		discountService.deleteDiscount("DiscTotalCost1");

	}

}
