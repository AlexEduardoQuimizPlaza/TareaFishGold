package com.example.tareafishgold;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private String correoTmp;
    private TextView mensaje;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Toolbar y hamburguesa
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, android.R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 2. Usuario logueado
        correoTmp = getIntent().getStringExtra("correo");
        mensaje = findViewById(R.id.main_lblMensaje);
        if (correoTmp != null) {
            mensaje.setText("Bienvenido al sistema Puerto Seguro\n" + correoTmp);
        }

        View headerView = navigationView.getHeaderView(0);
        TextView txtUsuarioHeader = headerView.findViewById(R.id.nav_header_title_real);
        TextView txtSubtitulo = headerView.findViewById(R.id.nav_header_subtitle);
        if (correoTmp != null && txtUsuarioHeader != null) {
            txtUsuarioHeader.setText(correoTmp);
        }
        if (UsuarioInicial.CEDULA.equals(correoTmp) && txtSubtitulo != null) {
            txtSubtitulo.setText(UsuarioInicial.ROL_DESCRIPCION);
        }

        // 3. Navegación
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_gestion_admin) {
                startActivity(new Intent(this, GestionSupervisorActivity.class));
            } else if (id == R.id.menu_trabajador) {
                startActivity(new Intent(this, TrabajadorActivity.class));
            } else if (id == R.id.menu_viaje) {
                startActivity(new Intent(this, ViajeActivity.class));
            } else if (id == R.id.menu_embarcacion) {
                startActivity(new Intent(this, PlanificacionFaenaActivity.class));
            } else if (id == R.id.menu_faena) {
                startActivity(new Intent(this, ControlAsistenciaActivity.class));
            } else if (id == R.id.menu_planificacion) {
                startActivity(new Intent(this, LiquidacionPagoActivity.class));
            } else if (id == R.id.menu_reportes) {
                startActivity(new Intent(this, ReporteViajeActivity.class));
            } else if (id == R.id.menu_about) {
                mostrarAcercaDe();
            } else {
                Toast.makeText(this, "Módulo " + item.getTitle() + " asignado a otro compañero", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void mostrarAcercaDe() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_acerca_de, null);
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Regresar", (d, w) -> d.dismiss())
                .create().show();
    }

    // Método 1: Solo cierra la sesión actual volviendo al Login (Mantiene los datos si se marcó recordar)
    public void cerrarSesion(View v) {
        Toast.makeText(this, "Sesión cerrada.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        // Limpiamos el historial para que no puedan volver presionando la flecha del teléfono
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Método 2: Borra por completo el SharedPreferences "Credenciales" y vuelve al Login vacío
    public void borrarPreferencias(View v) {
        getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        Toast.makeText(this, "Credenciales eliminadas del dispositivo.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        // Limpiamos el historial de navegación de la app de forma segura
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}