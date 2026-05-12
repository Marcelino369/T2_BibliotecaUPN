package com.jg.cesaryjuanclaudio.services;

import com.jg.cesaryjuanclaudio.entities.Libro;
import com.jg.cesaryjuanclaudio.entities.estados.EstadoLibro;
import com.jg.cesaryjuanclaudio.repositories.AppDatabase;
import com.jg.cesaryjuanclaudio.repositories.LibroDao;
import com.jg.cesaryjuanclaudio.repositories.PrestamoDao;
import java.util.List;

public class LibroService {

    private final LibroDao libroDao;
    private final PrestamoDao prestamoDao;

    public LibroService(AppDatabase db) {
        this.libroDao = db.libroDao();
        this.prestamoDao = db.prestamoDao();
    }

    public void registrar(Libro libro) {
        libro.setEstado(EstadoLibro.DISPONIBLE);
        libroDao.insert(libro);
    }

    public void actualizar(Libro libro) {
        libroDao.update(libro);
    }

    public Libro buscarPorCodigo(String cod) {
        return libroDao.getByCodigo(cod);
    }

    public List<Libro> listarTodos() {
        return libroDao.getTodos();
    }

    public List<Libro> listarDisponibles() {
        return libroDao.getByEstado(EstadoLibro.DISPONIBLE);
    }

    public List<Libro> listarDescartados() {
        return libroDao.getByEstado(EstadoLibro.DESCARTADO);
    }

    public boolean estaDisponible(Libro libro) {
        return libro.getStockDisponible() > 0
                && libro.getEstado() != EstadoLibro.DESCARTADO;
    }

    public void renovar(Libro libro, int stockAdicional) {
        libroDao.aumentarStock(libro.getCod(), stockAdicional);
        libroDao.actualizarEstado(libro.getCod(), EstadoLibro.DISPONIBLE);
    }

    public void aumentarStock(String codigoLibro, int cantidad) {
        libroDao.aumentarStock(codigoLibro, cantidad);
    }

    public boolean puedeDescartarse(String codigoLibro) {
        List prestamosActivos = prestamoDao.getPrestamosActivosByLibro(codigoLibro);
        return prestamosActivos.isEmpty();
    }

    public String descartar(Libro libro) {
        if (!puedeDescartarse(libro.getCod())) {
            return "No se puede descartar: el libro tiene préstamos activos.";
        }
        libroDao.actualizarEstado(libro.getCod(), EstadoLibro.DESCARTADO);
        return null;
    }
}