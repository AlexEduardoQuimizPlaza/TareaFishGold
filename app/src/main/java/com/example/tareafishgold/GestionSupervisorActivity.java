package com.example.tareafishgold;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class GestionSupervisorActivity extends AppCompatActivity {

    private TextInputEditText etBuscar, etCedula, etNombre, etApellido, etEdad, etPass;
    private Button btnNuevo, btnGuardar, btnActualizar, btnEliminar;
    private ListView lvSupervisores;

    private BaseDatosSQLite dbHelper;
    private ArrayAdapter<String> listAdapter;
    private List<String[]> supervisoresData;
    private String cedulaSeleccionada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_supervisor);

        dbHelper = new BaseDatosSQLite(this);

        // Inicialización del Toolbar y botón de retroceder
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Inicialización de Vistas
        etBuscar = findViewById(R.id.etBuscarSupervisor);
        etCedula = findViewById(R.id.etSupCedula);
        etNombre = findViewById(R.id.etSupNombres);
        etApellido = findViewById(R.id.etSupApellidos);
        etEdad = findViewById(R.id.etSupEdad);
        etPass = findViewById(R.id.etSupPass);

        lvSupervisores = findViewById(R.id.lvSupervisores);

        btnNuevo = findViewById(R.id.btnSupNuevo);
        btnGuardar = findViewById(R.id.btnSupGuardar);
        btnActualizar = findViewById(R.id.btnSupActualizar);
        btnEliminar = findViewById(R.id.btnSupEliminar);

        // Configuración de Lista y Adaptadores
        supervisoresData = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvSupervisores.setAdapter(listAdapter);

        // Comportamiento Inicial
        configurarModoNuevo();
        cargarSupervisores("");

        // Listener de búsqueda en tiempo real
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                cargarSupervisores(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Evento al seleccionar un ítem de la lista
        lvSupervisores.setOnItemClickListener((parent, view, position, id) ->
                cargarEnFormulario(supervisoresData.get(position)));

        // Listeners de Botones
        btnNuevo.setOnClickListener(v -> configurarModoNuevo());
        btnGuardar.setOnClickListener(v -> guardar());
        btnActualizar.setOnClickListener(v -> actualizar());
        btnEliminar.setOnClickListener(v -> eliminar());
    }

    private void cargarSupervisores(String filtro) {
        supervisoresData.clear();
        listAdapter.clear();

        Cursor cursor = dbHelper.buscarSupervisores(filtro);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String cedula = cursor.getString(0);      // COL_CEDULA
                String nombre = cursor.getString(1);      // COL_NOMBRES
                String apellido = cursor.getString(2);    // COL_APELLIDOS
                String edad = String.valueOf(cursor.getInt(3)); // COL_EDAD
                String pass = cursor.getString(9);        // COL_PASSWORD

                supervisoresData.add(new String[]{cedula, nombre, apellido, edad, pass});
                listAdapter.add(cedula + " — " + nombre + " " + apellido);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        listAdapter.notifyDataSetChanged();
    }

    private void cargarEnFormulario(String[] datos) {
        cedulaSeleccionada = datos[0];
        etCedula.setText(datos[0]);
        etCedula.setEnabled(false);

        etNombre.setText(datos[1]);
        etApellido.setText(datos[2]);
        etEdad.setText(datos[3]);
        etPass.setText(datos[4]);

        btnGuardar.setVisibility(View.GONE);
        btnActualizar.setVisibility(View.VISIBLE);
        btnEliminar.setVisibility(View.VISIBLE);
    }

    private void configurarModoNuevo() {
        cedulaSeleccionada = null;
        etCedula.setEnabled(true);
        etCedula.setText("");
        etNombre.setText("");
        etApellido.setText("");
        etEdad.setText("");
        etPass.setText("");

        btnGuardar.setVisibility(View.VISIBLE);
        btnActualizar.setVisibility(View.GONE);
        btnEliminar.setVisibility(View.GONE);
    }

    private void guardar() {
        String cedula = etCedula.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String pass = etPass.getText().toString();

        if (cedula.isEmpty() || nombre.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(this, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.existeSupervisor(cedula)) {
            Toast.makeText(this, "Error: la cédula ya se encuentra registrada", Toast.LENGTH_SHORT).show();
            return;
        }

        // Corregido para insertar un registro nuevo
        boolean ok = dbHelper.insertarSupervisor(cedula, nombre, apellido, Integer.parseInt(edadStr),
                "N/A", "N/A", "N/A", "N/A", 0.0f, pass);

        if (ok) {
            Toast.makeText(this, "Supervisor registrado", Toast.LENGTH_SHORT).show();
            configurarModoNuevo();
            cargarSupervisores("");
        } else {
            Toast.makeText(this, "Error al guardar el supervisor", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizar() {
        if (cedulaSeleccionada == null) return;

        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String pass = etPass.getText().toString();

        if (nombre.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(this, "Nombre y edad son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = dbHelper.actualizarSupervisor(cedulaSeleccionada, nombre, apellido,
                Integer.parseInt(edadStr), "N/A", "N/A", "N/A", "N/A", 0.0f, pass);

        if (ok) {
            Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
            configurarModoNuevo();
            cargarSupervisores("");
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminar() {
        if (cedulaSeleccionada == null) return;

        if (dbHelper.eliminarSupervisor(cedulaSeleccionada)) {
            Toast.makeText(this, "Supervisor eliminado del sistema", Toast.LENGTH_SHORT).show();
            configurarModoNuevo();
            cargarSupervisores("");
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }
}