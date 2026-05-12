package com.jg.cesaryjuanclaudio.services;

import com.jg.cesaryjuanclaudio.entities.Libro;
import com.jg.cesaryjuanclaudio.entities.Prestamo;
import com.jg.cesaryjuanclaudio.entities.estados.EstadoLibro;
import com.jg.cesaryjuanclaudio.repositories.AppDatabase;
import com.jg.cesaryjuanclaudio.repositories.LibroDao;
import com.jg.cesaryjuanclaudio.repositories.PrestamoDao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ReporteService {

    private final PrestamoDao prestamoDao;
    private final LibroDao libroDao;
    private final MoraService moraService;

    public ReporteService(AppDatabase db) {
        this.prestamoDao = db.prestamoDao();
        this.libroDao = db.libroDao();
        this.moraService = MoraService.getInstance();
    }

    public List<PrestamoDao.LibroUsadoResult> getLibrosMasUsados() {
        return prestamoDao.getLibrosMasUsados();
    }

    public List<PrestamoDao.UsuarioActivoResult> getUsuariosMasPrestamos() {
        return prestamoDao.getUsuariosMasPrestamos();
    }

    public List<Prestamo> getTodosConMora() {
        List<Prestamo> prestamos = prestamoDao.getTodos();
        for (Prestamo p : prestamos) {
            if (!p.isEntregado() && !p.isPerdido()) {
                // mora actual en tiempo real
                p.setMoraAcumulada(moraService.calcularMoraActual(p));
            }
        }
        return prestamos;
    }

    public List<Prestamo> getPorIntervalo(LocalDate inicio, LocalDate fin) {
        String startMillis = String.valueOf(inicio.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        long endMillis = fin.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return prestamoDao.getPrestamosPorFecha(startMillis, String.valueOf(endMillis));
    }

    public List<Libro> getLibrosDescartados() {
        return libroDao.getByEstado(EstadoLibro.DESCARTADO);
    }

    public List<Libro> getLibrosDisponibles() {
        return libroDao.getByEstado(EstadoLibro.DISPONIBLE);
    }
}