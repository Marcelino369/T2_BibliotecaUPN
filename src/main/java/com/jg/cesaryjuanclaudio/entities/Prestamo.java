package com.jg.cesaryjuanclaudio.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.jg.cesaryjuanclaudio.entities.estados.TipoUsuarios;

import java.time.LocalDate;

@Entity(tableName = "prestamos")
public class Prestamo {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    // Quién pide el libro
    private String codigoUsuario;   // unifica los 3 tipos
    private TipoUsuarios tipoUsuario; // para saber en qué tabla buscar
    private String nombreUsuario;   // desnormalizado para reportes rápidos

    // Qué libro
    private String codigoLibro;
    private String tituloLibro;     // desnormalizado igual

    // Fechas
    private LocalDate fechaPrestamo;
    private LocalDate fechaLimite;       // fechaPrestamo + diasPrestamo
    private LocalDate fechaEntrega;      // null mientras no se entregue

    // Estado
    private boolean entregado;      // false por defecto
    private boolean perdido;        // false por defecto

    // Deuda
    private Double moraAcumulada;   // se calcula, pero se guarda al cerrar el préstamo

    public Prestamo() {
        this.entregado = false;
        this.perdido = false;
        this.moraAcumulada = 0.0;
    }

    // --- Getters y setters ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigoUsuario() { return codigoUsuario; }
    public void setCodigoUsuario(String codigoUsuario) { this.codigoUsuario = codigoUsuario; }

    public TipoUsuarios getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuarios tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getCodigoLibro() { return codigoLibro; }
    public void setCodigoLibro(String codigoLibro) { this.codigoLibro = codigoLibro; }

    public String getTituloLibro() { return tituloLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }

    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public boolean isEntregado() { return entregado; }
    public void setEntregado(boolean entregado) { this.entregado = entregado; }

    public boolean isPerdido() { return perdido; }
    public void setPerdido(boolean perdido) { this.perdido = perdido; }

    public Double getMoraAcumulada() { return moraAcumulada; }
    public void setMoraAcumulada(Double moraAcumulada) { this.moraAcumulada = moraAcumulada; }
}