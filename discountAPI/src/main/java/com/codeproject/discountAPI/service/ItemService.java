package com.codeproject.discountAPI.service;

import com.codeproject.discountAPI.domain.Item;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class ItemService {

    public Item addNewItem(@NotNull Item item) throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> savedResult = firestore.collection("items")
                .document(item.id)
                .set(item);

        savedResult.get();

        return item;
    }
}
