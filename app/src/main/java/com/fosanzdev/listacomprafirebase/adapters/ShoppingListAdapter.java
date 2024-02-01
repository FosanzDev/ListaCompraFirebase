package com.fosanzdev.listacomprafirebase.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fosanzdev.listacomprafirebase.R;
import com.fosanzdev.listacomprafirebase.managers.ShoppingListManager;
import com.fosanzdev.listacomprafirebase.models.ShoppingList;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>{

    private ShoppingListManager manager;
    private ShoppingListViewHolder.OnShoppingListClickListener listener;

    public ShoppingListAdapter(ShoppingListManager manager, ShoppingListViewHolder.OnShoppingListClickListener listener){
        this.manager = manager;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.shopping_list_item, parent, false);
        return new ShoppingListViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
        holder.bind(manager.get(position));
    }

    @Override
    public int getItemCount() {
        return manager.size();
    }

    public static class ShoppingListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public interface OnShoppingListClickListener {
            void onShoppingListClick(ShoppingList shoppingList, int position);
            void onShoppingListLongClick(ShoppingList shoppingList, int position);
        }

        TextView tvName;
        TextView tvItemCount;
        OnShoppingListClickListener listener;
        ShoppingList shoppingList;

        public ShoppingListViewHolder(@NonNull View itemView, OnShoppingListClickListener listener) {
            super(itemView);
            this.listener = listener;
            tvName = itemView.findViewById(R.id.tvShoppingListName);
            tvItemCount = itemView.findViewById(R.id.tvShoppingListItemCount);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(ShoppingList shoppingList){
            tvName.setText(shoppingList.getNombre());
            tvItemCount.setText(String.valueOf(shoppingList.getItems().size()));
            this.shoppingList = shoppingList;
        }

        @Override
        public void onClick(View v) {
            listener.onShoppingListClick(shoppingList, getBindingAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onShoppingListLongClick(shoppingList, getBindingAdapterPosition());
            return true;
        }
    }

}
