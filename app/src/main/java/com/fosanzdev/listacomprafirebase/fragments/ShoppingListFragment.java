package com.fosanzdev.listacomprafirebase.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fosanzdev.listacomprafirebase.activities.ItemSelectionActivity;
import com.fosanzdev.listacomprafirebase.R;
import com.fosanzdev.listacomprafirebase.adapters.ItemGridAdapter;
import com.fosanzdev.listacomprafirebase.managers.ShoppingListManager;
import com.fosanzdev.listacomprafirebase.models.Item;
import com.fosanzdev.listacomprafirebase.models.ItemViewFittable;
import com.fosanzdev.listacomprafirebase.models.ShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ShoppingListFragment extends Fragment implements ShoppingListManager.OnShoppingListInteractionListener, ItemGridAdapter.ItemViewHolder.IOnItemClickedListener {

    private Context context;
    private ShoppingList shoppingList;
    private RecyclerView rvShoppingList;
    private ItemGridAdapter adapter;
    private ShoppingListManager manager;
    private ActivityResultLauncher<Intent> selectItemActivityResultLauncher;
    private View v;

    //Empty contstructor
    public ShoppingListFragment(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
        this.manager = new ShoppingListManager(FirebaseFirestore.getInstance(), this);
    }

    public ShoppingListFragment() {
        this.manager = new ShoppingListManager(FirebaseFirestore.getInstance(), this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.shopping_list_detail_fragment, container, false);

        selectItemActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                String selectedItemId = data.getStringExtra("selectedItemId");
                                manager.addItem(shoppingList, selectedItemId);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
        );
        if (shoppingList != null) {
            onDataChanged();

            FloatingActionButton fab = v.findViewById(R.id.fabAddItem);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSelectItemActivity();
                }
            });
        }
        return v;
    }

    private void startSelectItemActivity() {
        Intent intent = new Intent(context, ItemSelectionActivity.class);
        ArrayList<String> excludeItems = new ArrayList<>();
        for (Item item : shoppingList.getItems()) {
            excludeItems.add(item.getNombre());
        }
        intent.putStringArrayListExtra("excludeItems", excludeItems);
        selectItemActivityResultLauncher.launch(intent);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDataChanged() {
        if (v == null || shoppingList == null) {
            return;
        }
        rvShoppingList = v.findViewById(R.id.rvItemList);
        adapter = new ItemGridAdapter(shoppingList.getItems(), this);
        rvShoppingList.setAdapter(adapter);
        rvShoppingList.setLayoutManager(new GridLayoutManager(context, 3));

    }

    @Override
    public void onShoppingListChanged(ShoppingList shoppingList) {
        if (shoppingList.getId().equals(this.shoppingList.getId())) {
            this.shoppingList = shoppingList;
            onDataChanged();
        }
    }

    @Override
    public void onGridItemClicked(ItemViewFittable gridItem) {
        if (gridItem instanceof Item) {
            manager.deleteItem(shoppingList, (Item) gridItem);
            adapter.notifyDataSetChanged();
        }
    }
}
