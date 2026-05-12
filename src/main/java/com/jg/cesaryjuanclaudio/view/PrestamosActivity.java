package com.jg.cesaryjuanclaudio.view;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

import com.jg.cesaryjuanclaudio.R;
import com.jg.cesaryjuanclaudio.entities.Prestamo;
import com.jg.cesaryjuanclaudio.entities.Usuario;
import com.jg.cesaryjuanclaudio.entities.estados.TipoUsuarios;
import com.jg.cesaryjuanclaudio.repositories.AppDatabase;
import com.jg.cesaryjuanclaudio.services.MoraService;
import com.jg.cesaryjuanclaudio.services.PrestamoService;
import com.jg.cesaryjuanclaudio.services.UsuarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class PrestamosActivity extends AppCompatActivity {

    EditText etCodUsuario, etCodLibro, etDias, etIdPrestamo;
    Spinner spinnerTipo;
    CheckBox cbPerdido;
    Button btnPrestar, btnDevolver;
    ListView lvPrestamos;

    AppDatabase database;
    PrestamoService prestamoService;
    UsuarioService usuarioService;
    ArrayAdapter<String> adapter;
    List<String> filas = new ArrayList<>();

    // Para guardar temporalmente el préstamo encontrado
    Prestamo prestamoSeleccionado = null;
    Usuario usuarioSeleccionado   = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database       = AppDatabase.getDatabase(this);
        prestamoService= new PrestamoService(database);
        usuarioService = new UsuarioService(database);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prestamos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etCodUsuario = findViewById(R.id.et_cod_usuario_prestamo);
        etCodLibro   = findViewById(R.id.et_cod_libro_prestamo);
        etDias       = findViewById(R.id.et_dias);
        etIdPrestamo = findViewById(R.id.et_id_prestamo);
        spinnerTipo  = findViewById(R.id.spinner_tipo_usuario);
        cbPerdido    = findViewById(R.id.cb_perdido);
        btnPrestar   = findViewById(R.id.btn_prestar);
        btnDevolver  = findViewById(R.id.btn_devolver);
        lvPrestamos  = findViewById(R.id.lv_prestamos);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Estudiante", "Docente", "Administrativo"});
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterSpinner);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filas);
        lvPrestamos.setAdapter(adapter);

        cargarPrestamos();

        btnPrestar.setOnClickListener(v -> registrarPrestamo());
        btnDevolver.setOnClickListener(v -> registrarDevolucion());
    }

    private void registrarPrestamo() {
        String codUsuario = etCodUsuario.getText().toString().trim();
        String codLibro   = etCodLibro.getText().toString().trim();
        String diasStr    = etDias.getText().toString().trim();

        if (codUsuario.isEmpty() || codLibro.isEmpty() || diasStr.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int dias;
        try {
            dias = Integer.parseInt(diasStr);
            if (dias <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Los días deben ser un número mayor a 0.", Toast.LENGTH_SHORT).show();
            return;
        }

        TipoUsuarios tipo = getTipoSeleccionado();

        Executors.newSingleThreadExecutor().execute(() -> {
            Usuario usuario = usuarioService.buscarPorCodigo(codUsuario, tipo);

            if (usuario == null) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Usuario no encontrado.", Toast.LENGTH_SHORT).show());
                return;
            }

            String error = prestamoService.prestarLibro(usuario, codLibro, dias);

            runOnUiThread(() -> {
                if (error != null) {
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Préstamo registrado correctamente.", Toast.LENGTH_SHORT).show();
                    etCodUsuario.setText(""); etCodLibro.setText(""); etDias.setText("");
                    cargarPrestamos();
                }
            });
        });
    }

    private void registrarDevolucion() {
        String idStr = etIdPrestamo.getText().toString().trim();

        if (idStr.isEmpty()) {
            Toast.makeText(this, "Ingrese el ID del préstamo.", Toast.LENGTH_SHORT).show();
            return;
        }

        int id;
        try { id = Integer.parseInt(idStr); }
        catch (NumberFormatException e) {
            Toast.makeText(this, "ID inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean perdido = cbPerdido.isChecked();

        Executors.newSingleThreadExecutor().execute(() -> {
            Prestamo prestamo = prestamoService.getPrestamoById(id);

            if (prestamo == null) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Préstamo no encontrado.", Toast.LENGTH_SHORT).show());
                return;
            }

            if (prestamo.isEntregado() || prestamo.isPerdido()) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Este préstamo ya fue cerrado.", Toast.LENGTH_SHORT).show());
                return;
            }

            TipoUsuarios tipo = prestamo.getTipoUsuario();
            Usuario usuario = usuarioService.buscarPorCodigo(prestamo.getCodigoUsuario(), tipo);

            String resultado = prestamoService.devolverLibro(prestamo, usuario, perdido);

            runOnUiThread(() -> {
                String msg = resultado != null ? resultado : "Devolución registrada sin mora.";
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                etIdPrestamo.setText("");
                cbPerdido.setChecked(false);
                cargarPrestamos();
            });
        });
    }

    private void cargarPrestamos() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Prestamo> lista = prestamoService.getTodosPrestamos();
            filas.clear();
            MoraService moraService = MoraService.getInstance();
            for (Prestamo p : lista) {
                double mora = moraService.calcularMoraActual(p);
                String estado = p.isPerdido() ? "PERDIDO"
                        : p.isEntregado() ? "ENTREGADO"
                        : "ACTIVO";
                filas.add("ID:" + p.getId()
                        + "  " + p.getNombreUsuario()
                        + "  |  " + p.getTituloLibro()
                        + "  [" + estado + "]"
                        + "  Mora: S/" + mora
                        + "  Límite: " + p.getFechaLimite());
            }
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    private TipoUsuarios getTipoSeleccionado() {
        int pos = spinnerTipo.getSelectedItemPosition();
        switch (pos) {
            case 1:  return TipoUsuarios.DOCENTE;
            case 2:  return TipoUsuarios.ADMINISTRATIVO;
            default: return TipoUsuarios.ESTUDIANTE;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPrestamos();
    }
}