package com.jg.cesaryjuanclaudio.repositories;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.jg.cesaryjuanclaudio.entities.Docente;

import java.util.List;

@Dao
public interface DocenteDao {

    @Insert
    void insert(Docente d);

    @Update
    void update(Docente d);

    @Delete
    void delete(Docente d);

    @Query("SELECT * FROM docentes")
    List<Docente> listarDocentes();

    @Query("SELECT * FROM docentes WHERE id = :id")
    Docente getById(Integer id);

    @Query("SELECT * FROM docentes WHERE codDocente = :cod LIMIT 1")
    Docente getDocenteByCodigo(String cod);

    @Query("SELECT * FROM docentes WHERE correo = :correo LIMIT 1")
    Docente getByCorreo(String correo);

    @Query("DELETE FROM docentes WHERE id = :id")
    void deleteById(Integer id);
}