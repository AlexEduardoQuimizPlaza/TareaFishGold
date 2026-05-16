package com.example.tareafishgold;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViajeActivity extends AppCompatActivity {

    private BaseDatosSQLite db;

    private TextInputEditText etBuscar, etCodigo, etEmbarcacion, etDestino, etFecha, etMeta;
    private Spinner spEstado;
    private ListView lvViajes;
    private TextView tvTripulacionResumen;

    private android.widget.Button btnNuevo, btnGuardar, btnActualizar, btnEliminar;

    private String viajeIdSeleccionado = null;
    private List<String> cedulasTripulacion = new ArrayList<>();

    // Datos del adapter
    private final List<String> listaDisplay = new ArrayList<>();
    private final List<String> listaIds = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaje);

        db = new BaseDatosSQLite(this);

        etBuscar           = findViewById(R.id.etBuscarViaje);
        etCodigo           = findViewById(R.id.etViajeCodigo);
        etEmbarcacion      = findViewById(R.id.etViajeEmbarcacion);
        etDestino          = findViewById(R.id.etViajeDestino);
        etFecha            = findViewById(R.id.etViajeFecha);
        etMeta             = findViewById(R.id.etViajeMeta);
        spEstado           = findViewById(R.id.spViajeEstado);
        lvViajes           = findViewById(R.id.lvViajes);
        tvTripulacionResumen = findViewById(R.id.tvTripulacionResumen);
        btnNuevo           = findViewById(R.id.btnViajeNuevo);
        btnGuardar         = findViewById(R.id.btnViajeGuardar);
        btnActualizar      = findViewById(R.id.btnViajeActualizar);
        btnEliminar        = findViewById(R.id.btnViajeEliminar);

        ArrayAdapter<CharSequence> estadoAdapter = ArrayAdapter.createFromResource(
                this, R.array.estados_viaje, android.R.layout.simple_spinner_item);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(estadoAdapter);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDisplay);
        lvViajes.setAdapter(adapter);

        cargarViajes("");

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                cargarViajes(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etFecha.setOnClickListener(v -> mostrarDatePicker());

        lvViajes.setOnItemClickListener((parent, view, position, id) -> cargarFormulario(position));

        btnNuevo.setOnClickListener(v -> limpiarFormulario());
        btnGuardar.setOnClickListener(v -> guardarViaje());
        btnActualizar.setOnClickListener(v -> actualizarViaje());
        btnEliminar.setOnClickListener(v -> confirmarEliminar());

        findViewById(R.id.btnSeleccionarTripulacion).setOnClickListener(v -> mostrarDialogoTripulacion());
    }

    private void cargarViajes(String filtro) {
        listaDisplay.clear();
        listaIds.clear();
        Cursor cursor = db.buscarViajes(filtro);
        if (cursor.moveToFirst()) {
            do {
                listaIds.add(String.valueOf(cursor.getLong(0)));
                listaDisplay.add(cursor.getString(1) + " — " + cursor.getString(2) + " [" + cursor.getString(6) + "]");
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void cargarFormulario(int position) {
        viajeIdSeleccionado = listaIds.get(position);

        Cursor cursor = db.buscarViajes("");
        // Buscar el registro con el ID seleccionado
        if (cursor.moveToFirst()) {
            do {
                if (String.valueOf(cursor.getLong(0)).equals(viajeIdSeleccionado)) {
                    etCodigo.setText(cursor.getString(1));
                    etCodigo.setEnabled(false);
                    etEmbarcacion.setText(cursor.getString(2));
                    etDestino.setText(cursor.getString(3));
                    etFecha.setText(cursor.getString(4));
                    String metaStr = cursor.isNull(5) ? "" : String.valueOf(cursor.getFloat(5));
                    etMeta.setText(metaStr);

                    String estado = cursor.getString(6);
                    String[] estados = getResources().getStringArray(R.array.estados_viaje);
                    for (int i = 0; i < estados.length; i++) {
                        if (estados[i].equals(estado)) { spEstado.setSelection(i); break; }
                    }
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        cargarTripulacionViaje();

        btnGuardar.setVisibility(View.GONE);
        btnActualizar.setVisibility(View.VISIBLE);
        btnEliminar.setVisibility(View.VISIBLE);
    }

    private void cargarTripulacionViaje() {
        cedulasTripulacion.clear();
        Cursor cursor = db.obtenerTripulacionViaje(viajeIdSeleccionado);
        if (cursor.moveToFirst()) {
            do { cedulasTripulacion.add(cursor.getString(0)); } while (cursor.moveToNext());
        }
        cursor.close();
        actualizarResumenTripulacion();
    }

    private void actualizarResumenTripulacion() {
        if (cedulasTripulacion.isEmpty()) {
            tvTripulacionResumen.setText("Sin tripulación asignada");
            return;
        }
        StringBuilder sb = new StringBuilder(cedulasTripulacion.size() + " miembro(s): ");
        for (int i = 0; i < cedulasTripulacion.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(db.obtenerNombreTrabajador(cedulasTripulacion.get(i)));
        }
        tvTripulacionResumen.setText(sb.toString());
    }

    private void mostrarDialogoTripulacion() {
        Cursor cursor = db.obtenerTrabajadoresActivos();
        if (!cursor.moveToFirst()) {
            cursor.close();
            Toast.makeText(this, "No hay trabajadores activos registrados.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> nombres = new ArrayList<>();
        List<String> cedulas = new ArrayList<>();
        do {
            cedulas.add(cursor.getString(0));
            nombres.add(cursor.getString(1) + " (" + cursor.getString(2) + ")");
        } while (cursor.moveToNext());
        cursor.close();

        boolean[] seleccionados = new boolean[cedulas.size()];
        for (int i = 0; i < cedulas.size(); i++) {
            seleccionados[i] = cedulasTripulacion.contains(cedulas.get(i));
        }

        new AlertDialog.Builder(this)
                .setTitle("Seleccionar Tripulación")
                .setMultiChoiceItems(nombres.toArray(new String[0]), seleccionados,
                        (dialog, which, isChecked) -> seleccionados[which] = isChecked)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    cedulasTripulacion.clear();
                    for (int i = 0; i < cedulas.size(); i++) {
                        if (seleccionados[i]) cedulasTripulacion.add(cedulas.get(i));
                    }
                    actualizarResumenTripulacion();
                    if (viajeIdSeleccionado != null) {
                        db.guardarTripulacion(viajeIdSeleccionado, cedulasTripulacion);
                        Toast.makeText(this, "Tripulación actualizada.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarViaje() {
        String codigo = getText(etCodigo);
        String embarcacion = getText(etEmbarcacion);
        String destino = getText(etDestino);
        String fecha = getText(etFecha);
        String metaStr = getText(etMeta);
        String estado = spEstado.getSelectedItem().toString();

        if (codigo.isEmpty() || embarcacion.isEmpty()) {
            Toast.makeText(this, "Código y embarcación son obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        float meta = 0f;
        if (!metaStr.isEmpty()) {
            try { meta = Float.parseFloat(metaStr); } catch (NumberFormatException e) {
                Toast.makeText(this, "Meta de pesca inválida.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        long id = db.insertarViaje(codigo, embarcacion, destino, fecha, meta, estado);
        if (id == -1) {
            Toast.makeText(this, "El código de viaje ya existe.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cedulasTripulacion.isEmpty()) {
            db.guardarTripulacion(String.valueOf(id), cedulasTripulacion);
        }

        Toast.makeText(this, "Viaje guardado correctamente.", Toast.LENGTH_SHORT).show();
        limpiarFormulario();
        cargarViajes("");
    }

    private void actualizarViaje() {
        String embarcacion = getText(etEmbarcacion);
        String destino = getText(etDestino);
        String fecha = getText(etFecha);
        String metaStr = getText(etMeta);
        String estado = spEstado.getSelectedItem().toString();

        if (embarcacion.isEmpty()) {
            Toast.makeText(this, "La embarcación es obligatoria.", Toast.LENGTH_SHORT).show();
            return;
        }

        float meta = 0f;
        if (!metaStr.isEmpty()) {
            try { meta = Float.parseFloat(metaStr); } catch (NumberFormatException e) {
                Toast.makeText(this, "Meta de pesca inválida.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        boolean ok = db.actualizarViaje(viajeIdSeleccionado, embarcacion, destino, fecha, meta, estado);
        if (ok) {
            db.guardarTripulacion(viajeIdSeleccionado, cedulasTripulacion);
            Toast.makeText(this, "Viaje actualizado.", Toast.LENGTH_SHORT).show();
            limpiarFormulario();
            cargarViajes("");
        } else {
            Toast.makeText(this, "Error al actualizar.", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar viaje")
                .setMessage("¿Está seguro? También se eliminarán las planificaciones y registros de asistencia asociados.")
                .setPositiveButton("Eliminar", (d, w) -> {
                    // Eliminar tripulación y el viaje
                    db.guardarTripulacion(viajeIdSeleccionado, new ArrayList<>());
                    // Eliminar planificaciones asociadas en cascada
                    List<com.example.tareafishgold.model.PlanificacionFaena> planes =
                            db.buscarPlanificacionesFaena("");
                    for (com.example.tareafishgold.model.PlanificacionFaena plan : planes) {
                        if (String.valueOf(plan.getViajeId()).equals(viajeIdSeleccionado)) {
                            db.eliminarPlanificacionFaena(plan.getId());
                        }
                    }
                    android.database.sqlite.SQLiteDatabase raw = db.getWritableDatabase();
                    raw.delete("viajes", "id = ?", new String[]{viajeIdSeleccionado});
                    Toast.makeText(this, "Viaje eliminado.", Toast.LENGTH_SHORT).show();
                    limpiarFormulario();
                    cargarViajes("");
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void limpiarFormulario() {
        viajeIdSeleccionado = null;
        cedulasTripulacion.clear();
        etCodigo.setText("");
        etCodigo.setEnabled(true);
        etEmbarcacion.setText("");
        etDestino.setText("");
        etFecha.setText("");
        etMeta.setText("");
        spEstado.setSelection(0);
        tvTripulacionResumen.setText("Sin tripulación asignada");
        btnGuardar.setVisibility(View.VISIBLE);
        btnActualizar.setVisibility(View.GONE);
        btnEliminar.setVisibility(View.GONE);
    }

    private void mostrarDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String fecha = String.format("%04d-%02d-%02d", year, month + 1, day);
                    etFecha.setText(fecha);
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
