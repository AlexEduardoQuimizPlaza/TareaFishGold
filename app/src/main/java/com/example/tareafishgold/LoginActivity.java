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
    TextInputEditText correo, clave;
    CheckBox recordar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
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

        Log.e("Lifecycle", "Lanzamiento 1: OnCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Lifecycle", "Lanzamiento 2: OnStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Lifecycle", "Lanzamiento 4: OnResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Lifecycle", "Lanzamiento 3: OnPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Lifecycle", "Lanzamiento 5: OnStop");
    }


   @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("Lifecycle", "Lanzamiento 6: OnRestart");

        if (recordar != null && !recordar.isChecked()) {
            if (correo != null) correo.setText("");
            if (clave != null) clave.setText("");
        }
        
        if (correo != null) correo.requestFocus();
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

    public void guardarCredencialesSP (String correo, String clave){
        SharedPreferences spLogin = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor spLoginEdit= spLogin.edit();
        spLoginEdit.putString("UsuarioSP", correo);
        spLoginEdit.putString("ClavesSP", clave);
        spLoginEdit.commit();

    }
    public void validarIngreso(View v) {
        String user = correo.getText().toString();
        String pass = clave.getText().toString();

        if (recordar.isChecked()) {
            guardarCredencialesSP(user, pass);
        } else {
            SharedPreferences sp = getSharedPreferences("Credenciales", MODE_PRIVATE);
            sp.edit().clear().apply();
        }

        Toast.makeText(this, "Sesión: " + user, Toast.LENGTH_SHORT).show();

        Intent rutaPrincipal = new Intent(this, MainActivity.class);
        rutaPrincipal.putExtra("correo", user);
        startActivity(rutaPrincipal);
    }

    public void irARegistro(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}
