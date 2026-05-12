package com.jg.cesaryjuanclaudio.repositories;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jg.cesaryjuanclaudio.entities.Prestamo;

import java.util.List;

@Dao
public interface PrestamoDao {

    @Insert
    long insert(Prestamo prestamo);

    @Update
    void update(Prestamo prestamo);

    @Query("SELECT * FROM prestamos WHERE id = :id")
    Prestamo getById(Integer id);

    @Query("SELECT * FROM prestamos WHERE codigoUsuario = :cod AND entregado = 0 AND perdido = 0")
    List<Prestamo> getPrestamosActivosByUsuario(String cod);

    @Query("SELECT * FROM prestamos WHERE codigoLibro = :cod AND entregado = 0 AND perdido = 0")
    List<Prestamo> getPrestamosActivosByLibro(String cod);

    @Query("SELECT * FROM prestamos ORDER BY fechaPrestamo DESC")
    List<Prestamo> getTodos();

    @Query("SELECT * FROM prestamos WHERE entregado = 0 AND perdido = 0 AND fechaLimite < :hoy")
    List<Prestamo> getPrestamosVencidos(String hoy);

    @Query("SELECT codigoLibro, tituloLibro, COUNT(*) AS total FROM prestamos GROUP BY codigoLibro ORDER BY total DESC")
    List<LibroUsadoResult> getLibrosMasUsados();

    @Query("SELECT codigoUsuario, nombreUsuario, COUNT(*) AS total FROM prestamos GROUP BY codigoUsuario ORDER BY total DESC")
    List<UsuarioActivoResult> getUsuariosMasPrestamos();

    @Query("SELECT * FROM prestamos WHERE fechaPrestamo BETWEEN :inicio AND :fin ORDER BY fechaPrestamo DESC")
    List<Prestamo> getPrestamosPorFecha(String inicio, String fin);

    class LibroUsadoResult {
        public String codigoLibro;
        public String tituloLibro;
        public int total;
    }

    class UsuarioActivoResult {
        public String codigoUsuario;
        public String nombreUsuario;
        public int total;
    }
}