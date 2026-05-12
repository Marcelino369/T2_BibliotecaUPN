package com.jg.cesaryjuanclaudio.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.jg.cesaryjuanclaudio.entities.estados.EstadoLibro;

@Entity(tableName = "libros")
public class Libro {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String cod;
    private String titulo;
    private String categoria;
    private String editorial;
    private Integer stockTotal;
    private Integer stockDisponible;
    private EstadoLibro estado;

    public Libro() {
    }

    public Libro(Integer id, String cod, String titulo, String categoria, String editorial, Integer stockTotal, Integer stockDisponible, EstadoLibro estado) {
        this.id = id;
        this.cod = cod;
        this.titulo = titulo;
        this.categoria = categoria;
        this.editorial = editorial;
        this.stockTotal = stockTotal;
        this.stockDisponible = stockDisponible;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public Integer getStockTotal() {
        return stockTotal;
    }

    public void setStockTotal(Integer stockTotal) {
        this.stockTotal = stockTotal;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public EstadoLibro getEstado() {
        return estado;
    }

    public void setEstado(EstadoLibro estado) {
        this.estado = estado;
    }

    @NonNull
    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", cod='" + cod + '\'' +
                ", titulo='" + titulo + '\'' +
                ", categoria='" + categoria + '\'' +
                ", editorial='" + editorial + '\'' +
                ", stockTotal=" + stockTotal +
                ", stockDisponible=" + stockDisponible +
                ", estado=" + estado +
                '}';
    }
}

