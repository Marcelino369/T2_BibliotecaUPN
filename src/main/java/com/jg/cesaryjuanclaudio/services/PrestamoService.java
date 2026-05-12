package com.jg.cesaryjuanclaudio.services;

import com.jg.cesaryjuanclaudio.entities.*;
import com.jg.cesaryjuanclaudio.repositories.AppDatabase;
import com.jg.cesaryjuanclaudio.repositories.LibroDao;
import com.jg.cesaryjuanclaudio.repositories.PrestamoDao;
import com.jg.cesaryjuanclaudio.utils.Configuracion;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PrestamoService {

    private final PrestamoDao prestamoDao;
    private final LibroDao libroDao;
    private final UsuarioService usuarioService;
    private final LibroService libroService;
    private final MoraService moraService;

    public PrestamoService(AppDatabase db) {
        this.prestamoDao = db.prestamoDao();
        this.libroDao = db.libroDao();
        this.usuarioService = new UsuarioService(db);
        this.libroService = new LibroService(db);
        this.moraService = MoraService.getInstance();
    }

    public String prestarLibro(Usuario usuario, String codigoLibro, int diasPrestamo) {

        // Validar usuario
        if (!usuarioService.puedeSolicitarPrestamo(usuario)) {
            return usuarioService.motivoNoPuedePedir(usuario);
        }

        // Validar libro
        Libro libro = libroService.buscarPorCodigo(codigoLibro);
        if (libro == null) return "Libro no encontrado.";
        if (!libroService.estaDisponible(libro)) return "Libro no disponible.";

        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(diasPrestamo);

        Prestamo prestamo = new Prestamo();
        prestamo.setCodigoUsuario(usuario.getCodigoUsuario());
        prestamo.setTipoUsuario(usuario.getTipoUsuario());
        prestamo.setNombreUsuario(usuario.getNombre() + " " + usuario.getApellido());
        prestamo.setCodigoLibro(libro.getCod());
        prestamo.setTituloLibro(libro.getTitulo());
        prestamo.setFechaPrestamo(hoy);
        prestamo.setFechaLimite(fechaLimite);

        prestamoDao.insert(prestamo);
        libroDao.decrementarStock(codigoLibro);

        usuario.setCantPrestamo(usuario.getCantPrestamo() + 1);
        usuarioService.actualizarUsuario(usuario);

        return null; // null = éxito
    }

    public String devolverLibro(Prestamo prestamo, Usuario usuario, boolean perdido) {

        LocalDate hoy = LocalDate.now();
        double cargo;
        String mensaje;

        if (perdido) {
            prestamo.setPerdido(true);
            prestamo.setFechaEntrega(hoy);
            prestamo.setMoraAcumulada(Configuracion.MULTA_PERDIDA);
            cargo = Configuracion.MULTA_PERDIDA;
            mensaje = "Libro perdido. Multa aplicada: S/ " + cargo;

        } else {
            double mora = moraService.calcularMoraDevolucion(prestamo, hoy);
            prestamo.setEntregado(true);
            prestamo.setFechaEntrega(hoy);
            prestamo.setMoraAcumulada(mora);
            cargo = mora;
            libroDao.incrementarStock(prestamo.getCodigoLibro());

            if (mora > 0) {
                long dias = ChronoUnit.DAYS.between(prestamo.getFechaLimite(), hoy);
                mensaje = "Entregado con " + dias + " días de retraso. Mora: S/ " + mora;
            } else {
                mensaje = null;
            }
        }

        prestamoDao.update(prestamo);

        if (cargo > 0) usuarioService.agregarDeuda(usuario, cargo);
        usuario.setCantPrestamo(Math.max(0, usuario.getCantPrestamo() - 1));
        usuarioService.actualizarUsuario(usuario);

        return mensaje;
    }

    public List<Prestamo> getPrestamosActivosDeUsuario(String codigoUsuario) {
        return prestamoDao.getPrestamosActivosByUsuario(codigoUsuario);
    }

    public List<Prestamo> getTodosPrestamos() {
        return prestamoDao.getTodos();
    }

    public List<Prestamo> getPrestamosVencidos() {
        return prestamoDao.getPrestamosVencidos(LocalDate.now().toString());
    }

    public List<Prestamo> getPorIntervalo(LocalDate inicio, LocalDate fin) {
        return prestamoDao.getPrestamosPorFecha(inicio.toString(), fin.toString());
    }

    public Prestamo getPrestamoById(int id) {
        return prestamoDao.getById(id);
    }
}