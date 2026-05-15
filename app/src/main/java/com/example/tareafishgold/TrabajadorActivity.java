package com.example.tareafishgold;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class TrabajadorActivity extends AppCompatActivity {

    private TextInputEditText etBuscar, etCedula, etNombre, etTelefono, etDireccion;
    private Spinner spRol, spEstado;
    private Button btnNuevo, btnGuardar, btnActualizar, btnEliminar;
    private ListView lvTrabajadores;
    private BaseDatosSQLite dbHelper;
    private ArrayAdapter<String> listAdapter;
    private List<String[]> trabajadoresData;
    private String cedulaSeleccionada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabajador);

        dbHelper = new BaseDatosSQLite(this);

        etBuscar = findViewById(R.id.etBuscarTrabajador);
        etCedula = findViewById(R.id.etTrabCedula);
        etNombre = findViewById(R.id.etTrabNombre);
        etTelefono = findViewById(R.id.etTrabTelefono);
        etDireccion = findViewById(R.id.etTrabDireccion);
        spRol = findViewById(R.id.spTrabRol);
        spEstado = findViewById(R.id.spTrabEstado);
        lvTrabajadores = findViewById(R.id.lvTrabajadores);
        btnNuevo = findViewById(R.id.btnTrabNuevo);
        btnGuardar = findViewById(R.id.btnTrabGuardar);
        btnActualizar = findViewById(R.id.btnTrabActualizar);
        btnEliminar = findViewById(R.id.btnTrabEliminar);

        configurarSpinners();

        trabajadoresData = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvTrabajadores.setAdapter(listAdapter);

        configurarModoNuevo();
        cargarTrabajadores("");

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                cargarTrabajadores(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        lvTrabajadores.setOnItemClickListener((parent, view, position, id) ->
                cargarEnFormulario(trabajadoresData.get(position)));

        btnNuevo.setOnClickListener(v -> configurarModoNuevo());
        btnGuardar.setOnClickListener(v -> guardar());
        btnActualizar.setOnClickListener(v -> actualizar());
        btnEliminar.setOnClickListener(v -> eliminar());
    }

    private void configurarSpinners() {
        ArrayAdapter<CharSequence> rolAdapter = ArrayAdapter.createFromResource(
                this, R.array.roles_trabajador, android.R.layout.simple_spinner_item);
        rolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRol.setAdapter(rolAdapter);

        ArrayAdapter<CharSequence> estadoAdapter = ArrayAdapter.createFromResource(
                this, R.array.estados_trabajador, android.R.layout.simple_spinner_item);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(estadoAdapter);
    }

    private void cargarTrabajadores(String filtro) {
        trabajadoresData.clear();
        listAdapter.clear();
        Cursor cursor = dbHelper.buscarTrabajadores(filtro);
        if (cursor.moveToFirst()) {
            do {
                trabajadoresData.add(new String[]{
                        cursor.getString(0), // cedula
                        cursor.getString(1), // nombre
                        cursor.getString(2), // rol
                        cursor.getString(3), // telefono
                        cursor.getString(4), // direccion
                        cursor.getString(5)  // estado
                });
                listAdapter.add(cursor.getString(0) + " — " + cursor.getString(1)
                        + " | " + cursor.getString(2)
                        + " [" + cursor.getString(5) + "]");
            } while (cursor.moveToNext());
        }
        cursor.close();
        listAdapter.notifyDataSetChanged();
    }

    private void cargarEnFormulario(String[] datos) {
        cedulaSeleccionada = datos[0];
        etCedula.setText(datos[0]);
        etCedula.setEnabled(false); // la cédula no se puede modificar (RF-2)
        etNombre.setText(datos[1]);
        seleccionarSpinner(spRol, datos[2]);
        etTelefono.setText(datos[3]);
        etDireccion.setText(datos[4]);
        seleccionarSpinner(spEstado, datos[5]);

        btnGuardar.setVisibility(View.GONE);
        btnActualizar.setVisibility(View.VISIBLE);
        btnEliminar.setVisibility(View.VISIBLE);
    }

    private void configurarModoNuevo() {
        cedulaSeleccionada = null;
        etCedula.setEnabled(true);
        etCedula.setText("");
        etNombre.setText("");
        etTelefono.setText("");
        etDireccion.setText("");
        spRol.setSelection(0);
        spEstado.setSelection(0);
        btnGuardar.setVisibility(View.VISIBLE);
        btnActualizar.setVisibility(View.GONE);
        btnEliminar.setVisibility(View.GONE);
    }

    private void guardar() {
        String cedula = etCedula.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();

        if (cedula.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Cédula y nombre son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = dbHelper.insertarTrabajador(
                cedula, nombre,
                spRol.getSelectedItem().toString(),
                etTelefono.getText().toString().trim(),
                etDireccion.getText().toString().trim(),
                spEstado.getSelectedItem().toString());

        if (ok) {
            Toast.makeText(this, "Trabajador registrado", Toast.LENGTH_SHORT).show();
            configurarModoNuevo();
            cargarTrabajadores("");
        } else {
            Toast.makeText(this, "Error: esa cédula ya está registrada", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizar() {
        if (cedulaSeleccionada == null) return;
        String nombre = etNombre.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = dbHelper.actualizarTrabajador(
                cedulaSeleccionada, nombre,
                spRol.getSelectedItem().toString(),
                etTelefono.getText().toString().trim(),
                etDireccion.getText().toString().trim(),
                spEstado.getSelectedItem().toString());

        if (ok) {
            Toast.makeText(this, "Trabajador actualizado", Toast.LENGTH_SHORT).show();
            configurarModoNuevo();
            cargarTrabajadores("");
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminar() {
        if (cedulaSeleccionada == null) return;
        if (dbHelper.eliminarTrabajador(cedulaSeleccionada)) {
            Toast.makeText(this, "Trabajador eliminado", Toast.LENGTH_SHORT).show();
            configurarModoNuevo();
            cargarTrabajadores("");
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void seleccionarSpinner(Spinner spinner, String valor) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(valor)) {
                spinner.setSelection(i);
                return;
            }
        }
    }
}
