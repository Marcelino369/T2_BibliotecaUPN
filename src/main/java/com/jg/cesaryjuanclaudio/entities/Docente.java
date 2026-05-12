package com.jg.cesaryjuanclaudio.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.jg.cesaryjuanclaudio.entities.estados.TipoUsuarios;

@Entity(tableName = "docentes")
public class Docente extends Usuario{
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String codDocente;
    private String facultad;

    public Docente() {
    }

    public Docente(String nombre, String apellido, String correo,
                   String contrasena, String codDocente, String facultad) {
        super(nombre, apellido, correo, contrasena);
        this.codDocente = codDocente;
        this.facultad = facultad;
        this.tipoUsuario = TipoUsuarios.DOCENTE;
        this.codigoUsuario = codDocente;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodDocente() {
        return codDocente;
    }

    public void setCodDocente(String codDocente) {
        this.codDocente = codDocente;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }
}
