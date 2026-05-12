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

    private TextInputLayout tilMail;
    private TextInputLayout tilPassword;
    private TextInputEditText etCorreo;
    private TextInputEditText etPassword;

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

        tilMail = findViewById(R.id.til_mail);
        tilPassword = findViewById(R.id.til_password);

        etCorreo = findViewById(R.id.et_correo);
        etPassword = findViewById(R.id.et_password);
        Button btnSignIn = findViewById(R.id.btn_signIn);

        btnSignIn.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String correo = etCorreo.getText().toString();
        String password = etPassword.getText().toString();

        tilMail.setError(null);
        tilPassword.setError(null);

        if (correo.isBlank()) {
            tilMail.setError("Error: El correo es requerido");
            return;
        }
        if (password.isBlank()) {
            tilPassword.setError("Error: La contraseña es requerida");
            return;
        }

        boolean credencialesValidas = AuthService.getInstance().autenticar(correo, password);

        if (credencialesValidas) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, ListaUsuariosActivity.class);
            startActivity(intent);
            finish();
        } else {
            tilMail.setError("Correo o contraseña incorrectos");
            tilPassword.setError("Correo o contraseña incorrectos");
        }
    }
}