package com.fosanzdev.listacomprafirebase.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShoppingList {


    private String id;
    private String nombre;
    private List<Item> items= new ArrayList<>();

    //Empty constructor
    public ShoppingList() {

    }

    public ShoppingList(String nombre) {
        this.nombre = nombre;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public void removeItem(int index) {
        items.remove(index);
    }

    public void clearItems() {
        items.clear();
    }

    @Exclude
    public int getItemsCount() {
        return items.size();
    }

    public Item getItem(int index) {
        return items.get(index);
    }

    public boolean containsItem(Item item) {
        return items.contains(item);
    }

    public boolean containsItem(String itemId) {
        for (Item item : items) {
            if (item.getId().equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre: ").append(nombre).append("\n");
        sb.append("Items: ").append("\n")
                .append("----------------------------").append("\n");
        for (Item item : items) {
            sb.append(item.toString()).append("\n");
        }
        sb.append("----------------------------");
        return sb.toString();
    }
}
