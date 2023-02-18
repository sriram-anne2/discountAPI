package com.codeproject.discountAPI.service;

import com.codeproject.discountAPI.domain.Item;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ItemService {

    public Item createNewItem(@NotNull Item item) throws ExecutionException, InterruptedException {

        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> savedResult = firestore.collection("items")
                .document(item.id)
                .set(item);

        savedResult.get();

        return item;
    }

    public ArrayList<Item> getAllItems() throws ExecutionException, InterruptedException {

        ArrayList<Item> items = new ArrayList<>();
        Firestore firestore = FirestoreClient.getFirestore();
        List<QueryDocumentSnapshot> itemsRaw = firestore.collection("items").get().get().getDocuments();

        for(DocumentSnapshot doc : itemsRaw){
            items.add(doc.toObject(Item.class));
        }

        return items;
    }

    public Item getItemById(@NotNull String itemId) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentSnapshot itemDocument = firestore.collection("items").document(itemId).get().get();
        Item item = null;

        if (itemDocument.exists()){
            item = itemDocument.toObject(Item.class);
        }
        return item;
    }
}
