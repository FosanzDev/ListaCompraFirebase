package com.fosanzdev.listacomprafirebase.managers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fosanzdev.listacomprafirebase.models.Item;
import com.fosanzdev.listacomprafirebase.models.ShoppingList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;

public class ShoppingListManager extends ArrayList<ShoppingList> {

    public interface OnShoppingListInteractionListener {
        void onDataChanged();

        void onShoppingListChanged(ShoppingList shoppingList);
    }

    private FirebaseFirestore db;
    private OnShoppingListInteractionListener listener;

    public ShoppingListManager(FirebaseFirestore db, OnShoppingListInteractionListener listener) {
        super();
        this.db = db;
        this.listener = listener;
        init();
    }

    private void init() {
        db.collection("listas").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(ShoppingListManager.class.getSimpleName(), "Listen failed.", e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            ShoppingList newShoppingList = dc.getDocument().toObject(ShoppingList.class);
                            newShoppingList.setId(dc.getDocument().getId());
                            addSilent(newShoppingList);
                            listener.onDataChanged();
                            break;
                        case MODIFIED:
                            ShoppingList modifiedShoppingList = dc.getDocument().toObject(ShoppingList.class);
                            modifiedShoppingList.setId(dc.getDocument().getId());
                            int index = getPosition(modifiedShoppingList.getId());
                            if (index != -1) {
                                set(index, modifiedShoppingList);
                                listener.onShoppingListChanged(modifiedShoppingList);
                            }
                            break;
                        case REMOVED:
                            String removedId = dc.getDocument().getId();
                            int removedIndex = getPosition(removedId);
                            if (removedIndex != -1) {
                                remove(removedIndex);
                                listener.onDataChanged();
                            }
                            break;
                    }
                }
            }
        });
    }

    private void addSilent(ShoppingList shoppingList) {
        super.add(shoppingList);
    }

    @Override
    public boolean add(ShoppingList shoppingList) {
        addSilent(shoppingList);
        final boolean[] result = {false};
        //Add to the database
        db.collection("listas").add(shoppingList).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                //Add to the list
                shoppingList.setId(task.getResult().getId());
                result[0] = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(ShoppingListManager.class.getSimpleName(), "Error al añadir la lista", e);
            }
        });
        return result[0];
    }

    @Override
    public boolean addAll(Collection<? extends ShoppingList> c) {
        final boolean[] result = {false};
        for (ShoppingList shoppingList : c) {
            result[0] = add(shoppingList);
        }
        return result[0];
    }

    @Override
    public boolean remove(Object o) {
        boolean result = super.remove(o);
        if (result) {
            db.collection("listas").document(((ShoppingList) o).getId()).delete();
        }
        return result;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        final boolean[] result = {false};
        for (Object o : c) {
            result[0] = remove(o);
        }
        return result[0];
    }

    @Override
    public ShoppingList set(int index, ShoppingList element) {
        ShoppingList shoppingList = super.set(index, element);
        db.collection("listas").document(shoppingList.getId()).set(element);
        return shoppingList;
    }

    @Override
    public ShoppingList remove(int index) {
        ShoppingList shoppingList = super.remove(index);
        db.collection("listas").document(shoppingList.getId()).delete();
        return shoppingList;
    }

    public ShoppingList get(String id) {
        for (ShoppingList shoppingList : this) {
            if (shoppingList.getId().equals(id)) {
                return shoppingList;
            }
        }
        return null;
    }

    public int getPosition(String id) {
        for (int i = 0; i < size(); i++) {
            if (get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public void deleteItem(ShoppingList shoppingList, Item item) {
        shoppingList.getItems().remove(item);
        db.collection("listas").document(shoppingList.getId()).set(shoppingList);
    }

    public void addItem(ShoppingList shoppingList, Item item) {
        shoppingList.getItems().add(item);
        db.collection("listas").document(shoppingList.getId()).set(shoppingList);
    }

    public void addItem(ShoppingList shoppingList, String itemId) {
    db.collection("items").document(itemId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot itemDocument = task.getResult();
                if (itemDocument.exists()) {
                    Item item = itemDocument.toObject(Item.class);
                    if (item != null) {
                        item.setId(itemDocument.getId()); // Set the ID for the Item
                        shoppingList.getItems().add(item);
                        db.collection("listas").document(shoppingList.getId()).set(shoppingList);
                    }
                }
            }
        }
    });
}

    public void update(ShoppingList shoppingList) {
        db.collection("listas").document(shoppingList.getId()).set(shoppingList);
    }
}
