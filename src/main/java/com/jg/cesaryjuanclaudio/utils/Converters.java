package com.jg.cesaryjuanclaudio.utils;

import androidx.room.TypeConverter;

import com.jg.cesaryjuanclaudio.entities.estados.EstadoLibro;
import com.jg.cesaryjuanclaudio.entities.estados.EstadoUsuario;
import com.jg.cesaryjuanclaudio.entities.estados.TipoUsuarios;

import java.time.LocalDate;

public class Converters {

    @TypeConverter
    public static LocalDate fromString(String value) {
        return value == null ? null : LocalDate.parse(value);
    }

    @TypeConverter
    public static String localDateToString(LocalDate date) {
        return date == null ? null : date.toString();
    }

    // EstadoLibro ↔ String
    @TypeConverter
    public static EstadoLibro toEstadoLibro(String value) {
        return value == null ? null : EstadoLibro.valueOf(value);
    }

    @TypeConverter
    public static String fromEstadoLibro(EstadoLibro estado) {
        return estado == null ? null : estado.name();
    }

    // EstadoUsuario ↔ String
    @TypeConverter
    public static EstadoUsuario toEstadoUsuario(String value) {
        return value == null ? null : EstadoUsuario.valueOf(value);
    }

    @TypeConverter
    public static String fromEstadoUsuario(EstadoUsuario estado) {
        return estado == null ? null : estado.name();
    }

    // TipoUsuarios ↔ String
    @TypeConverter
    public static TipoUsuarios toTipoUsuarios(String value) {
        return value == null ? null : TipoUsuarios.valueOf(value);
    }

    @TypeConverter
    public static String fromTipoUsuarios(TipoUsuarios tipo) {
        return tipo == null ? null : tipo.name();
    }
}