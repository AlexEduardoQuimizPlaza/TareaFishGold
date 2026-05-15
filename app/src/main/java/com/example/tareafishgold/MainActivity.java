package com.example.tareafishgold;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private String correoTmp;
    private TextView mensaje;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 1. Configuración de Interfaz y Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        // Ajuste de color del icono de hamburguesa
        toggle.getDrawerArrowDrawable().setColor(androidx.core.content.ContextCompat.getColor(this, android.R.color.white));

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 2. Lógica Dinámica de Usuario (Supervisor)
        Intent infoAdicional = getIntent();
        correoTmp = infoAdicional.getStringExtra("correo");

        mensaje = findViewById(R.id.main_lblMensaje);
        if (correoTmp != null) {
            mensaje.setText("Bienvenido al sistema Puerto Seguro\n" + correoTmp);
        }

        // Actualizar el encabezado del menú lateral con la cédula/usuario
        View headerView = navigationView.getHeaderView(0);
        TextView txtUsuarioHeader = headerView.findViewById(R.id.nav_header_title_real);
        if (correoTmp != null && txtUsuarioHeader != null) {
            txtUsuarioHeader.setText(correoTmp);
        }

        // 3. Listener de Navegación Corregido para Gestión CRUD
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Lógica para la nueva orden: Consulta, Actualización y Eliminación
            if (id == R.id.menu_gestion_admin) {
                Intent intent = new Intent(MainActivity.this, GestionSupervisorActivity.class);
                startActivity(intent);
            }
            // Tu módulo específico según la propuesta
            else if (id == R.id.menu_planificacion) {
                Toast.makeText(this, "Módulo de Gestión de Pescadores en desarrollo", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.menu_about) {
                mostrarAcercaDe();
            }
            else {
                Toast.makeText(this, "Módulo " + item.getTitle() + " asignado a otro compañero", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        // Manejo de Insets para diseño Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void mostrarAcercaDe() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_acerca_de, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setPositiveButton("Regresar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    public void borrarPreferencias(View v) {
        SharedPreferences sp = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        sp.edit().clear().apply();
        Toast.makeText(this, "Sesión cerrada y preferencias borradas.", Toast.LENGTH_SHORT).show();

        // Redirigir al login tras borrar preferencias
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}