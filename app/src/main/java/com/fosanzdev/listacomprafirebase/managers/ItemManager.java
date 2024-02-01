package com.fosanzdev.listacomprafirebase.managers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fosanzdev.listacomprafirebase.models.Category;
import com.fosanzdev.listacomprafirebase.models.Item;
import com.fosanzdev.listacomprafirebase.models.ItemViewFittable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemManager extends ArrayList<Item> {

    public List<? extends ItemViewFittable> filterByCategoryExcluding(Category category, List<String> itemsToExclude) {

        List<ItemViewFittable> filteredList = new ArrayList<>();
        for (Item item : this) {
            if (item.getCategoria().getNombre().equals(category.getNombre()) && !itemsToExclude.contains(item.getNombre())) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    public interface OnItemInteractionListener {
        void onDataChanged();
    }

    private FirebaseFirestore db;
    private OnItemInteractionListener listener;

    public ItemManager(FirebaseFirestore db, OnItemInteractionListener listener) {
        super();
        this.listener = listener;
        this.db = db;
        init();
    }

    private void init(){
        db.collection("items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e(ItemManager.class.getSimpleName(), "Listen failed.", error);
                    return;
                }

                //Clear the current list
                clear();

                //Add all the documents in the snapshot
                for (DocumentSnapshot doc : value){
                    Item item = doc.toObject(Item.class);
                    item.setId(doc.getId());
                    addSilent(item);
                }
                listener.onDataChanged();
            }
        });
    }

    private void addSilent(Item item) {
        super.add(item);
    }

    @Override
    public boolean add(Item item){
        final boolean[] result = {false};
        //Add to the database
        db.collection("items").add(item).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                //Add to the list
                item.setId(task.getResult().getId());
                addSilent(item);
                result[0] = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(ItemManager.class.getSimpleName(), "Error al añadir el item", e);
            }
        });
        return result[0];
    }

    @Override
    public boolean addAll(Collection<? extends Item> c) {
        final boolean[] result = {false};
        for (Item item : c) {
            result[0] = add(item);
        }
        return result[0];
    }

    @Override
    public boolean remove(Object o) {
        boolean result = super.remove(o);
        if (result) {
            db.collection("items").document(((Item) o).getId()).delete();
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        final boolean[] result = {false};
        for (Object item : c) {
            result[0] = remove(item);
        }
        return result[0];
    }

    @Override
    public Item set(int index, Item item) {
        Item oldItem = super.set(index, item);
        if (oldItem != null) {
            db.collection("items").document(oldItem.getId()).delete();
            db.collection("items").document(item.getId()).set(item);
        }
        return oldItem;
    }

    @Override
    public Item remove(int index){
        Item item = super.remove(index);
        if (item != null) {
            db.collection("items").document(item.getId()).delete();
        }
        return item;
    }

    public Item get(String id) {
        for (Item item : this) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public void update(Item item){
        db.collection("items").document(item.getId()).set(item);
    }
}
