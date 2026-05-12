package com.jg.cesaryjuanclaudio.view;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

import com.google.android.material.textfield.TextInputLayout;
import com.jg.cesaryjuanclaudio.R;
import com.jg.cesaryjuanclaudio.entities.Libro;
import com.jg.cesaryjuanclaudio.entities.Prestamo;
import com.jg.cesaryjuanclaudio.repositories.AppDatabase;
import com.jg.cesaryjuanclaudio.repositories.PrestamoDao;
import com.jg.cesaryjuanclaudio.services.MoraService;
import com.jg.cesaryjuanclaudio.services.ReporteService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ReportesActivity extends AppCompatActivity {

    Spinner spinnerReporte;
    TextInputLayout tilFechaInicio, tilFechaFin;
    EditText etFechaInicio, etFechaFin;
    Button btnGenerar;
    ListView lvReporte;

    AppDatabase database;
    ReporteService reporteService;
    ArrayAdapter<String> adapter;
    List<String> filas = new ArrayList<>();

    private static final String[] TIPOS_REPORTE = {
            "Libros más usados",
            "Usuarios con más préstamos",
            "Todos los préstamos y moras",
            "Por intervalo de fechas",
            "Libros descartados",
            "Libros disponibles"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = AppDatabase.getDatabase(this);
        reporteService = new ReporteService(database);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reportes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerReporte  = findViewById(R.id.spinner_reporte);
        tilFechaInicio  = findViewById(R.id.til_fecha_inicio);
        tilFechaFin     = findViewById(R.id.til_fecha_fin);
        etFechaInicio   = findViewById(R.id.et_fecha_inicio);
        etFechaFin      = findViewById(R.id.et_fecha_fin);
        btnGenerar      = findViewById(R.id.btn_generar);
        lvReporte       = findViewById(R.id.lv_reporte);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filas);
        lvReporte.setAdapter(adapter);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TIPOS_REPORTE);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReporte.setAdapter(adapterSpinner);

        // Mostrar/ocultar campos de fecha según reporte elegido
        spinnerReporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean mostrarFechas = (position == 3); // "Por intervalo de fechas"
                tilFechaInicio.setVisibility(mostrarFechas ? View.VISIBLE : View.GONE);
                tilFechaFin.setVisibility(mostrarFechas ? View.VISIBLE : View.GONE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnGenerar.setOnClickListener(v -> generarReporte());
    }

    private void generarReporte() {
        int tipo = spinnerReporte.getSelectedItemPosition();

        Executors.newSingleThreadExecutor().execute(() -> {
            filas.clear();

            switch (tipo) {
                case 0: reporteLibrosMasUsados(); break;
                case 1: reporteUsuariosMasPrestamos(); break;
                case 2: reporteTodosConMora(); break;
                case 3: reportePorIntervalo(); break;
                case 4: reporteDescartados(); break;
                case 5: reporteDisponibles(); break;
            }

            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    private void reporteLibrosMasUsados() {
        List<PrestamoDao.LibroUsadoResult> lista = reporteService.getLibrosMasUsados();
        for (PrestamoDao.LibroUsadoResult r : lista)
            filas.add(r.tituloLibro + "  |  Préstamos: " + r.total + "  [" + r.codigoLibro + "]");
        if (lista.isEmpty()) filas.add("Sin datos.");
    }

    private void reporteUsuariosMasPrestamos() {
        List<PrestamoDao.UsuarioActivoResult> lista = reporteService.getUsuariosMasPrestamos();
        for (PrestamoDao.UsuarioActivoResult r : lista)
            filas.add(r.nombreUsuario + "  |  Préstamos: " + r.total + "  [" + r.codigoUsuario + "]");
        if (lista.isEmpty()) filas.add("Sin datos.");
    }

    private void reporteTodosConMora() {
        List<Prestamo> lista = reporteService.getTodosConMora();
        MoraService moraService = MoraService.getInstance();
        for (Prestamo p : lista) {
            String estado = p.isPerdido() ? "PERDIDO" : p.isEntregado() ? "ENTREGADO" : "ACTIVO";
            filas.add("ID:" + p.getId()
                    + "  " + p.getNombreUsuario()
                    + "  |  " + p.getTituloLibro()
                    + "  [" + estado + "]"
                    + "  Mora: S/" + String.format("%.2f", p.getMoraAcumulada()));
        }
        if (lista.isEmpty()) filas.add("Sin préstamos registrados.");
    }

    private void reportePorIntervalo() {
        String inicioStr = etFechaInicio.getText().toString().trim();
        String finStr    = etFechaFin.getText().toString().trim();

        if (inicioStr.isEmpty() || finStr.isEmpty()) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Ingrese ambas fechas.", Toast.LENGTH_SHORT).show());
            return;
        }

        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fin    = LocalDate.parse(finStr);

            if (fin.isBefore(inicio)) {
                runOnUiThread(() ->
                        Toast.makeText(this, "La fecha fin debe ser posterior a la fecha inicio.",
                                Toast.LENGTH_SHORT).show());
                return;
            }

            List<Prestamo> lista = reporteService.getPorIntervalo(inicio, fin);
            for (Prestamo p : lista)
                filas.add(p.getFechaPrestamo() + "  " + p.getNombreUsuario()
                        + "  |  " + p.getTituloLibro()
                        + "  Mora: S/" + String.format("%.2f", p.getMoraAcumulada()));
            if (lista.isEmpty()) filas.add("Sin préstamos en ese intervalo.");

        } catch (DateTimeParseException e) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Formato de fecha inválido. Use YYYY-MM-DD.", Toast.LENGTH_SHORT).show());
        }
    }

    private void reporteDescartados() {
        List<Libro> lista = reporteService.getLibrosDescartados();
        for (Libro l : lista)
            filas.add(l.getCod() + "  |  " + l.getTitulo()
                    + "  Stock: " + l.getStockDisponible() + "/" + l.getStockTotal());
        if (lista.isEmpty()) filas.add("No hay libros descartados.");
    }

    private void reporteDisponibles() {
        List<Libro> lista = reporteService.getLibrosDisponibles();
        for (Libro l : lista)
            filas.add(l.getCod() + "  |  " + l.getTitulo()
                    + "  Stock: " + l.getStockDisponible() + "/" + l.getStockTotal());
        if (lista.isEmpty()) filas.add("No hay libros disponibles.");
    }
}