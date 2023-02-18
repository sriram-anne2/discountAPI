package com.codeproject.discountAPI.service;

import com.codeproject.discountAPI.domain.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DiscountService {


    public Discount addNewDiscount(Discount discount) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> savedResult = firestore.collection("discounts")
                .document(discount.code)
                .set(discount);

        savedResult.get();

        return discount;

    }

    public ArrayList<Object> getAllDiscountViews() throws ExecutionException, InterruptedException {

        List<QueryDocumentSnapshot> rawDiscounts = getAllDiscounts();

        ArrayList<Object> discountViews = new ArrayList<>();
        for (DocumentSnapshot doc : rawDiscounts){
            Discount discount = doc.toObject(Discount.class);
            if (discount == null) continue;
            if (discount.discountType == DiscountType.ByItemType){
                discountViews.add(doc.toObject(DiscByItemType.class));
            } else if (discount.discountType == DiscountType.ByTotalCost){
                discountViews.add(doc.toObject(DiscByTotalCost.class));
            } else if (discount.discountType == DiscountType.ByCountOfItems){
                discountViews.add(doc.toObject(DiscByCountOfItem.class));
            }
        }
        return discountViews;
    }

    private List<QueryDocumentSnapshot> getAllDiscounts() throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();

        return firestore.collection("discounts").get().get().getDocuments();
    }


    public Object getDiscountByCode(@NotNull String discountCode) throws ExecutionException, InterruptedException {

        DocumentSnapshot doc = getRawDiscountByCode(discountCode);
        Object discount = null;
        Discount disc = null;
        if (doc != null){
            disc = doc.toObject(Discount.class);
        }
        if (disc != null) {
            if (disc.discountType == DiscountType.ByItemType){
                discount = doc.toObject(DiscByItemType.class);
            } else if (disc.discountType == DiscountType.ByTotalCost){
                discount = doc.toObject(DiscByTotalCost.class);
            } else if (disc.discountType == DiscountType.ByCountOfItems){
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

    public void deleteDiscount(@NotNull String discountCode){
        Firestore firestore = FirestoreClient.getFirestore();

        firestore.collection("discounts").document(discountCode).delete();
    }
}
