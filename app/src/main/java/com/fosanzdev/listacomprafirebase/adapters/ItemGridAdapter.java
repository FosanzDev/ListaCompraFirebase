package com.fosanzdev.listacomprafirebase.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fosanzdev.listacomprafirebase.R;
import com.fosanzdev.listacomprafirebase.Utils;
import com.fosanzdev.listacomprafirebase.models.ItemViewFittable;

import java.util.List;

public class ItemGridAdapter extends RecyclerView.Adapter<ItemGridAdapter.ItemViewHolder>{

    private List<? extends ItemViewFittable> items;
    private ItemViewHolder.IOnItemClickedListener listener;


    public ItemGridAdapter(List<? extends ItemViewFittable> items, ItemViewHolder.IOnItemClickedListener listener) {
        this.items = items;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.shopping_list_detail_item, parent, false);
        return new ItemViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind((ItemViewFittable) items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public interface IOnItemClickedListener {
            void onGridItemClicked(ItemViewFittable gridItem);
        }

        ImageView ivItemImage;
        TextView tvItemName;
        ItemViewFittable gridItem;
        IOnItemClickedListener listener;


        public ItemViewHolder(@NonNull View itemView, IOnItemClickedListener listener) {
            super(itemView);
            this.listener = listener;
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            itemView.setOnClickListener(this);
        }

        public void bind(ItemViewFittable gridItem){
            this.gridItem = gridItem;
            tvItemName.setText(gridItem.getNombre());
            ivItemImage.setImageBitmap(Utils.base64ToBitMap(gridItem.getImage()));
        }

        @Override
        public void onClick(View v) {
            listener.onGridItemClicked(gridItem);
        }
    }
}
