package com.fosanzdev.listacomprafirebase.managers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fosanzdev.listacomprafirebase.models.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;

public class CategoryManager extends ArrayList<Category> {

    public interface OnCategoryInteractionListener {
        void onDataChanged();
    }

    private FirebaseFirestore db;
    private OnCategoryInteractionListener listener;

    public CategoryManager(FirebaseFirestore db, OnCategoryInteractionListener listener) {
        super();
        this.listener = listener;
        this.db = db;
        init();
    }

    private void init(){
        db.collection("categorias").addSnapshotListener((value, error) -> {
            if (error != null){
                Log.e(CategoryManager.class.getSimpleName(), "Listen failed.", error);
                return;
            }

            //Clear the current list
            clear();

            //Add all the documents in the snapshot
            for (DocumentSnapshot doc : value){
                Category category = doc.toObject(Category.class);
                category.setId(doc.getId());
                addSilent(category);
            }
            listener.onDataChanged();
        });
    }

    private void addSilent(Category category) {
        super.add(category);
    }

    @Override
    public boolean add(Category category){
        final boolean[] result = {false};
        //Add to the database
        db.collection("categorias").add(category).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                //Add to the list
                category.setId(task.getResult().getId());
                addSilent(category);
                result[0] = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                result[0] = false;
                Log.e(CategoryManager.class.getSimpleName(), "Error al añadir la categoría", e);
            }
        });

        return result[0];
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Category> c) {
        final boolean[] result = {false};
        for (Category category : c) {
            result[0] = add(category);
        }
        return result[0];
    }

    @Override
    public boolean remove(Object o) {
        boolean result = super.remove(o);
        if (result) {
            db.collection("categorias").document(((Category) o).getId()).delete();
        }
        return result;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        boolean result = super.removeAll(c);
        if (result) {
            for (Object o : c) {
                db.collection("categorias").document(((Category) o).getId()).delete();
            }
        }
        return result;
    }

    @Override
    public Category set(int index, Category category) {
        Category oldCategory = super.set(index, category);
        db.collection("categorias").document(oldCategory.getId()).set(category);
        return oldCategory;
    }

    @Override
    public Category remove(int index) {
        Category category = super.remove(index);
        db.collection("categorias").document(category.getId()).delete();
        return category;
    }

    public Category get(String id) {
        for (Category category : this) {
            if (category.getId().equals(id)) {
                return category;
            }
        }
        return null;
    }

    public void update(Category category) {
        db.collection("categorias").document(category.getId()).set(category);
    }
}
