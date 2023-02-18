package com.codeproject.discountAPI.service;

import com.codeproject.discountAPI.domain.Discount;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

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
}
