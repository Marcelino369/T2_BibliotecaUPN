package com.jg.cesaryjuanclaudio.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

import com.jg.cesaryjuanclaudio.R;
import com.jg.cesaryjuanclaudio.entities.Administrativo;
import com.jg.cesaryjuanclaudio.entities.Docente;
import com.jg.cesaryjuanclaudio.entities.Estudiante;
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

        spinnerTipo = findViewById(R.id.spinner_tipo);
        lvUsuarios  = findViewById(R.id.lv_usuarios);

        // Adapter de la lista
        adapterLista = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filas);
        lvUsuarios.setAdapter(adapterLista);

        // Spinner con los 3 tipos
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
    }

    private void cargarSegunTipo(int tipo) {
        Executors.newSingleThreadExecutor().execute(() -> {
            filas.clear();

            if (tipo == 0) {
                List<Estudiante> lista = usuarioService.listarEstudiantes();
                for (Estudiante e : lista)
                    filas.add(e.getCodEstudiante() + "  |  " + e.getNombre() + " " + e.getApellido()
                            + "  [" + e.getEstado() + "]");

            } else if (tipo == 1) {
                List<Docente> lista = usuarioService.listarDocentes();
                for (Docente d : lista)
                    filas.add(d.getCodDocente() + "  |  " + d.getNombre() + " " + d.getApellido()
                            + "  [" + d.getEstado() + "]");

            } else {
                List<Administrativo> lista = usuarioService.listarAdministrativos();
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
        // Recargar al volver de otra pantalla
        cargarSegunTipo(spinnerTipo.getSelectedItemPosition());
    }
}