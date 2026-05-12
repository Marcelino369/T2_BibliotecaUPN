package com.jg.cesaryjuanclaudio.entities;

import androidx.room.Ignore;

import com.jg.cesaryjuanclaudio.entities.estados.EstadoUsuario;
import com.jg.cesaryjuanclaudio.entities.estados.TipoUsuarios;

public class Usuario {
    protected String nombre;
    protected String apellido;
    protected String correo;
    protected EstadoUsuario estado;
    protected Double deuda;
    protected Integer cantPrestamo;
    protected String contrasena;
    protected TipoUsuarios tipoUsuario;
    protected String codigoUsuario; // codEstudiante, codDocente o codAdmin

    public Usuario (){
        this.estado = EstadoUsuario.ACTIVO;
        this.deuda = 0d;
        this.cantPrestamo = 0;
    }
    @Ignore
    public Usuario(String nombre, String apellido, String correo, String contrasena) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.estado = EstadoUsuario.ACTIVO;
        this.deuda = 0d;
        this.cantPrestamo = 0;
        this.contrasena = contrasena;
    }

    public TipoUsuarios getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuarios tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getCodigoUsuario() { return codigoUsuario; }
    public void setCodigoUsuario(String codigoUsuario) { this.codigoUsuario = codigoUsuario; }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public Double getDeuda() {
        return deuda;
    }

    public void setDeuda(Double deuda) {
        this.deuda = deuda;
    }

    public Integer getCantPrestamo() {
        return cantPrestamo;
    }

    public void setCantPrestamo(Integer cantPrestamo) {
        this.cantPrestamo = cantPrestamo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
