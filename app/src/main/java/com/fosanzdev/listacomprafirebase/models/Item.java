package com.fosanzdev.listacomprafirebase.models;

import com.google.firebase.firestore.Exclude;

public class Item implements ItemViewFittable{

    private String id;
    private String nombre;
    private Category categoria;
    private String image;

    //Empty constructor
    public Item() {

    }

    public Item(String nombre, Category categoria, String image) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.image = image;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public Category getCategoria() {
        return categoria;
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(id).append("\n");
        sb.append("Nombre: ").append(nombre).append("\n");
        sb.append("Categoria: ").append(categoria.getNombre()).append("\n");
        return sb.toString();
    }
}
