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
    Button btnAgregar;
    ListView lvLibros;

    AppDatabase database;
    LibroService libroService;
    ArrayAdapter<String> adapter;
    List<String> filas = new ArrayList<>();

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

        etCod       = findViewById(R.id.et_cod_libro);
        etTitulo    = findViewById(R.id.et_titulo_libro);
        etCategoria = findViewById(R.id.et_categoria);
        etEditorial = findViewById(R.id.et_editorial);
        etStock     = findViewById(R.id.et_stock);
        btnAgregar  = findViewById(R.id.btn_agregar_libro);
        lvLibros    = findViewById(R.id.lv_libros);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filas);
        lvLibros.setAdapter(adapter);

        cargarLibros();

        btnAgregar.setOnClickListener(v -> agregarLibro());
    }

    private void agregarLibro() {
        String cod      = etCod.getText().toString().trim();
        String titulo   = etTitulo.getText().toString().trim();
        String cat      = etCategoria.getText().toString().trim();
        String edit     = etEditorial.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();

        if (cod.isEmpty() || titulo.isEmpty() || cat.isEmpty() || edit.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int stock;
        try {
            stock = Integer.parseInt(stockStr);
            if (stock <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El stock debe ser un número mayor a 0.", Toast.LENGTH_SHORT).show();
            return;
        }

        Libro libro = new Libro(null, cod, titulo, cat, edit, stock, stock, EstadoLibro.DISPONIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            libroService.registrar(libro);
            runOnUiThread(() -> {
                Toast.makeText(this, "Libro registrado.", Toast.LENGTH_SHORT).show();
                etCod.setText(""); etTitulo.setText(""); etCategoria.setText("");
                etEditorial.setText(""); etStock.setText("");
                cargarLibros();
            });
        });
    }

    private void cargarLibros() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Libro> libros = libroService.listarTodos();
            filas.clear();
            for (Libro l : libros)
                filas.add(l.getCod() + "  |  " + l.getTitulo()
                        + "\n[" + l.getEstado() + "]"
                        + "\nStock: " + l.getStockDisponible() + "  |  " + l.getStockTotal());
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarLibros();
    }
}