package com.jg.cesaryjuanclaudio.repositories;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.jg.cesaryjuanclaudio.entities.Estudiante;

import java.util.List;

@Dao
public interface EstudianteDao {

    @Insert
    void insert(Estudiante es);

    @Update
    void update(Estudiante es);

    @Delete
    void delete(Estudiante es);

    @Query("SELECT * FROM estudiantes")
    List<Estudiante> listarEstudiantes();

    @Query("SELECT * FROM estudiantes WHERE id = :id")
    Estudiante getById(Integer id);

    @Query("SELECT * FROM estudiantes WHERE codEstudiante = :cod LIMIT 1")
    Estudiante getEstudianteByCodigo(String cod);

    @Query("SELECT * FROM estudiantes WHERE correo = :correo LIMIT 1")
    Estudiante getByCorreo(String correo);

    @Query("DELETE FROM estudiantes WHERE id = :id")
    void deleteById(Integer id);
}