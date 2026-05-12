package com.jg.cesaryjuanclaudio.services;

import com.jg.cesaryjuanclaudio.entities.Prestamo;
import com.jg.cesaryjuanclaudio.utils.Configuracion;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MoraService {

    private static MoraService instance;

    private MoraService() {}

    public static MoraService getInstance() {
        if (instance == null) instance = new MoraService();
        return instance;
    }

    public double calcularMoraActual(Prestamo prestamo) {
        if (prestamo.isEntregado() || prestamo.isPerdido()) {
            return prestamo.getMoraAcumulada(); // ya cerrado, devolver lo guardado
        }
        long diasRetraso = getDiasRetraso(prestamo.getFechaLimite(), LocalDate.now());
        return diasRetraso * Configuracion.MORA_DIARIA;
    }

    public double calcularMoraDevolucion(Prestamo prestamo, LocalDate fechaEntrega) {
        long diasRetraso = getDiasRetraso(prestamo.getFechaLimite(), fechaEntrega);
        return diasRetraso * Configuracion.MORA_DIARIA;
    }

    public long getDiasRetraso(LocalDate fechaLimite, LocalDate fechaActual) {
        if (!fechaActual.isAfter(fechaLimite)) return 0;
        return ChronoUnit.DAYS.between(fechaLimite, fechaActual);
    }
}