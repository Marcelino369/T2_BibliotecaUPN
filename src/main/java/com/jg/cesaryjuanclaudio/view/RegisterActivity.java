package com.jg.cesaryjuanclaudio.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import com.jg.cesaryjuanclaudio.entities.Prestamo;
import com.jg.cesaryjuanclaudio.entities.Usuario;
import com.jg.cesaryjuanclaudio.entities.estados.EstadoUsuario;
import com.jg.cesaryjuanclaudio.entities.estados.TipoUsuarios;
import com.jg.cesaryjuanclaudio.repositories.AppDatabase;
import com.jg.cesaryjuanclaudio.services.PrestamoService;
import com.jg.cesaryjuanclaudio.services.UsuarioService;

import java.util.List;
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
    TextView tvTitulo;

    Button btnRegistrar, btnEstado;

    AppDatabase database;
    UsuarioService usuarioService;
    PrestamoService prestamoService;

    Usuario usuarioSeleccionado = null;
    boolean modoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = AppDatabase.getDatabase(this);
        usuarioService = new UsuarioService(database);
        prestamoService = new PrestamoService(database); // NUEVO

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button btnRetroceder  = findViewById(R.id.btn_retroceder);
        if(btnRetroceder != null) {
            btnRetroceder.setOnClickListener(v -> finish());
        }

        vincularVistas();
        configurarRadioGroup();
        verificarModoEdicion();
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
        btnEstado  = findViewById(R.id.btn_estado);

        tvTitulo = findViewById(R.id.tv_titulo);
    }

    private void configurarRadioGroup() {
        rgUsuario.setOnCheckedChangeListener((group, checkedId) -> {
            if(!modoEdicion) ocultarCamposEspecificos();

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

            btnRegistrar.setOnClickListener(v -> {
                if (modoEdicion) {
                    modificarUsuarioActual();
                } else {
                    intentarRegistrar(checkedId);
                }
            });
        });
    }

    private void verificarModoEdicion() {
        String codigoExtra = getIntent().getStringExtra("EXTRA_CODIGO");
        String tipoExtra = getIntent().getStringExtra("EXTRA_TIPO");

        if (codigoExtra != null && tipoExtra != null) {
            modoEdicion = true;
            TipoUsuarios tipo = TipoUsuarios.valueOf(tipoExtra);

            Executors.newSingleThreadExecutor().execute(() -> {
                usuarioSeleccionado = usuarioService.buscarPorCodigo(codigoExtra, tipo);
                runOnUiThread(() -> cargarDatosEnVista(usuarioSeleccionado));
            });
        }
    }

    private void cargarDatosEnVista(Usuario u) {
        if (u == null) return;

        etNombre.setText(u.getNombre());
        etApellido.setText(u.getApellido());
        etCorreo.setText(u.getCorreo());
        etContrasena.setText(u.getContrasena());

        for(int i = 0; i < rgUsuario.getChildCount(); i++){
            rgUsuario.getChildAt(i).setEnabled(false);
        }

        if (u instanceof Estudiante) {
            rbEstudiante.setChecked(true);
            etCodigoEstudiante.setText(((Estudiante) u).getCodEstudiante());
            etCarrera.setText(((Estudiante) u).getCarrera());
            etCodigoEstudiante.setEnabled(false);
        } else if (u instanceof Docente) {
            rbDocente.setChecked(true);
            etCodigoDocente.setText(((Docente) u).getCodDocente());
            etFacultad.setText(((Docente) u).getFacultad());
            etCodigoDocente.setEnabled(false);
        } else if (u instanceof Administrativo) {
            rbPersonal.setChecked(true);
            etCodigoPersonal.setText(((Administrativo) u).getCodAdmin());
            etArea.setText(((Administrativo) u).getArea());
            etCodigoPersonal.setEnabled(false);
        }

        tvTitulo.setText("Modificar Usuario");
        btnRegistrar.setText("Modificar");
        configurarBotonesEstado(u);
    }

    private void configurarBotonesEstado(Usuario u) {
        if (u.getEstado() == EstadoUsuario.ACTIVO) {
            btnEstado.setText("Desactivar");
            btnEstado.setVisibility(View.VISIBLE);
        } else {
            btnEstado.setText("Activar");
            btnEstado.setVisibility(View.VISIBLE);
        }

        String _estado = btnEstado.getText().toString();

        if ( _estado.equals("Desactivar") ){
            btnEstado.setOnClickListener(v -> {
                Executors.newSingleThreadExecutor().execute(() -> {
                    List<Prestamo> prestamosActivos = prestamoService.getPrestamosActivosDeUsuario(u.getCodigoUsuario());

                    if (prestamosActivos != null && !prestamosActivos.isEmpty()) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "No se puede bloquear: El usuario tiene " + prestamosActivos.size() + " préstamo(s) activo(s).", Toast.LENGTH_LONG).show();
                        });
                    } else {
                        usuarioService.bloquear(u);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Usuario Bloqueado", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                });
            });
        }else if (_estado.equals("Activar")){

            btnEstado.setOnClickListener(v -> {
                Executors.newSingleThreadExecutor().execute(() -> {
                    usuarioService.activar(u);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Usuario Activado", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                });
            });
        }
    }

    private void modificarUsuarioActual() {
        usuarioSeleccionado.setNombre(etNombre.getText().toString().trim());
        usuarioSeleccionado.setApellido(etApellido.getText().toString().trim());
        usuarioSeleccionado.setCorreo(etCorreo.getText().toString().trim());
        usuarioSeleccionado.setContrasena(etContrasena.getText().toString().trim());

        if (usuarioSeleccionado instanceof Estudiante) {
            ((Estudiante) usuarioSeleccionado).setCarrera(etCarrera.getText().toString().trim());
        } else if (usuarioSeleccionado instanceof Docente) {
            ((Docente) usuarioSeleccionado).setFacultad(etFacultad.getText().toString().trim());
        } else if (usuarioSeleccionado instanceof Administrativo) {
            ((Administrativo) usuarioSeleccionado).setArea(etArea.getText().toString().trim());
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            usuarioService.actualizarUsuario(usuarioSeleccionado);
            runOnUiThread(() -> {
                Toast.makeText(this, "Usuario actualizado correctamente.", Toast.LENGTH_SHORT).show();
                finish();
            });
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

    private void intentarRegistrar(int checkedId) {
        String nombre     = etNombre.getText().toString().trim();
        String apellido   = etApellido.getText().toString().trim();
        String correo     = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

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
            Usuario usuarioExistente = usuarioService.buscarPorCodigo(cod, TipoUsuarios.ESTUDIANTE);
            if (usuarioExistente != null) {
                runOnUiThread(() -> Toast.makeText(this, "Error: El código " + cod + " ya está registrado.", Toast.LENGTH_LONG).show());
                return;
            }

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
            Usuario usuarioExistente = usuarioService.buscarPorCodigo(cod, TipoUsuarios.DOCENTE);
            if (usuarioExistente != null) {
                runOnUiThread(() -> Toast.makeText(this, "Error: El código " + cod + " ya está registrado.", Toast.LENGTH_LONG).show());
                return;
            }

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
            Usuario usuarioExistente = usuarioService.buscarPorCodigo(cod, TipoUsuarios.ADMINISTRATIVO);
            if (usuarioExistente != null) {
                runOnUiThread(() -> Toast.makeText(this, "Error: El código " + cod + " ya está registrado.", Toast.LENGTH_LONG).show());
                return;
            }

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
}