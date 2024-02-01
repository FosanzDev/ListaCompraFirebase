package com.fosanzdev.listacomprafirebase.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fosanzdev.listacomprafirebase.adapters.ItemGridAdapter;
import com.fosanzdev.listacomprafirebase.R;
import com.fosanzdev.listacomprafirebase.managers.ItemManager;
import com.fosanzdev.listacomprafirebase.models.Category;
import com.fosanzdev.listacomprafirebase.models.Item;
import com.fosanzdev.listacomprafirebase.models.ItemViewFittable;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ItemSelectionFragment extends Fragment implements ItemManager.OnItemInteractionListener, ItemGridAdapter.ItemViewHolder.IOnItemClickedListener{

    public interface IItemSelectionFragmentListener {
        void onItemSelection(Item item);
    }

    private ItemGridAdapter adapter;
    private IItemSelectionFragmentListener listener;
    private ItemManager itemManager;
    private View v;
    private Category category;
    private List<String> excludeItems = new ArrayList<>();

    public ItemSelectionFragment(List<String> excludeItems) {
        this.itemManager = new ItemManager(FirebaseFirestore.getInstance(), this);
        this.excludeItems = excludeItems;
    }

    public ItemSelectionFragment(Category category, List<String> excludeItems) {
        this.itemManager = new ItemManager(FirebaseFirestore.getInstance(), this);
        this.category = category;
        this.excludeItems = excludeItems;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_grid_item_selection, container, false);
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (IItemSelectionFragmentListener) context;
    }

    @Override
    public void onGridItemClicked(ItemViewFittable gridItem) {
        if (gridItem instanceof Item) {
            listener.onItemSelection((Item) gridItem);
        }
    }

    @Override
    public void onDataChanged() {
        if (v == null) return;
        if (itemManager.size() != 0) {
            RecyclerView rvItemGridSelection = v.findViewById(R.id.rvItemGridSelection);
            if (category == null) {
                adapter = new ItemGridAdapter(itemManager, this);
            } else {
                adapter = new ItemGridAdapter(itemManager.filterByCategoryExcluding(category, excludeItems), this);
            }
            rvItemGridSelection.setAdapter(adapter);
            rvItemGridSelection.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }
    }
}
