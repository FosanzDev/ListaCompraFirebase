package com.fosanzdev.listacomprafirebase.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fosanzdev.listacomprafirebase.R;
import com.fosanzdev.listacomprafirebase.adapters.ShoppingListAdapter;
import com.fosanzdev.listacomprafirebase.managers.ShoppingListManager;
import com.fosanzdev.listacomprafirebase.models.ShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainFragment extends Fragment implements ShoppingListManager.OnShoppingListInteractionListener,
        ShoppingListAdapter.ShoppingListViewHolder.OnShoppingListClickListener {

    public interface IMainFragmentListener {
        void onShoppingListSelected(ShoppingList shoppingList, ShoppingListManager manager);
    }

    private Context context;
    private ShoppingListManager shoppingListManager;
    private ShoppingListAdapter adapter;
    private IMainFragmentListener listener;
    private FirebaseFirestore db;
    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_fragment, container, false);
        v.findViewById(R.id.loadingGif).setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();

        shoppingListManager = new ShoppingListManager(db, this);

        FloatingActionButton fab = v.findViewById(R.id.fabAddShoppingList);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Nueva lista de la compra");
                EditText etShoppingListName = new EditText(context);
                builder.setView(etShoppingListName);

                builder.setPositiveButton("Crear", (dialog, which) -> {
                    String shoppingListName = etShoppingListName.getText().toString();
                    ShoppingList shoppingList = new ShoppingList(shoppingListName);
                    shoppingListManager.add(shoppingList);
                });

                builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

                builder.show();
            }
        });

        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        listener = (IMainFragmentListener) context;
    }

    @Override
    public void onDataChanged() {
        v.findViewById(R.id.loadingGif).setVisibility(View.GONE);
        RecyclerView rvShoppingLists = v.findViewById(R.id.rvShoppingLists);
        adapter = new ShoppingListAdapter(shoppingListManager, this);
        rvShoppingLists.setAdapter(adapter);
        rvShoppingLists.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onShoppingListChanged(ShoppingList shoppingList) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onShoppingListClick(ShoppingList shoppingList, int position) {
        listener.onShoppingListSelected(shoppingList, shoppingListManager);
    }

    @Override
    public void onShoppingListLongClick(ShoppingList shoppingList, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("¿Eliminar lista?");

        builder.setPositiveButton("Eliminar", (dialog, which) -> {
            shoppingListManager.remove(shoppingList);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();

    }
}
