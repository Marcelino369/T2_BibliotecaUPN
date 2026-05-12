package com.jg.cesaryjuanclaudio.services;

import com.jg.cesaryjuanclaudio.utils.Configuracion;

public class AuthService {

    private static AuthService instance;

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    public boolean autenticar(String correo, String password) {
        return Configuracion.ADMIN_EMAIL.equals(correo)
                && Configuracion.ADMIN_PASSWORD.equals(password);
    }
}