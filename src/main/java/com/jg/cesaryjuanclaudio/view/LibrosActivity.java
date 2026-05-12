package com.jg.cesaryjuanclaudio.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

import com.jg.cesaryjuanclaudio.R;
import com.jg.cesaryjuanclaudio.entities.Libro;
import com.jg.cesaryjuanclaudio.entities.estados.EstadoLibro;
import com.jg.cesaryjuanclaudio.repositories.AppDatabase;
import com.jg.cesaryjuanclaudio.services.LibroService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class LibrosActivity extends AppCompatActivity {

    EditText etCod, etTitulo, etCategoria, etEditorial, etStock;
    Button btnAgregar, btnDescartar, btnRenovar, btnRetroceder;
    ListView lvLibros;

    AppDatabase database;
    LibroService libroService;
    ArrayAdapter<String> adapter;
    List<String> filas = new ArrayList<>();

    List<Libro> listaLibros = new ArrayList<>();

    Libro libroSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = AppDatabase.getDatabase(this);
        libroService = new LibroService(database);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_libros);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnRetroceder = findViewById(R.id.btn_retroceder);
        btnAgregar    = findViewById(R.id.btn_agregar_libro);
        btnDescartar  = findViewById(R.id.btn_descartar);
        btnRenovar    = findViewById(R.id.btn_renovar);
        etCod         = findViewById(R.id.et_cod_libro);
        etTitulo      = findViewById(R.id.et_titulo_libro);
        etCategoria   = findViewById(R.id.et_categoria);
        etEditorial   = findViewById(R.id.et_editorial);
        etStock       = findViewById(R.id.et_stock);
        lvLibros      = findViewById(R.id.lv_libros);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filas);
        lvLibros.setAdapter(adapter);

        cargarLibros();

        btnRetroceder.setOnClickListener(v -> finish());

        lvLibros.setOnItemClickListener((parent, view, position, id) -> {
            libroSeleccionado = listaLibros.get(position);
            cargarEnCampos(libroSeleccionado);
            modoEdicion(true);
        });

        btnAgregar.setOnClickListener(v -> {
            if (libroSeleccionado == null) {
                agregarLibro();
            } else {
                modificarLibro();
            }
        });

        btnDescartar.setOnClickListener(v -> descartarLibro());

        btnRenovar.setOnClickListener(v -> renovarLibro());
    }

    private void agregarLibro() {
        String cod      = etCod.getText().toString().trim();
        String titulo   = etTitulo.getText().toString().trim();
        String cat      = etCategoria.getText().toString().trim();
        String edit     = etEditorial.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();

        if (cod.isEmpty() || titulo.isEmpty() || cat.isEmpty()
                || edit.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int stock;
        try {
            stock = Integer.parseInt(stockStr);
            if (stock <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El stock debe ser mayor a 0.", Toast.LENGTH_SHORT).show();
            return;
        }

        Libro libro = new Libro(null, cod, titulo, cat, edit, stock, stock, EstadoLibro.DISPONIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            Libro libroExistente = libroService.buscarPorCodigo(cod);
            if (libroExistente != null) {
                runOnUiThread(() -> Toast.makeText(this, "Error: El código " + cod + " ya pertenece a otro libro.", Toast.LENGTH_LONG).show());
                return;
            }

            libroService.registrar(libro);
            runOnUiThread(() -> {
                Toast.makeText(this, "Libro registrado.", Toast.LENGTH_SHORT).show();
                limpiarYRecargar();
            });
        });
    }

    private void modificarLibro() {
        String titulo   = etTitulo.getText().toString().trim();
        String cat      = etCategoria.getText().toString().trim();
        String edit     = etEditorial.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();

        if (titulo.isEmpty() || cat.isEmpty() || edit.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int stock;
        try {
            stock = Integer.parseInt(stockStr);
            if (stock <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El stock debe ser mayor a 0.", Toast.LENGTH_SHORT).show();
            return;
        }

        int diferencia = stock - libroSeleccionado.getStockTotal();
        int nuevoDisponible = Math.max(0, libroSeleccionado.getStockDisponible() + diferencia);

        libroSeleccionado.setTitulo(titulo);
        libroSeleccionado.setCategoria(cat);
        libroSeleccionado.setEditorial(edit);
        libroSeleccionado.setStockTotal(stock);
        libroSeleccionado.setStockDisponible(nuevoDisponible);

        Libro copia = libroSeleccionado;
        Executors.newSingleThreadExecutor().execute(() -> {
            libroService.actualizar(copia);
            runOnUiThread(() -> {
                Toast.makeText(this, "Libro actualizado.", Toast.LENGTH_SHORT).show();
                limpiarYRecargar();
            });
        });
    }

    private void descartarLibro() {
        if (libroSeleccionado == null) return;

        Libro copia = libroSeleccionado;
        Executors.newSingleThreadExecutor().execute(() -> {
            String error = libroService.descartar(copia);
            runOnUiThread(() -> {
                if (error != null) {
                    // Tiene préstamos activos
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Libro descartado.", Toast.LENGTH_SHORT).show();
                    limpiarYRecargar();
                }
            });
        });
    }

    private void renovarLibro() {
        if (libroSeleccionado == null) return;

        if (libroSeleccionado.getEstado() != EstadoLibro.DESCARTADO) {
            Toast.makeText(this, "Solo se pueden renovar libros descartados.", Toast.LENGTH_SHORT).show();
            return;
        }

        Libro copia = libroSeleccionado;
        Executors.newSingleThreadExecutor().execute(() -> {
            libroService.renovar(copia, 0);
            runOnUiThread(() -> {
                Toast.makeText(this, "Libro renovado. Estado: DISPONIBLE.", Toast.LENGTH_SHORT).show();
                limpiarYRecargar();
            });
        });
    }

    private void cargarEnCampos(Libro libro) {
        etCod.setText(libro.getCod());
        etCod.setEnabled(false);
        etTitulo.setText(libro.getTitulo());
        etCategoria.setText(libro.getCategoria());
        etEditorial.setText(libro.getEditorial());
        etStock.setText(String.valueOf(libro.getStockTotal()));
    }

    private void modoEdicion(boolean editando) {
        if (editando) {
            btnAgregar.setText("Modificar");
            btnDescartar.setEnabled(true);
            // Renovar solo se habilita si el libro está descartado
            btnRenovar.setEnabled(
                    libroSeleccionado != null
                            && libroSeleccionado.getEstado() == EstadoLibro.DESCARTADO
            );
        } else {
            btnAgregar.setText("Agregar");
            btnDescartar.setEnabled(false);
            btnRenovar.setEnabled(false);
            etCod.setEnabled(true);
            libroSeleccionado = null;
        }
    }

    private void limpiarYRecargar() {
        etCod.setText("");
        etTitulo.setText("");
        etCategoria.setText("");
        etEditorial.setText("");
        etStock.setText("");
        modoEdicion(false);
        cargarLibros();
    }

    private void cargarLibros() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Libro> libros = libroService.listarTodos();
            filas.clear();
            listaLibros.clear();
            listaLibros.addAll(libros);
            for (Libro l : libros)
                filas.add(l.getCod() + "  |  " + l.getTitulo()
                        + "\n[" + l.getEstado() + "]"
                        + "  Stock: " + l.getStockDisponible() + "/" + l.getStockTotal());
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarLibros();
    }
}