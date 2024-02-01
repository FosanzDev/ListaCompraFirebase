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

import com.fosanzdev.listacomprafirebase.R;
import com.fosanzdev.listacomprafirebase.adapters.ItemGridAdapter;
import com.fosanzdev.listacomprafirebase.managers.CategoryManager;
import com.fosanzdev.listacomprafirebase.models.Category;
import com.fosanzdev.listacomprafirebase.models.Item;
import com.fosanzdev.listacomprafirebase.models.ItemViewFittable;
import com.google.firebase.firestore.FirebaseFirestore;

public class CategorySelectionFragment extends Fragment implements ItemGridAdapter.ItemViewHolder.IOnItemClickedListener, CategoryManager.OnCategoryInteractionListener {

    public interface ICategorySelectionFragmentListener {
        void onCategorySelected(Category category);
    }

    private ItemGridAdapter adapter;
    private ICategorySelectionFragmentListener listener;
    private CategoryManager categoryManager;
    private View v;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryManager = new CategoryManager(FirebaseFirestore.getInstance(), this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_grid_item_selection, container, false);
        RecyclerView rvItemGridSelection = v.findViewById(R.id.rvItemGridSelection);
        adapter = new ItemGridAdapter(categoryManager, this);
        rvItemGridSelection.setAdapter(adapter);
        rvItemGridSelection.setLayoutManager(new GridLayoutManager(getContext(), 3));
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (ICategorySelectionFragmentListener) context;
    }

    @Override
    public void onDataChanged() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onGridItemClicked(ItemViewFittable gridItem) {
        if (gridItem instanceof Category) {
            listener.onCategorySelected((Category) gridItem);
        }
    }
}
