package com.codeproject.discountAPI;

import com.codeproject.discountAPI.domain.*;
import com.codeproject.discountAPI.service.DiscountService;
import com.codeproject.discountAPI.service.ItemService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class DiscountApiApplication {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		SpringApplication.run(DiscountApiApplication.class, args);

		ItemService itemService = new ItemService();
		DiscountService discountService = new DiscountService();

		Item item = new Item();
		item.cost = 25;
		item.id = "test1";
		item.itemType = ItemType.Clothes;

		itemService.addNewItem(item);

		DiscByItemType discByItemType = new DiscByItemType();

		discByItemType.code = "DiscItemType1";
		discByItemType.rate = 15;
		discByItemType.itemType = ItemType.Clothes;

		DiscByCountOfItem discByCountOfItem = new DiscByCountOfItem();
		discByCountOfItem.code = "DiscCountItem1";
		discByCountOfItem.rate = 23;
		discByCountOfItem.itemCount = 5;
		discByCountOfItem.itemId = "test1";

		DiscByTotalCost discByTotalCost = new DiscByTotalCost();
		discByTotalCost.code = "DiscTotalCost1";
		discByTotalCost.rate = 20;
		discByTotalCost.applyAfterCost = 80;

		discountService.addNewDiscount(discByItemType);
		discountService.addNewDiscount(discByCountOfItem);
		discountService.addNewDiscount(discByTotalCost);


	}

}
