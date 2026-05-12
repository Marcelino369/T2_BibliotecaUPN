package com.jg.cesaryjuanclaudio.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

import com.jg.cesaryjuanclaudio.R;
import com.jg.cesaryjuanclaudio.entities.Administrativo;
import com.jg.cesaryjuanclaudio.entities.Docente;
import com.jg.cesaryjuanclaudio.entities.Estudiante;
import com.jg.cesaryjuanclaudio.entities.Usuario;
import com.jg.cesaryjuanclaudio.repositories.AppDatabase;
import com.jg.cesaryjuanclaudio.services.UsuarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ListaUsuariosActivity extends AppCompatActivity {

    Spinner spinnerTipo;
    ListView lvUsuarios;
    AppDatabase database;
    UsuarioService usuarioService;
    ArrayAdapter<String> adapterLista;
    List<String> filas = new ArrayList<>();

    // NUEVO: Lista para mantener los objetos reales en sincronía con los Strings
    List<Usuario> listaUsuariosActual = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = AppDatabase.getDatabase(this);
        usuarioService = new UsuarioService(database);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_usuarios);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnRegistrarUsuarios = findViewById(R.id.btn_registrarUsuarios);
        btnRegistrarUsuarios.setOnClickListener(v -> {
            Intent act = new Intent(this, RegisterActivity.class);
            startActivity(act);
        });

        Button btnRegistroLibros = findViewById(R.id.btn_registroLibros);
        btnRegistroLibros.setOnClickListener(v -> {
            Intent act = new Intent(this, LibrosActivity.class);
            startActivity(act);
        });

        Button btnPrestamos = findViewById(R.id.btn_prestamos);
        btnPrestamos.setOnClickListener(v -> {
            Intent act = new Intent(this, PrestamosActivity.class);
            startActivity(act);
        });

        Button btn_reportes = findViewById(R.id.btn_reportes);
        btn_reportes.setOnClickListener(v -> {
            Intent act = new Intent(this, ReportesActivity.class);
            startActivity(act);
        });

        spinnerTipo = findViewById(R.id.spinner_tipo);
        lvUsuarios  = findViewById(R.id.lv_usuarios);

        adapterLista = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filas);
        lvUsuarios.setAdapter(adapterLista);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Estudiantes", "Docentes", "Administrativos"});

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterSpinner);

        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarSegunTipo(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ARREGLADO: Enviar el código y tipo al RegisterActivity
        lvUsuarios.setOnItemClickListener((parent, view, position, id) -> {
            Usuario usuarioSeleccionado = listaUsuariosActual.get(position);

            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("EXTRA_CODIGO", usuarioSeleccionado.getCodigoUsuario());
            intent.putExtra("EXTRA_TIPO", usuarioSeleccionado.getTipoUsuario().name());

            startActivity(intent);
        });
    }

    private void cargarSegunTipo(int tipo) {
        Executors.newSingleThreadExecutor().execute(() -> {
            filas.clear();
            listaUsuariosActual.clear(); // Limpiamos la lista de objetos

            if (tipo == 0) {
                List<Estudiante> lista = usuarioService.listarEstudiantes();
                listaUsuariosActual.addAll(lista); // Guardamos los objetos
                for (Estudiante e : lista)
                    filas.add(e.getCodEstudiante() + "  |  " + e.getNombre() + " " + e.getApellido()
                            + "  [" + e.getEstado() + "]");

            } else if (tipo == 1) {
                List<Docente> lista = usuarioService.listarDocentes();
                listaUsuariosActual.addAll(lista); // Guardamos los objetos
                for (Docente d : lista)
                    filas.add(d.getCodDocente() + "  |  " + d.getNombre() + " " + d.getApellido()
                            + "  [" + d.getEstado() + "]");

            } else {
                List<Administrativo> lista = usuarioService.listarAdministrativos();
                listaUsuariosActual.addAll(lista); // Guardamos los objetos
                for (Administrativo a : lista)
                    filas.add(a.getCodAdmin() + "  |  " + a.getNombre() + " " + a.getApellido()
                            + "  [" + a.getEstado() + "]");
            }

            runOnUiThread(() -> adapterLista.notifyDataSetChanged());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarSegunTipo(spinnerTipo.getSelectedItemPosition());
    }
}