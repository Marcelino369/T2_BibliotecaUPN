package com.jg.cesaryjuanclaudio.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.jg.cesaryjuanclaudio.R;
import com.jg.cesaryjuanclaudio.entities.Administrativo;
import com.jg.cesaryjuanclaudio.entities.Docente;
import com.jg.cesaryjuanclaudio.entities.Estudiante;
import com.jg.cesaryjuanclaudio.repositories.AppDatabase;
import com.jg.cesaryjuanclaudio.services.UsuarioService;

import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    RadioGroup rgUsuario;
    RadioButton rbEstudiante, rbDocente, rbPersonal;

    EditText etNombre, etApellido, etCorreo, etContrasena,
            etCodigoEstudiante, etCarrera,
            etCodigoDocente, etFacultad,
            etCodigoPersonal, etArea;

    TextInputLayout tilCodigoEstudiante, tilCarrera,
            tilCodigoDocente, tilFacultad,
            tilCodigoPersonal, tilArea;

    Button btnRegistrar;

    AppDatabase database;
    UsuarioService usuarioService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = AppDatabase.getDatabase(this);
        usuarioService = new UsuarioService(database);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        vincularVistas();
        configurarRadioGroup();
    }


    private void vincularVistas() {
        rgUsuario    = findViewById(R.id.rg_usuario);
        rbEstudiante = findViewById(R.id.rb_estudiante);
        rbDocente    = findViewById(R.id.rb_docente);
        rbPersonal   = findViewById(R.id.rb_pAdministrativo);

        etNombre          = findViewById(R.id.et_nombre);
        etApellido        = findViewById(R.id.et_apellido);
        etCorreo          = findViewById(R.id.et_correo);
        etContrasena      = findViewById(R.id.et_contrasena);
        etCodigoEstudiante= findViewById(R.id.et_codigoEstudiante);
        etCarrera         = findViewById(R.id.et_carrera);
        etCodigoDocente   = findViewById(R.id.et_codigoDocente);
        etFacultad        = findViewById(R.id.et_facultad);
        etCodigoPersonal  = findViewById(R.id.et_codigoPersonal);
        etArea            = findViewById(R.id.et_area);

        tilCodigoEstudiante = findViewById(R.id.til_codigoEstudiante);
        tilCarrera          = findViewById(R.id.til_carrera);
        tilCodigoDocente    = findViewById(R.id.til_codigoDocente);
        tilFacultad         = findViewById(R.id.til_facultad);
        tilCodigoPersonal   = findViewById(R.id.til_codigoPersonal);
        tilArea             = findViewById(R.id.til_area);

        btnRegistrar = findViewById(R.id.btn_registrar);
    }

    private void configurarRadioGroup() {
        rgUsuario.setOnCheckedChangeListener((group, checkedId) -> {
            // Ocultar todo primero
            ocultarCamposEspecificos();

            if (checkedId == R.id.rb_estudiante) {
                tilCodigoEstudiante.setVisibility(View.VISIBLE);
                tilCarrera.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_docente) {
                tilCodigoDocente.setVisibility(View.VISIBLE);
                tilFacultad.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_pAdministrativo) {
                tilCodigoPersonal.setVisibility(View.VISIBLE);
                tilArea.setVisibility(View.VISIBLE);
            }

            // El click del botón se asigna una sola vez aquí
            btnRegistrar.setOnClickListener(v -> intentarRegistrar(checkedId));
        });
    }

    private void ocultarCamposEspecificos() {
        tilCodigoEstudiante.setVisibility(View.GONE);
        tilCarrera.setVisibility(View.GONE);
        tilCodigoDocente.setVisibility(View.GONE);
        tilFacultad.setVisibility(View.GONE);
        tilCodigoPersonal.setVisibility(View.GONE);
        tilArea.setVisibility(View.GONE);
    }

    // Un solo punto de entrada para registrar según el radio seleccionado
    private void intentarRegistrar(int checkedId) {
        String nombre     = etNombre.getText().toString().trim();
        String apellido   = etApellido.getText().toString().trim();
        String correo     = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        // Validaciones comunes
        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!correo.endsWith("@upn.pe")) {
            Toast.makeText(this, "El correo debe ser institucional (@upn.pe).", Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkedId == R.id.rb_estudiante) {
            registrarEstudiante(nombre, apellido, correo, contrasena);
        } else if (checkedId == R.id.rb_docente) {
            registrarDocente(nombre, apellido, correo, contrasena);
        } else if (checkedId == R.id.rb_pAdministrativo) {
            registrarPersonal(nombre, apellido, correo, contrasena);
        } else {
            Toast.makeText(this, "Seleccione un tipo de usuario.", Toast.LENGTH_SHORT).show();
        }
    }

    private void registrarEstudiante(String nombre, String apellido, String correo, String contrasena) {
        String cod    = etCodigoEstudiante.getText().toString().trim();
        String carrera= etCarrera.getText().toString().trim();

        if (cod.isEmpty() || carrera.isEmpty()) {
            Toast.makeText(this, "Complete código y carrera.", Toast.LENGTH_SHORT).show();
            return;
        }

        Estudiante e = new Estudiante(nombre, apellido, correo, contrasena, cod, carrera);

        Executors.newSingleThreadExecutor().execute(() -> {
            usuarioService.registrarEstudiante(e);
            runOnUiThread(() -> {
                Toast.makeText(this, "Estudiante registrado correctamente.", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            });
        });
    }

    private void registrarDocente(String nombre, String apellido, String correo, String contrasena) {
        String cod     = etCodigoDocente.getText().toString().trim();
        String facultad= etFacultad.getText().toString().trim();

        if (cod.isEmpty() || facultad.isEmpty()) {
            Toast.makeText(this, "Complete código y facultad.", Toast.LENGTH_SHORT).show();
            return;
        }

        Docente d = new Docente(nombre, apellido, correo, contrasena, cod, facultad);

        Executors.newSingleThreadExecutor().execute(() -> {
            usuarioService.registrarDocente(d);
            runOnUiThread(() -> {
                Toast.makeText(this, "Docente registrado correctamente.", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            });
        });
    }

    private void registrarPersonal(String nombre, String apellido, String correo, String contrasena) {
        String cod  = etCodigoPersonal.getText().toString().trim();
        String area = etArea.getText().toString().trim();

        if (cod.isEmpty() || area.isEmpty()) {
            Toast.makeText(this, "Complete código y área.", Toast.LENGTH_SHORT).show();
            return;
        }

        Administrativo a = new Administrativo(nombre, apellido, correo, contrasena, cod, area);

        Executors.newSingleThreadExecutor().execute(() -> {
            usuarioService.registrarAdministrativo(a);
            runOnUiThread(() -> {
                Toast.makeText(this, "Personal registrado correctamente.", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            });
        });
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etApellido.setText("");
        etCorreo.setText("");
        etContrasena.setText("");
        etCodigoEstudiante.setText("");
        etCarrera.setText("");
        etCodigoDocente.setText("");
        etFacultad.setText("");
        etCodigoPersonal.setText("");
        etArea.setText("");
        rgUsuario.clearCheck();
        ocultarCamposEspecificos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}