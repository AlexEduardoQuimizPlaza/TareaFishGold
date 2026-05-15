package com.example.tareafishgold;

import android.app.DatePickerDialog;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PlanificacionActivity extends AppCompatActivity {

    private TextInputEditText etBuscar, etCodigo, etEmbarcacion, etDestino, etFecha, etMeta;
    private Spinner spEstadoViaje;
    private Button btnNuevo, btnGuardar, btnActualizar, btnTripulacion;
    private ListView lvViajes;
    private BaseDatosSQLite dbHelper;
    private ArrayAdapter<String> listAdapter;
    private List<String[]> viajesData;
    private String idViajeSeleccionado = null;
    private List<String> cedulasSeleccionadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planificacion);

        dbHelper = new BaseDatosSQLite(this);

        etBuscar = findViewById(R.id.etBuscarViaje);
        etCodigo = findViewById(R.id.etViajeCodigo);
        etEmbarcacion = findViewById(R.id.etViajeEmbarcacion);
        etDestino = findViewById(R.id.etViajeDestino);
        etFecha = findViewById(R.id.etViajeFecha);
        etMeta = findViewById(R.id.etViajeMeta);
        spEstadoViaje = findViewById(R.id.spViajeEstado);
        lvViajes = findViewById(R.id.lvViajes);
        btnNuevo = findViewById(R.id.btnViajeNuevo);
        btnGuardar = findViewById(R.id.btnViajeGuardar);
        btnActualizar = findViewById(R.id.btnViajeActualizar);
        btnTripulacion = findViewById(R.id.btnSeleccionarTripulacion);

        etFecha.setFocusable(false);
        etFecha.setOnClickListener(v -> mostrarDatePicker());

        configurarSpinner();

        viajesData = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvViajes.setAdapter(listAdapter);

        configurarModoNuevo();
        cargarViajes("");

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                cargarViajes(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        lvViajes.setOnItemClickListener((parent, view, position, id) ->
                cargarEnFormulario(viajesData.get(position)));

        btnNuevo.setOnClickListener(v -> configurarModoNuevo());
        btnGuardar.setOnClickListener(v -> guardar());
        btnActualizar.setOnClickListener(v -> actualizar());
        btnTripulacion.setOnClickListener(v -> seleccionarTripulacion());
    }

    private void configurarSpinner() {
        ArrayAdapter<CharSequence> estadoAdapter = ArrayAdapter.createFromResource(
                this, R.array.estados_viaje, android.R.layout.simple_spinner_item);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstadoViaje.setAdapter(estadoAdapter);
    }

    private void cargarViajes(String filtro) {
        viajesData.clear();
        listAdapter.clear();
        Cursor cursor = dbHelper.buscarViajes(filtro);
        if (cursor.moveToFirst()) {
            do {
                viajesData.add(new String[]{
                        cursor.getString(0), // id
                        cursor.getString(1), // codigo
                        cursor.getString(2), // embarcacion
                        cursor.getString(3), // destino
                        cursor.getString(4), // fecha
                        cursor.getString(5), // meta
                        cursor.getString(6)  // estado
                });
                listAdapter.add(cursor.getString(1) + " | " + cursor.getString(2)
                        + " → " + cursor.getString(3)
                        + " [" + cursor.getString(6) + "]");
            } while (cursor.moveToNext());
        }
        cursor.close();
        listAdapter.notifyDataSetChanged();
    }

    private void cargarEnFormulario(String[] datos) {
        idViajeSeleccionado = datos[0];
        etCodigo.setText(datos[1]);
        etCodigo.setEnabled(false); // código no editable una vez creado
        etEmbarcacion.setText(datos[2]);
        etDestino.setText(datos[3]);
        etFecha.setText(datos[4]);
        etMeta.setText(datos[5]);
        seleccionarSpinner(spEstadoViaje, datos[6]);

        boolean finalizado = "Finalizado".equals(datos[6]);
        setFormularioEditable(!finalizado);

        cargarTripulacionViaje(datos[0]);

        btnGuardar.setVisibility(View.GONE);
        btnActualizar.setVisibility(finalizado ? View.GONE : View.VISIBLE);

        if (finalizado) {
            Toast.makeText(this, "Viaje finalizado — solo lectura (RF-5)", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarTripulacionViaje(String viajeId) {
        cedulasSeleccionadas.clear();
        Cursor cursor = dbHelper.obtenerTripulacionViaje(viajeId);
        if (cursor.moveToFirst()) {
            do { cedulasSeleccionadas.add(cursor.getString(0)); }
            while (cursor.moveToNext());
        }
        cursor.close();
        actualizarBotonTripulacion();
    }

    private void configurarModoNuevo() {
        idViajeSeleccionado = null;
        cedulasSeleccionadas.clear();
        etCodigo.setEnabled(true);
        etCodigo.setText("");
        etEmbarcacion.setText("");
        etDestino.setText("");
        etFecha.setText("");
        etMeta.setText("");
        spEstadoViaje.setSelection(0);
        setFormularioEditable(true);
        actualizarBotonTripulacion();
        btnGuardar.setVisibility(View.VISIBLE);
        btnActualizar.setVisibility(View.GONE);
    }

    private void guardar() {
        String codigo = etCodigo.getText().toString().trim();
        String embarcacion = etEmbarcacion.getText().toString().trim();
        String metaStr = etMeta.getText().toString().trim();

        if (codigo.isEmpty() || embarcacion.isEmpty()) {
            Toast.makeText(this, "Código y embarcación son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        float meta = 0;
        try {
            if (!metaStr.isEmpty()) {
                meta = Float.parseFloat(metaStr);
                if (meta <= 0) {
                    Toast.makeText(this, "La meta debe ser mayor a cero (RF-5)", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Meta inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        long nuevoId = dbHelper.insertarViaje(
                codigo, embarcacion,
                etDestino.getText().toString().trim(),
                etFecha.getText().toString().trim(),
                meta,
                spEstadoViaje.getSelectedItem().toString());

        if (nuevoId != -1) {
            dbHelper.guardarTripulacion(String.valueOf(nuevoId), cedulasSeleccionadas);
            Toast.makeText(this, "Viaje registrado", Toast.LENGTH_SHORT).show();
            configurarModoNuevo();
            cargarViajes("");
        } else {
            Toast.makeText(this, "Error: ese código de viaje ya existe", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizar() {
        if (idViajeSeleccionado == null) return;
        String metaStr = etMeta.getText().toString().trim();

        float meta = 0;
        try {
            if (!metaStr.isEmpty()) {
                meta = Float.parseFloat(metaStr);
                if (meta <= 0) {
                    Toast.makeText(this, "La meta debe ser mayor a cero (RF-5)", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Meta inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = dbHelper.actualizarViaje(
                idViajeSeleccionado,
                etEmbarcacion.getText().toString().trim(),
                etDestino.getText().toString().trim(),
                etFecha.getText().toString().trim(),
                meta,
                spEstadoViaje.getSelectedItem().toString());

        if (ok) {
            dbHelper.guardarTripulacion(idViajeSeleccionado, cedulasSeleccionadas);
            Toast.makeText(this, "Viaje actualizado", Toast.LENGTH_SHORT).show();
            configurarModoNuevo();
            cargarViajes("");
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void seleccionarTripulacion() {
        Cursor cursor = dbHelper.obtenerTrabajadoresActivos();
        List<String> nombres = new ArrayList<>();
        List<String> cedulas = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                cedulas.add(cursor.getString(0));
                nombres.add(cursor.getString(0) + " — " + cursor.getString(1)
                        + " (" + cursor.getString(2) + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (nombres.isEmpty()) {
            Toast.makeText(this, "No hay trabajadores activos disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean[] seleccionados = new boolean[cedulas.size()];
        for (int i = 0; i < cedulas.size(); i++) {
            seleccionados[i] = cedulasSeleccionadas.contains(cedulas.get(i));
        }

        new AlertDialog.Builder(this)
                .setTitle("Seleccionar Tripulación")
                .setMultiChoiceItems(nombres.toArray(new String[0]), seleccionados,
                        (dialog, which, isChecked) -> {
                            if (isChecked) cedulasSeleccionadas.add(cedulas.get(which));
                            else cedulasSeleccionadas.remove(cedulas.get(which));
                        })
                .setPositiveButton("Confirmar", (dialog, which) -> actualizarBotonTripulacion())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void actualizarBotonTripulacion() {
        btnTripulacion.setText("Tripulación (" + cedulasSeleccionadas.size() + " seleccionados)");
    }

    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) -> etFecha.setText(
                        String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setFormularioEditable(boolean editable) {
        etEmbarcacion.setEnabled(editable);
        etDestino.setEnabled(editable);
        etFecha.setEnabled(editable);
        etMeta.setEnabled(editable);
        spEstadoViaje.setEnabled(editable);
        btnTripulacion.setEnabled(editable);
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
