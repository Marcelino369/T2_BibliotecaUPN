package com.jg.cesaryjuanclaudio.services;

import com.jg.cesaryjuanclaudio.entities.*;
import com.jg.cesaryjuanclaudio.entities.estados.EstadoUsuario;
import com.jg.cesaryjuanclaudio.entities.estados.TipoUsuarios;
import com.jg.cesaryjuanclaudio.repositories.*;
import com.jg.cesaryjuanclaudio.utils.Configuracion;

import java.util.List;

public class UsuarioService {

    private final EstudianteDao estudianteDao;
    private final DocenteDao docenteDao;
    private final AdministrativoDao adminDao;

    public UsuarioService(AppDatabase db) {
        this.estudianteDao = db.estudianteDao();
        this.docenteDao = db.docenteDao();
        this.adminDao = db.adminDao();
    }

    public void registrarEstudiante(Estudiante e) {
        estudianteDao.insert(e);
    }

    public void registrarDocente(Docente d) {
        docenteDao.insert(d);
    }

    public void registrarAdministrativo(Administrativo a) {
        adminDao.insert(a);
    }

    public List<Estudiante> listarEstudiantes() {
        return estudianteDao.listarEstudiantes();
    }

    public List<Docente> listarDocentes() {
        return docenteDao.listarDocentes();
    }

    public List<Administrativo> listarAdministrativos() {
        return adminDao.listarAdministrativos();
    }


    public Usuario buscarPorCodigo(String codigo, TipoUsuarios tipo) {
        switch (tipo) {
            case ESTUDIANTE:    return estudianteDao.getEstudianteByCodigo(codigo);
            case DOCENTE:       return docenteDao.getDocenteByCodigo(codigo);
            case ADMINISTRATIVO: return adminDao.getAdministrativoByCodigo(codigo);
            default: return null;
        }
    }

    public void bloquear(Usuario usuario) {
        usuario.setEstado(EstadoUsuario.BLOQUEADO);
        actualizarUsuario(usuario);
    }

    public void activar(Usuario usuario) {
        usuario.setEstado(EstadoUsuario.ACTIVO);
        actualizarUsuario(usuario);
    }

    public void actualizarUsuario(Usuario usuario) {
        if (usuario instanceof Estudiante)
            estudianteDao.update((Estudiante) usuario);
        else if (usuario instanceof Docente)
            docenteDao.update((Docente) usuario);
        else if (usuario instanceof Administrativo)
            adminDao.update((Administrativo) usuario);
    }

    public boolean puedeSolicitarPrestamo(Usuario usuario) {
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) return false;
        if (usuario.getDeuda() > 0) return false;

        int limite = getLimitePrestamos(usuario.getTipoUsuario());
        return usuario.getCantPrestamo() < limite;
    }

    public String motivoNoPuedePedir(Usuario usuario) {
        if (usuario.getEstado() == EstadoUsuario.BLOQUEADO)
            return "Usuario bloqueado.";
        if (usuario.getDeuda() > 0)
            return "Tiene deuda pendiente de S/ " + usuario.getDeuda();
        if (usuario.getCantPrestamo() >= getLimitePrestamos(usuario.getTipoUsuario()))
            return "Alcanzó el límite de préstamos activos.";
        return null;
    }

    private int getLimitePrestamos(TipoUsuarios tipo) {
        switch (tipo) {
            case DOCENTE:       return Configuracion.MAX_PRESTAMOS_DOCENTE;
            case ADMINISTRATIVO: return Configuracion.MAX_PRESTAMOS_ADMIN;
            default:            return Configuracion.MAX_PRESTAMOS_ESTUDIANTE;
        }
    }

    public void agregarDeuda(Usuario usuario, double monto) {
        usuario.setDeuda(usuario.getDeuda() + monto);
        actualizarUsuario(usuario);
    }
}