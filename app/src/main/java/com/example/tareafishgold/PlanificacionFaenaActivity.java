package com.example.tareafishgold;

import android.app.DatePickerDialog;
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

import com.example.tareafishgold.contract.IFaenaPlanificacionRepository;
import com.example.tareafishgold.data.FaenaPlanificacionRepositoryImpl;
import com.example.tareafishgold.model.PlanificacionFaena;
import com.example.tareafishgold.model.ViajeResumen;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PlanificacionFaenaActivity extends AppCompatActivity {

    private TextInputEditText etBuscar, etNombre, etInicio, etFin, etObs;
    private Spinner spViaje, spTurno, spEstado;
    private ListView lvPlanificaciones;
    private Button btnNuevo, btnGuardar, btnActualizar, btnEliminar;
    private IFaenaPlanificacionRepository repository;
    private List<PlanificacionFaena> planificaciones;
    private List<ViajeResumen> viajes;
    private ArrayAdapter<String> listAdapter;
    private Long idSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planificacion_faena);

        BaseDatosSQLite db = new BaseDatosSQLite(this);
        repository = new FaenaPlanificacionRepositoryImpl(db);

        MaterialToolbar toolbar = findViewById(R.id.toolbarM4);
        toolbar.setNavigationOnClickListener(v -> finish());

        etBuscar = findViewById(R.id.etBuscarPlanFaena);
        etNombre = findViewById(R.id.etNombreFaena);
        etInicio = findViewById(R.id.etFechaInicioFaena);
        etFin = findViewById(R.id.etFechaFinFaena);
        etObs = findViewById(R.id.etObsPlanFaena);
        spViaje = findViewById(R.id.spViajePlanFaena);
        spTurno = findViewById(R.id.spTurnoFaena);
        spEstado = findViewById(R.id.spEstadoPlanFaena);
        lvPlanificaciones = findViewById(R.id.lvPlanificaciones);
        btnNuevo = findViewById(R.id.btnPlanFaenaNuevo);
        btnGuardar = findViewById(R.id.btnPlanFaenaGuardar);
        btnActualizar = findViewById(R.id.btnPlanFaenaActualizar);
        btnEliminar = findViewById(R.id.btnPlanFaenaEliminar);

        configurarSpinners();
        etInicio.setOnClickListener(v -> mostrarDatePicker(etInicio));
        etFin.setOnClickListener(v -> mostrarDatePicker(etFin));

        planificaciones = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvPlanificaciones.setAdapter(listAdapter);

        cargarViajes();
        cargarPlanificaciones("");

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                cargarPlanificaciones(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        lvPlanificaciones.setOnItemClickListener((p, v, pos, id) -> cargarEnFormulario(planificaciones.get(pos)));
        btnNuevo.setOnClickListener(v -> limpiarFormulario());
        btnGuardar.setOnClickListener(v -> guardar());
        btnActualizar.setOnClickListener(v -> actualizar());
        btnEliminar.setOnClickListener(v -> eliminar());

        limpiarFormulario();
    }

    private void configurarSpinners() {
        ArrayAdapter<CharSequence> turnoAdapter = ArrayAdapter.createFromResource(
                this, R.array.turnos_faena, android.R.layout.simple_spinner_item);
        turnoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTurno.setAdapter(turnoAdapter);

        ArrayAdapter<CharSequence> estadoAdapter = ArrayAdapter.createFromResource(
                this, R.array.estados_plan_faena, android.R.layout.simple_spinner_item);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(estadoAdapter);
    }

    private void cargarViajes() {
        viajes = repository.listarViajesActivos();
        List<String> etiquetas = new ArrayList<>();
        if (viajes.isEmpty()) {
            etiquetas.add("Sin viajes — cree uno en Planificación (Nuñez)");
        } else {
            for (ViajeResumen v : viajes) {
                etiquetas.add(v.getEtiquetaSpinner());
            }
        }
        ArrayAdapter<String> viajeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, etiquetas);
        viajeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spViaje.setAdapter(viajeAdapter);
        spViaje.setEnabled(!viajes.isEmpty());
    }

    private void cargarPlanificaciones(String filtro) {
        planificaciones = repository.buscarPlanificaciones(filtro);
        listAdapter.clear();
        for (PlanificacionFaena p : planificaciones) {
            listAdapter.add(p.getEtiquetaLista());
        }
        listAdapter.notifyDataSetChanged();
    }

    private void cargarEnFormulario(PlanificacionFaena plan) {
        idSeleccionado = plan.getId();
        etNombre.setText(plan.getNombreFaena());
        etInicio.setText(plan.getFechaInicio());
        etFin.setText(plan.getFechaFin());
        etObs.setText(plan.getObservaciones());
        seleccionarSpinner(spTurno, plan.getTurno());
        seleccionarSpinner(spEstado, plan.getEstado());
        seleccionarViaje(plan.getViajeId());

        btnGuardar.setVisibility(View.GONE);
        btnActualizar.setVisibility(View.VISIBLE);
        btnEliminar.setVisibility(View.VISIBLE);
    }

    private void seleccionarViaje(long viajeId) {
        for (int i = 0; i < viajes.size(); i++) {
            if (viajes.get(i).getId() == viajeId) {
                spViaje.setSelection(i);
                return;
            }
        }
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        etNombre.setText("");
        etInicio.setText("");
        etFin.setText("");
        etObs.setText("");
        spTurno.setSelection(0);
        spEstado.setSelection(0);
        if (!viajes.isEmpty()) {
            spViaje.setSelection(0);
        }
        btnGuardar.setVisibility(View.VISIBLE);
        btnActualizar.setVisibility(View.GONE);
        btnEliminar.setVisibility(View.GONE);
    }

    private void guardar() {
        if (viajes.isEmpty()) {
            Toast.makeText(this, "Registre un viaje en Planificación de Faenas (Nuñez) primero", Toast.LENGTH_LONG).show();
            return;
        }
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre de la faena es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        ViajeResumen viaje = viajes.get(spViaje.getSelectedItemPosition());
        long id = repository.crearPlanificacion(
                viaje.getId(),
                nombre,
                etInicio.getText().toString().trim(),
                etFin.getText().toString().trim(),
                spTurno.getSelectedItem().toString(),
                spEstado.getSelectedItem().toString(),
                etObs.getText().toString().trim());

        if (id != -1) {
            Toast.makeText(this, "Planificación creada (M4)", Toast.LENGTH_SHORT).show();
            limpiarFormulario();
            cargarPlanificaciones("");
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizar() {
        if (idSeleccionado == null) return;
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre de la faena es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = repository.actualizarPlanificacion(
                idSeleccionado,
                nombre,
                etInicio.getText().toString().trim(),
                etFin.getText().toString().trim(),
                spTurno.getSelectedItem().toString(),
                spEstado.getSelectedItem().toString(),
                etObs.getText().toString().trim());

        if (ok) {
            Toast.makeText(this, "Planificación actualizada", Toast.LENGTH_SHORT).show();
            limpiarFormulario();
            cargarPlanificaciones("");
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminar() {
        if (idSeleccionado == null) return;
        if (repository.eliminarPlanificacion(idSeleccionado)) {
            Toast.makeText(this, "Planificación eliminada", Toast.LENGTH_SHORT).show();
            limpiarFormulario();
            cargarPlanificaciones("");
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDatePicker(TextInputEditText campo) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) -> campo.setText(
                        String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
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
