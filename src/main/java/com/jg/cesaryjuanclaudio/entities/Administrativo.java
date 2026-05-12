package com.jg.cesaryjuanclaudio.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.jg.cesaryjuanclaudio.entities.estados.TipoUsuarios;

@Entity(tableName = "administradores")
public class Administrativo extends Usuario{
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String codAdmin;
    private String area;

    public Administrativo (){

    }
    @Ignore
    public Administrativo(String nombre, String apellido, String correo,
                          String contrasena, String codAdmin, String area) {
        super(nombre, apellido, correo, contrasena);
        this.codAdmin = codAdmin;
        this.area = area;
        this.tipoUsuario = TipoUsuarios.ADMINISTRATIVO;
        this.codigoUsuario = codAdmin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodAdmin() {
        return codAdmin;
    }

    public void setCodAdmin(String codAdmin) {
        this.codAdmin = codAdmin;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
