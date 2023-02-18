package com.codeproject.discountAPI.service;

import com.codeproject.discountAPI.domain.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.apache.commons.lang3.AnnotationUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DiscountService {

    @Autowired
    ItemService itemService;

    public String addDiscount(DiscountRawRequest discountRawRequest) throws ExecutionException, InterruptedException {

        String discountCode = null;

        if (discountRawRequest.discountType == DiscountType.ByItemType) {
            DiscByItemType discByItemType = new DiscByItemType();
            discByItemType.code = discountRawRequest.code;
            discByItemType.rate = discountRawRequest.rate;
            discByItemType.discountType = DiscountType.ByItemType;

            if (discountRawRequest.itemType != null) {
                discByItemType.itemType = discountRawRequest.itemType;

                discountCode = createNewDiscount(discByItemType).code;
            }
        } else if (discountRawRequest.discountType == DiscountType.ByTotalCost) {
            DiscByTotalCost discByTotalCost = new DiscByTotalCost();
            discByTotalCost.code = discountRawRequest.code;
            discByTotalCost.rate = discountRawRequest.rate;
            discByTotalCost.discountType = DiscountType.ByTotalCost;

            if (discountRawRequest.applyAfterCost != 0) {
                discByTotalCost.applyAfterCost = discountRawRequest.applyAfterCost;

                discountCode = createNewDiscount(discByTotalCost).code;

            }
        } else if (discountRawRequest.discountType == DiscountType.ByCountOfItems) {
            DiscByCountOfItem discByCountOfItem = new DiscByCountOfItem();
            discByCountOfItem.code = discountRawRequest.code;
            discByCountOfItem.rate = discountRawRequest.rate;
            discByCountOfItem.discountType = discountRawRequest.discountType;

            if (discountRawRequest.itemId != null && discountRawRequest.itemCount != 0) {
                discByCountOfItem.itemId = discountRawRequest.itemId;
                discByCountOfItem.itemCount = discountRawRequest.itemCount;

                discountCode = createNewDiscount(discByCountOfItem).code;
            }
        }

        return discountCode;
    }

    private Discount createNewDiscount(Discount discount) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> savedResult = firestore.collection("discounts")
                .document(discount.code)
                .set(discount);

        savedResult.get();

        return discount;

    }

    public DiscountResponse calculateBestDiscount(ArrayList<ItemRequest> itemRequests) throws ExecutionException, InterruptedException {

        ArrayList<Discount> discounts = getAllDiscounts();
        DiscByItemType discByItemType = null;
        DiscByTotalCost discByTotalCost = null;
        DiscByCountOfItem discByCountOfItem = null;
        for (Discount disc : discounts) {
            if (disc.discountType == DiscountType.ByItemType) {
                discByItemType = (DiscByItemType) getDiscountByCode(disc.code);
            } else if (disc.discountType == DiscountType.ByTotalCost) {
                discByTotalCost = (DiscByTotalCost) getDiscountByCode(disc.code);
            } else if (disc.discountType == DiscountType.ByCountOfItems) {
                discByCountOfItem = (DiscByCountOfItem) getDiscountByCode(disc.code);
            }
        }

        DiscountResponse discountResponse = new DiscountResponse();
        for (ItemRequest itr : itemRequests) {
            Item item = itemService.getItemById(itr.getItemId());
            double bestPriceForItem = item.cost;
            String bestDiscountCode = null;

            if (discByItemType != null && discByItemType.itemType == item.itemType) {
                double discountedPrice = item.cost * (100 - discByItemType.rate) / 100;
                if (discountedPrice < bestPriceForItem) {
                    bestPriceForItem = discountedPrice;
                    bestDiscountCode = discByItemType.code;
                }
            }
            if (discByCountOfItem != null && itr.getItemQuantity() >= discByCountOfItem.itemCount) {
                double discountedPrice = item.cost * (100 - discByCountOfItem.rate) / 100;
                if (discountedPrice < bestPriceForItem) {
                    bestPriceForItem = discountedPrice;
                    bestDiscountCode = discByCountOfItem.code;
                }
            }
            if (discByTotalCost != null && item.cost >= discByTotalCost.applyAfterCost){
                double discountedPrice = item.cost * (100 - discByTotalCost.rate) / 100;
                if (discountedPrice < bestPriceForItem) {
                    bestPriceForItem = discountedPrice;
                    bestDiscountCode = discByTotalCost.code;
                }
            }
            discountResponse.discountCode = bestDiscountCode;
            discountResponse.finalCost = bestPriceForItem * itr.itemQuantity;
        }
        return discountResponse;
    }

    public ArrayList<Object> getAllDiscountViews() throws ExecutionException, InterruptedException {

        List<QueryDocumentSnapshot> rawDiscounts = getAllRawDiscounts();

        ArrayList<Object> discountViews = new ArrayList<>();
        for (DocumentSnapshot doc : rawDiscounts) {
            Discount discount = doc.toObject(Discount.class);
            if (discount == null) continue;
            if (discount.discountType == DiscountType.ByItemType) {
                discountViews.add(doc.toObject(DiscByItemType.class));
            } else if (discount.discountType == DiscountType.ByTotalCost) {
                discountViews.add(doc.toObject(DiscByTotalCost.class));
            } else if (discount.discountType == DiscountType.ByCountOfItems) {
                discountViews.add(doc.toObject(DiscByCountOfItem.class));
            }
        }
        return discountViews;
    }

    private List<QueryDocumentSnapshot> getAllRawDiscounts() throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();

        return firestore.collection("discounts").get().get().getDocuments();
    }

    public ArrayList<Discount> getAllDiscounts() throws ExecutionException, InterruptedException {

        ArrayList<Discount> discounts = new ArrayList<>();
        List<QueryDocumentSnapshot> rawDiscounts = getAllRawDiscounts();

        for (DocumentSnapshot doc : rawDiscounts) {
            discounts.add(doc.toObject(Discount.class));
        }
        return discounts;
    }


    public Object getDiscountByCode(@NotNull String discountCode) throws ExecutionException, InterruptedException {

        DocumentSnapshot doc = getRawDiscountByCode(discountCode);
        Object discount = null;
        Discount disc = null;
        if (doc != null) {
            disc = doc.toObject(Discount.class);
        }
        if (disc != null) {
            if (disc.discountType == DiscountType.ByItemType) {
                discount = doc.toObject(DiscByItemType.class);
            } else if (disc.discountType == DiscountType.ByTotalCost) {
                discount = doc.toObject(DiscByTotalCost.class);
            } else if (disc.discountType == DiscountType.ByCountOfItems) {
                discount = doc.toObject(DiscByCountOfItem.class);
            }
        }

        return discount;
    }

    private DocumentSnapshot getRawDiscountByCode(@NotNull String discountCode) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentSnapshot discountDocument = firestore.collection("discounts").document(discountCode).get().get();

        return discountDocument.exists() ? discountDocument : null;
    }

    public void deleteDiscount(@NotNull String discountCode) {
        Firestore firestore = FirestoreClient.getFirestore();

        firestore.collection("discounts").document(discountCode).delete();
    }
}
