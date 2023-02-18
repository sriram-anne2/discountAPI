package com.codeproject.discountAPI.service;

import com.codeproject.discountAPI.domain.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class DiscountService {

    @Autowired
    ItemService itemService;


    public Discount createNewDiscount(Discount discount) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> savedResult = firestore.collection("discounts")
                .document(discount.code)
                .set(discount);

        savedResult.get();

        return discount;

    }
    public ArrayList<Discount> getAllDiscounts() throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();
        ArrayList<Discount> discounts = new ArrayList<>();
        List<QueryDocumentSnapshot> rawDiscounts = firestore.collection("discounts").get().get().getDocuments();

        for (DocumentSnapshot doc : rawDiscounts) {
            discounts.add(doc.toObject(Discount.class));
        }
        return discounts;
    }

    public Discount getDiscountByCode(@NotNull String discountCode) throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();

        DocumentSnapshot doc = firestore.collection("discounts").document(discountCode).get().get();
        Discount disc = null;

        if (doc.exists()){
            disc = doc.toObject(Discount.class);
        }

        return disc;
    }

    public void deleteDiscount(@NotNull String discountCode) {
        Firestore firestore = FirestoreClient.getFirestore();

        firestore.collection("discounts").document(discountCode).delete();
    }

    public DiscountResponse calculateBestDiscount(ArrayList<ItemRequest> itemRequests) throws ExecutionException, InterruptedException {

        ArrayList<Discount> discounts = getAllDiscounts();

        HashMap<String, Double> discMapper = new HashMap<String, Double>();
        String bestDiscountCode = null;
        for (Discount discount : discounts){

            double bestPriceForItem;
            double finalBill = 0;
            for (ItemRequest itr : itemRequests){
                Item item = itemService.getItemById(itr.getItemId());
                bestPriceForItem = item.cost;
                if (discount.itemType != null && discount.itemType == item.itemType){
                    double discountedPrice = item.cost * (100 - discount.rate) / 100;
                    if (discountedPrice < bestPriceForItem) {
                        bestPriceForItem = discountedPrice;
                        bestDiscountCode = discount.code;
                    }
                } else if (discount.applyAfterCost != null && item.cost >= discount.applyAfterCost) {
                    double discountedPrice = item.cost * (100 - discount.rate) / 100;
                    if (discountedPrice < bestPriceForItem) {
                        bestPriceForItem = discountedPrice;
                        bestDiscountCode = discount.code;
                    }
                } else if (discount.itemId != null && discount.itemCount != null &&
                        Objects.equals(item.id, discount.itemId) && itr.getItemQuantity() >= discount.itemCount) {
                    double discountedPrice = item.cost * (100 - discount.rate) / 100;
                    if (discountedPrice < bestPriceForItem) {
                        bestPriceForItem = discountedPrice;
                        bestDiscountCode = discount.code;
                    }
                } else {
                    bestDiscountCode = "";
                }

                finalBill += bestPriceForItem * itr.itemQuantity;
            }

            discMapper.put(bestDiscountCode, finalBill);
        }

        Map.Entry<String, Double> min = null;
        for (Map.Entry<String, Double> entry : discMapper.entrySet()){
            if (min == null || min.getValue() > entry.getValue()){
                min = entry;
            }
        }

        DiscountResponse discountResponse = new DiscountResponse();
        discountResponse.discountCode = min.getKey();
        discountResponse.finalCost = min.getValue();

        return discountResponse;
    }


    // OLD CODE
