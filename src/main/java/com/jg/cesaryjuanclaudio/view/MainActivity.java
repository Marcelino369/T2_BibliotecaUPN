package com.jg.cesaryjuanclaudio.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jg.cesaryjuanclaudio.R;
import com.jg.cesaryjuanclaudio.services.AuthService;

public class MainActivity extends AppCompatActivity {

    // 1. Declarar las vistas
    private TextInputLayout tilMail;
    private TextInputLayout tilPassword;
    private TextInputEditText etCorreo;
    private TextInputEditText etPassword;
    private Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 2. Vincular las vistas con el XML
        tilMail = findViewById(R.id.til_mail);
        tilPassword = findViewById(R.id.til_password);
        etCorreo = findViewById(R.id.et_correo);
        etPassword = findViewById(R.id.et_password);
        btnSignIn = findViewById(R.id.btn_signIn);

        // 3. Configurar el evento del botón
        btnSignIn.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        tilMail.setError(null);
        tilPassword.setError(null);

        if (correo.isEmpty()) {
            tilMail.setError("El correo es requerido");
            return;
        }
        if (password.isEmpty()) {
            tilPassword.setError("La contraseña es requerida");
            return;
        }

        boolean credencialesValidas = AuthService.getInstance().autenticar(correo, password);

        if (credencialesValidas) {
            Toast.makeText(this, "Bienvenido al sistema", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, ReportesActivity.class); // <-- Cambia esto si tu Activity se llama diferente
            startActivity(intent);

            finish();
        } else {
            tilPassword.setError("Correo o contraseña incorrectos");
        }
    }
}