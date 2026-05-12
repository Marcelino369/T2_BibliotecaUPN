package com.jg.cesaryjuanclaudio.repositories;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.jg.cesaryjuanclaudio.entities.Administrativo;

import java.util.List;

@Dao
public interface AdministrativoDao {

    @Insert
    void insert(Administrativo a);

    @Update
    void update(Administrativo a);

    @Delete
    void delete(Administrativo a);

    @Query("SELECT * FROM administradores")
    List<Administrativo> listarAdministrativos();

    @Query("SELECT * FROM administradores WHERE id = :id")
    Administrativo getById(Integer id);

    @Query("SELECT * FROM administradores WHERE codAdmin = :cod LIMIT 1")
    Administrativo getAdministrativoByCodigo(String cod);

    @Query("SELECT * FROM administradores WHERE correo = :correo LIMIT 1")
    Administrativo getByCorreo(String correo);

    @Query("DELETE FROM administradores WHERE id = :id")
    void deleteById(Integer id);
}