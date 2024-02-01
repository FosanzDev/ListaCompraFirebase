package com.fosanzdev.listacomprafirebase.models;

import com.google.firebase.firestore.Exclude;

public class Category implements ItemViewFittable{

    private String id;
    private String nombre;
    private String image;

    //Empty constructor
    public Category() {

    }

    public Category(String nombre, String image) {
        this.nombre = nombre;
        this.image = image;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String getImage() {
        return image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String nombre) {
        this.nombre = nombre;
    }
}
