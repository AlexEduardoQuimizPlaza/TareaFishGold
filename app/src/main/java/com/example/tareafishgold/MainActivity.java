package com.example.tareafishgold;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    String correoTmp;
    TextView mensaje;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // --- LÓGICA DINÁMICA DE USUARIO ---

        // 1. Obtener el correo del Intent (el que usaste al iniciar sesión)
        Intent infoAdicional = getIntent();
        correoTmp = infoAdicional.getStringExtra("correo");

        // 2. Actualizar mensaje de bienvenida en la pantalla principal
        mensaje = findViewById(R.id.main_lblMensaje);
        mensaje.setText("Bienvenido al sistema FishGold\n" + correoTmp);

        // 3. Actualizar el nombre en el encabezado del Menú Lateral
        // Debemos obtener la vista del Header (posición 0)
        View headerView = navigationView.getHeaderView(0);
        // Buscamos el TextView dentro de ese header
        TextView txtUsuarioHeader = headerView.findViewById(R.id.nav_header_title_real);

        if (correoTmp != null && txtUsuarioHeader != null) {
            txtUsuarioHeader.setText(correoTmp); // Seteamos el nombre dinámico
        }

        // --- FIN LÓGICA DINÁMICA ---

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_about) {
                mostrarAcercaDe();
            } else {
                Toast.makeText(this, "Módulo " + item.getTitle() + " en desarrollo", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawers();
            return true;
        });

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
        Toast.makeText(this, "Preferencias borradas.", Toast.LENGTH_SHORT).show();
        finish();
    }
}