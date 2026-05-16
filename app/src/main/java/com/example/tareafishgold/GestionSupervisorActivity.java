package com.example.tareafishgold;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class GestionSupervisorActivity extends AppCompatActivity {

    private TextInputEditText etBuscar;
    private EditText etNom, etApe, etEdad, etPass;
    private Button btnConsultar, btnActualizar, btnEliminar;
    private BaseDatosSQLite dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_supervisor);

        dbHelper = new BaseDatosSQLite(this);

        etBuscar = findViewById(R.id.etBuscarCedula);
        etNom = findViewById(R.id.etEditNombres);
        etApe = findViewById(R.id.etEditApellidos);
        etEdad = findViewById(R.id.etEditEdad);
        etPass = findViewById(R.id.etEditPass);

        btnConsultar = findViewById(R.id.btnConsultar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);

        btnConsultar.setOnClickListener(v -> buscar());
        btnActualizar.setOnClickListener(v -> actualizar());
        btnEliminar.setOnClickListener(v -> eliminar());
    }

    private void buscar() {
        String cedula = etBuscar.getText().toString().trim();
        if (cedula.isEmpty()) return;

        Cursor res = dbHelper.buscarSupervisorPorCedula(cedula);
        if (res.moveToFirst()) {
            // Llenar los campos con la información recuperada
            etNom.setText(res.getString(1)); // COL_NOMBRES
            etApe.setText(res.getString(2)); // COL_APELLIDOS
            etEdad.setText(String.valueOf(res.getInt(3))); // COL_EDAD
            etPass.setText(res.getString(9)); // COL_PASSWORD
            Toast.makeText(this, "Datos de supervisor recuperados", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Supervisor no encontrado en Puerto Seguro", Toast.LENGTH_SHORT).show();
        }
        res.close();
    }

    private void actualizar() {
        String cedula = etBuscar.getText().toString().trim();
        if (dbHelper.actualizarSupervisor(cedula,
                etNom.getText().toString(),
                etApe.getText().toString(),
                Integer.parseInt(etEdad.getText().toString()),
                "N/A", "N/A", "N/A", "N/A", 0.0f, // Mantener campos originales o editables
                etPass.getText().toString())) {
            Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminar() {
        String cedula = etBuscar.getText().toString().trim();
        if (dbHelper.eliminarSupervisor(cedula)) {
            Toast.makeText(this, "Supervisor eliminado del sistema", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        }
    }

    private void limpiarCampos() {
        etBuscar.setText("");
        etNom.setText("");
        etApe.setText("");
        etEdad.setText("");
        etPass.setText("");
    }
}