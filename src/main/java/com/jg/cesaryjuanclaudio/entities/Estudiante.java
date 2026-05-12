package com.jg.cesaryjuanclaudio.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.jg.cesaryjuanclaudio.entities.estados.TipoUsuarios;

@Entity(tableName = "estudiantes")
public class Estudiante extends Usuario{
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String codEstudiante;

    private String carrera;

    public Estudiante (){

    }
    @Ignore
    public Estudiante(String nombre, String apellido, String correo,
                      String contrasena, String codEstudiante, String carrera) {
        super(nombre, apellido, correo, contrasena);
        this.codEstudiante = codEstudiante;
        this.carrera = carrera;
        this.tipoUsuario = TipoUsuarios.ESTUDIANTE;
        this.codigoUsuario = codEstudiante;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodEstudiante() {
        return codEstudiante;
    }

    public void setCodEstudiante(String codEstudiante) {
        this.codEstudiante = codEstudiante;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }
}
