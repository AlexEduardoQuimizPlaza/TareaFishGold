package com.example.tareafishgold;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tareafishgold.contract.IAsistenciaRepository;
import com.example.tareafishgold.contract.IFaenaPlanificacionRepository;
import com.example.tareafishgold.data.AsistenciaRepositoryImpl;
import com.example.tareafishgold.data.FaenaPlanificacionRepositoryImpl;
import com.example.tareafishgold.model.PlanificacionFaena;
import com.example.tareafishgold.model.RegistroAsistencia;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ControlAsistenciaActivity extends AppCompatActivity {

    private Spinner spPlanificacion, spTrabajador, spEstado;
    private TextInputEditText etFecha, etHoras;
    private TextView txtInfoPlanificacion;
    private ListView lvAsistencias;
    private Button btnRegistrar;
    private IFaenaPlanificacionRepository planificacionRepo;
    private IAsistenciaRepository asistenciaRepo;
    private BaseDatosSQLite db;
    private List<PlanificacionFaena> planificaciones;
    private List<String> cedulasTripulacion;
    private List<RegistroAsistencia> registros;
    private ArrayAdapter<String> asistenciaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_asistencia);

        db = new BaseDatosSQLite(this);
        planificacionRepo = new FaenaPlanificacionRepositoryImpl(db);
        asistenciaRepo = new AsistenciaRepositoryImpl(db);

        MaterialToolbar toolbar = findViewById(R.id.toolbarM5);
        toolbar.setNavigationOnClickListener(v -> finish());

        spPlanificacion = findViewById(R.id.spPlanificacionAsistencia);
        spTrabajador = findViewById(R.id.spTrabajadorAsistencia);
        spEstado = findViewById(R.id.spEstadoAsistencia);
        etFecha = findViewById(R.id.etFechaAsistencia);
        etHoras = findViewById(R.id.etHorasAsistencia);
        txtInfoPlanificacion = findViewById(R.id.txtInfoPlanificacion);
        lvAsistencias = findViewById(R.id.lvAsistencias);
        btnRegistrar = findViewById(R.id.btnRegistrarAsistencia);

        ArrayAdapter<CharSequence> estadoAdapter = ArrayAdapter.createFromResource(
                this, R.array.estados_asistencia, android.R.layout.simple_spinner_item);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(estadoAdapter);

        etFecha.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        etFecha.setOnClickListener(v -> mostrarDatePicker());

        registros = new ArrayList<>();
        asistenciaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvAsistencias.setAdapter(asistenciaAdapter);

        cargarPlanificaciones();

        spPlanificacion.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (!planificaciones.isEmpty()) {
                    onPlanificacionSeleccionada(planificaciones.get(position));
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnRegistrar.setOnClickListener(v -> registrarAsistencia());

        lvAsistencias.setOnItemLongClickListener((parent, view, position, id) -> {
            RegistroAsistencia reg = registros.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Eliminar registro")
                    .setMessage("¿Eliminar asistencia de " + reg.getTrabajadorNombre() + "?")
                    .setPositiveButton("Eliminar", (d, w) -> {
                        if (asistenciaRepo.eliminarAsistencia(reg.getId())) {
                            recargarAsistencias();
                            Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
            return true;
        });
    }

    private void cargarPlanificaciones() {
        planificaciones = planificacionRepo.buscarPlanificaciones("");
        List<String> etiquetas = new ArrayList<>();
        if (planificaciones.isEmpty()) {
            etiquetas.add("Sin planificaciones — cree una en M4");
        } else {
            for (PlanificacionFaena p : planificaciones) {
                etiquetas.add(p.getEtiquetaLista());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, etiquetas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPlanificacion.setAdapter(adapter);

        if (!planificaciones.isEmpty()) {
            onPlanificacionSeleccionada(planificaciones.get(0));
        } else {
            txtInfoPlanificacion.setText("Cree planificaciones en M4 antes de registrar asistencia.");
            spTrabajador.setEnabled(false);
            btnRegistrar.setEnabled(false);
        }
    }

    private void onPlanificacionSeleccionada(PlanificacionFaena plan) {
        txtInfoPlanificacion.setText(
                "Viaje: " + plan.getCodigoViaje() + "\n" +
                        "Período: " + plan.getFechaInicio() + " → " + plan.getFechaFin() + "\n" +
                        "Turno: " + plan.getTurno() + " | Estado: " + plan.getEstado());

        cedulasTripulacion = planificacionRepo.obtenerTripulacionPorPlanificacion(plan.getId());
        cargarTrabajadoresSpinner();
        recargarAsistencias();
    }

    private PlanificacionFaena getPlanificacionActual() {
        if (planificaciones.isEmpty()) return null;
        int pos = spPlanificacion.getSelectedItemPosition();
        if (pos < 0 || pos >= planificaciones.size()) return null;
        return planificaciones.get(pos);
    }

    private void cargarTrabajadoresSpinner() {
        List<String> etiquetas = new ArrayList<>();
        if (cedulasTripulacion == null || cedulasTripulacion.isEmpty()) {
            etiquetas.add("Sin tripulación — asigne en Planificación (Nuñez)");
            spTrabajador.setEnabled(false);
        } else {
            for (String cedula : cedulasTripulacion) {
                etiquetas.add(cedula + " — " + db.obtenerNombreTrabajador(cedula));
            }
            spTrabajador.setEnabled(true);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, etiquetas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTrabajador.setAdapter(adapter);
        btnRegistrar.setEnabled(!cedulasTripulacion.isEmpty() && !planificaciones.isEmpty());
    }

    private void recargarAsistencias() {
        PlanificacionFaena plan = getPlanificacionActual();
        if (plan == null) return;

        registros = asistenciaRepo.listarAsistencia(plan.getId());
        asistenciaAdapter.clear();
        for (RegistroAsistencia r : registros) {
            asistenciaAdapter.add(r.getTrabajadorNombre() + " | " + r.getFechaRegistro()
                    + " | " + r.getEstado() + " (" + r.getHoras() + " h)");
        }
        asistenciaAdapter.notifyDataSetChanged();
    }

    private void registrarAsistencia() {
        PlanificacionFaena plan = getPlanificacionActual();
        if (plan == null || cedulasTripulacion == null || cedulasTripulacion.isEmpty()) {
            Toast.makeText(this, "Seleccione planificación y tripulación válida", Toast.LENGTH_SHORT).show();
            return;
        }

        String cedula = cedulasTripulacion.get(spTrabajador.getSelectedItemPosition());
        String fecha = etFecha.getText().toString().trim();
        String estado = spEstado.getSelectedItem().toString();

        float horas = 8f;
        try {
            String horasStr = etHoras.getText() != null ? etHoras.getText().toString().trim() : "8";
            if (!horasStr.isEmpty()) {
                horas = Float.parseFloat(horasStr);
                if (horas < 0) {
                    Toast.makeText(this, "Las horas deben ser positivas", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Horas inválidas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fecha.isEmpty()) {
            Toast.makeText(this, "Indique la fecha de registro", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = asistenciaRepo.registrarAsistencia(
                plan.getId(), cedula, fecha, estado, horas);

        if (ok) {
            Toast.makeText(this, "Asistencia registrada (M5)", Toast.LENGTH_SHORT).show();
            recargarAsistencias();
        } else {
            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) -> etFecha.setText(
                        String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
}
