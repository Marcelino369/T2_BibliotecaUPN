package com.jg.cesaryjuanclaudio.repositories;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jg.cesaryjuanclaudio.entities.Libro;
import com.jg.cesaryjuanclaudio.entities.estados.EstadoLibro;

import java.util.List;

@Dao
public interface LibroDao {

    @Insert
    void insert(Libro libro);

    @Update
    void update(Libro libro);

    @Query("SELECT * FROM libros WHERE id = :id")
    Libro getById(Integer id);

    @Query("SELECT * FROM libros WHERE cod = :cod LIMIT 1")
    Libro getByCodigo(String cod);

    @Query("SELECT * FROM libros ORDER BY cod ASC")
    List<Libro> getTodos();

    @Query("SELECT * FROM libros WHERE estado = :estado")
    List<Libro> getByEstado(EstadoLibro estado);

    @Query("UPDATE libros SET stockDisponible = stockDisponible - 1 WHERE cod = :cod AND stockDisponible > 0")
    void decrementarStock(String cod);

    @Query("UPDATE libros SET stockDisponible = stockDisponible + 1 WHERE cod = :cod")
    void incrementarStock(String cod);

    @Query("UPDATE libros SET stockTotal = stockTotal + :cantidad, stockDisponible = stockDisponible + :cantidad WHERE cod = :cod")
    void aumentarStock(String cod, int cantidad);

    @Query("UPDATE libros SET estado = :estado WHERE cod = :cod")
    void actualizarEstado(String cod, EstadoLibro estado);

    @Query("DELETE FROM libros WHERE id = :id")
    void deleteById(Integer id);
}