//    public String addDiscountOld(DiscountRawRequest discountRawRequest) throws ExecutionException, InterruptedException {
//
//        String discountCode = null;
//
//        if (discountRawRequest.discountType == DiscountType.ByItemType) {
//            DiscByItemType discByItemType = new DiscByItemType();
//            discByItemType.code = discountRawRequest.code;
//            discByItemType.rate = discountRawRequest.rate;
//            discByItemType.discountType = DiscountType.ByItemType;
//
//            if (discountRawRequest.itemType != null) {
//                discByItemType.itemType = discountRawRequest.itemType;
//
//                discountCode = createNewDiscount(discByItemType).code;
//            }
//        } else if (discountRawRequest.discountType == DiscountType.ByTotalCost) {
//            DiscByTotalCost discByTotalCost = new DiscByTotalCost();
//            discByTotalCost.code = discountRawRequest.code;
//            discByTotalCost.rate = discountRawRequest.rate;
//            discByTotalCost.discountType = DiscountType.ByTotalCost;
//
//            if (discountRawRequest.applyAfterCost != 0) {
//                discByTotalCost.applyAfterCost = discountRawRequest.applyAfterCost;
//
//                discountCode = createNewDiscount(discByTotalCost).code;
//
//            }
//        } else if (discountRawRequest.discountType == DiscountType.ByCountOfItems) {
//            DiscByCountOfItem discByCountOfItem = new DiscByCountOfItem();
//            discByCountOfItem.code = discountRawRequest.code;
//            discByCountOfItem.rate = discountRawRequest.rate;
//            discByCountOfItem.discountType = discountRawRequest.discountType;
//
//            if (discountRawRequest.itemId != null && discountRawRequest.itemCount != 0) {
//                discByCountOfItem.itemId = discountRawRequest.itemId;
//                discByCountOfItem.itemCount = discountRawRequest.itemCount;
//
//                discountCode = createNewDiscount(discByCountOfItem).code;
//            }
//        }
//
//        return discountCode;
//    }
//
//    public DiscountResponse calculateBestDiscountOld(ArrayList<ItemRequest> itemRequests) throws ExecutionException, InterruptedException {
//
//        ArrayList<Discount> discounts = getAllDiscounts();
//        DiscByItemType discByItemType = null;
//        DiscByTotalCost discByTotalCost = null;
//        DiscByCountOfItem discByCountOfItem = null;
//        for (Discount disc : discounts) {
//            if (disc.discountType == DiscountType.ByItemType) {
//                discByItemType = (DiscByItemType) getDiscountByCodeOld(disc.code);
//            } else if (disc.discountType == DiscountType.ByTotalCost) {
//                discByTotalCost = (DiscByTotalCost) getDiscountByCodeOld(disc.code);
//            } else if (disc.discountType == DiscountType.ByCountOfItems) {
//                discByCountOfItem = (DiscByCountOfItem) getDiscountByCodeOld(disc.code);
//            }
//        }
//
//        DiscountResponse discountResponse = new DiscountResponse();
//        for (ItemRequest itr : itemRequests) {
//            Item item = itemService.getItemById(itr.getItemId());
//            double bestPriceForItem = item.cost;
//            String bestDiscountCode = null;
//
//            if (discByItemType != null && discByItemType.itemType == item.itemType) {
//                double discountedPrice = item.cost * (100 - discByItemType.rate) / 100;
//                if (discountedPrice < bestPriceForItem) {
//                    bestPriceForItem = discountedPrice;
//                    bestDiscountCode = discByItemType.code;
//                }
//            }
//            if (discByCountOfItem != null && itr.getItemQuantity() >= discByCountOfItem.itemCount) {
//                double discountedPrice = item.cost * (100 - discByCountOfItem.rate) / 100;
//                if (discountedPrice < bestPriceForItem) {
//                    bestPriceForItem = discountedPrice;
//                    bestDiscountCode = discByCountOfItem.code;
//                }
//            }
//            if (discByTotalCost != null && item.cost >= discByTotalCost.applyAfterCost){
//                double discountedPrice = item.cost * (100 - discByTotalCost.rate) / 100;
//                if (discountedPrice < bestPriceForItem) {
//                    bestPriceForItem = discountedPrice;
//                    bestDiscountCode = discByTotalCost.code;
//                }
//            }
//            discountResponse.discountCode = bestDiscountCode;
//            discountResponse.finalCost = bestPriceForItem * itr.itemQuantity;
//        }
//        return discountResponse;
//    }
//
//    public ArrayList<Object> getAllDiscountViewsOld() throws ExecutionException, InterruptedException {
//
//        List<QueryDocumentSnapshot> rawDiscounts = getAllRawDiscountsOld();
//
//        ArrayList<Object> discountViews = new ArrayList<>();
//        for (DocumentSnapshot doc : rawDiscounts) {
//            Discount discount = doc.toObject(Discount.class);
//            if (discount == null) continue;
//            if (discount.discountType == DiscountType.ByItemType) {
//                discountViews.add(doc.toObject(DiscByItemType.class));
//            } else if (discount.discountType == DiscountType.ByTotalCost) {
//                discountViews.add(doc.toObject(DiscByTotalCost.class));
//            } else if (discount.discountType == DiscountType.ByCountOfItems) {
//                discountViews.add(doc.toObject(DiscByCountOfItem.class));
//            }
//        }
//        return discountViews;
//    }
//
//    private List<QueryDocumentSnapshot> getAllRawDiscountsOld() throws ExecutionException, InterruptedException {
//
//        Firestore firestore = FirestoreClient.getFirestore();
//
//        return firestore.collection("discounts").get().get().getDocuments();
//    }
//    public Object getDiscountByCodeOld(@NotNull String discountCode) throws ExecutionException, InterruptedException {
//
//        DocumentSnapshot doc = getRawDiscountByCodeOld(discountCode);
//        Object discount = null;
//        Discount disc = null;
//        if (doc != null) {
//            disc = doc.toObject(Discount.class);
//        }
//        if (disc != null) {
//            if (disc.discountType == DiscountType.ByItemType) {
//                discount = doc.toObject(DiscByItemType.class);
//            } else if (disc.discountType == DiscountType.ByTotalCost) {
//                discount = doc.toObject(DiscByTotalCost.class);
//            } else if (disc.discountType == DiscountType.ByCountOfItems) {
//                discount = doc.toObject(DiscByCountOfItem.class);
//            }
//        }
//
//        return discount;
//    }
//
//    private DocumentSnapshot getRawDiscountByCodeOld(String discountCode) throws ExecutionException, InterruptedException {
//        Firestore firestore = FirestoreClient.getFirestore();
//        DocumentSnapshot discountDocument = firestore.collection("discounts").document(discountCode).get().get();
//
//        return discountDocument.exists() ? discountDocument : null;
//    }


}
