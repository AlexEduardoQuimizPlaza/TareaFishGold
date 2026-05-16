package com.example.tareafishgold;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText correo, clave;
    private CheckBox recordar;
    private BaseDatosSQLite dbHelper; // Instancia para la base de datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Inicializar DB Helper
        dbHelper = new BaseDatosSQLite(this);
        UsuarioInicial.asegurarCuentaDemo(dbHelper);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        correo = findViewById(R.id.txt_correo);
        clave = findViewById(R.id.txt_password);
        recordar = findViewById(R.id.login_chkrecordar);

        cargarCredencialesSP();

        if (correo != null && correo.getText().toString().trim().isEmpty()) {
            correo.setText(UsuarioInicial.CEDULA);
            clave.setText(UsuarioInicial.PASSWORD);
        }

        findViewById(R.id.btn_entrar_demo).setOnClickListener(v -> entrarComoDemo());

        Log.d("Lifecycle", "LoginActivity: OnCreate");
    }

    public void entrarComoDemo(View v) {
        entrarComoDemo();
    }

    private void entrarComoDemo() {
        if (correo != null) correo.setText(UsuarioInicial.CEDULA);
        if (clave != null) clave.setText(UsuarioInicial.PASSWORD);
        validarIngreso(null);
    }

    // --- Lógica de Base de Datos ---

    public void validarIngreso(View v) {
        String user = correo.getText().toString().trim(); // Aquí se ingresa la Cédula
        String pass = clave.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación real contra SQLite
        boolean esValido = dbHelper.validarLogin(user, pass);

        if (esValido) {
            manejarSharedPreferences(user, pass);

            Toast.makeText(this, "Acceso concedido al puerto", Toast.LENGTH_SHORT).show();

            Intent rutaPrincipal = new Intent(this, MainActivity.class);
            rutaPrincipal.putExtra("correo", user);
            startActivity(rutaPrincipal);
            finish(); // Cierra el login para que no se pueda regresar con el botón atrás
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Gestión de Persistencia (SharedPreferences) ---

    private void manejarSharedPreferences(String user, String pass) {
        if (recordar.isChecked()) {
            guardarCredencialesSP(user, pass);
        } else {
            SharedPreferences sp = getSharedPreferences("Credenciales", MODE_PRIVATE);
            sp.edit().clear().apply();
        }
    }

    private void cargarCredencialesSP() {
        SharedPreferences spLogin = getSharedPreferences("Credenciales", MODE_PRIVATE);
        String infoCorreo = spLogin.getString("UsuarioSP", "");
        String infoClave = spLogin.getString("ClavesSP", "");

        if (!infoCorreo.isEmpty() && !infoClave.isEmpty()) {
            if (correo != null) correo.setText(infoCorreo);
            if (clave != null) clave.setText(infoClave);
            if (recordar != null) recordar.setChecked(true);
        }
    }

    public void guardarCredencialesSP(String correoStr, String claveStr) {
        SharedPreferences spLogin = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor spLoginEdit = spLogin.edit();
        spLoginEdit.putString("UsuarioSP", correoStr);
        spLoginEdit.putString("ClavesSP", claveStr);
        spLoginEdit.apply(); // apply() es más eficiente que commit() en el hilo principal
    }

    public void irARegistro(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    // --- Ciclo de Vida para Logs ---

    @Override
    protected void onRestart() {
        super.onRestart();
        if (recordar != null && !recordar.isChecked()) {
            if (correo != null) correo.setText("");
            if (clave != null) clave.setText("");
        }
        if (correo != null) correo.requestFocus();
    }
}