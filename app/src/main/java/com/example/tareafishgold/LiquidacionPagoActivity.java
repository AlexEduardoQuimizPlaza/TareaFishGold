package com.example.tareafishgold;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tareafishgold.model.Liquidacion;
import com.example.tareafishgold.model.PlanificacionFaena;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LiquidacionPagoActivity extends AppCompatActivity {

    private Spinner spPlanificacion;
    private TextInputEditText etPrecioPorKg, etPesoCapturado, etCapitan, etObs;
    private TextView tvMontoEstimado;
    private Button btnFinalizar, btnEliminar;
    private ListView lvLiquidaciones;

    private BaseDatosSQLite db;
    private List<PlanificacionFaena> planificaciones;
    private List<Liquidacion> liquidaciones;
    private ArrayAdapter<String> listAdapter;
    private Long idLiqSeleccionada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquidacion_pago);

        db = new BaseDatosSQLite(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarLiquidacion);
        toolbar.setNavigationOnClickListener(v -> finish());

        spPlanificacion = findViewById(R.id.spPlanificacion);
        etPrecioPorKg = findViewById(R.id.etPrecioPorKg);
        etPesoCapturado = findViewById(R.id.etPesoCapturado);
        tvMontoEstimado = findViewById(R.id.tvMontoEstimado);
        etCapitan = findViewById(R.id.etCapitanResponsable);
        etObs = findViewById(R.id.etObsLiquidacion);
        btnFinalizar = findViewById(R.id.btnFinalizarPagar);
        btnEliminar = findViewById(R.id.btnEliminarLiquidacion);
        lvLiquidaciones = findViewById(R.id.lvLiquidaciones);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvLiquidaciones.setAdapter(listAdapter);

        cargarPlanificaciones();
        cargarLiquidaciones();

        TextWatcher calcWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                recalcularMonto();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        etPrecioPorKg.addTextChangedListener(calcWatcher);
        etPesoCapturado.addTextChangedListener(calcWatcher);

        lvLiquidaciones.setOnItemClickListener((parent, view, pos, id) -> {
            idLiqSeleccionada = liquidaciones.get(pos).getId();
            btnEliminar.setEnabled(true);
            Toast.makeText(this, "Liquidación seleccionada para eliminar", Toast.LENGTH_SHORT).show();
        });

        btnFinalizar.setOnClickListener(v -> guardar());
        btnEliminar.setOnClickListener(v -> eliminar());
    }

    private void cargarPlanificaciones() {
        planificaciones = db.buscarPlanificacionesFaena("");
        List<String> etiquetas = new ArrayList<>();
        if (planificaciones.isEmpty()) {
            etiquetas.add("Sin planificaciones — cree una en M4: Planificación Faenas");
        } else {
            for (PlanificacionFaena p : planificaciones) {
                etiquetas.add(p.getNombreFaena() + " | " + p.getCodigoViaje() + " [" + p.getEstado() + "]");
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, etiquetas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPlanificacion.setAdapter(adapter);
        spPlanificacion.setEnabled(!planificaciones.isEmpty());
    }

    private void cargarLiquidaciones() {
        liquidaciones = db.listarLiquidaciones();
        listAdapter.clear();
        for (Liquidacion liq : liquidaciones) {
            listAdapter.add(liq.getEtiquetaLista());
        }
        listAdapter.notifyDataSetChanged();
        idLiqSeleccionada = null;
        btnEliminar.setEnabled(false);
    }

    private void recalcularMonto() {
        try {
            float peso = Float.parseFloat(etPesoCapturado.getText().toString().trim());
            float precio = Float.parseFloat(etPrecioPorKg.getText().toString().trim());
            tvMontoEstimado.setText(String.format(Locale.getDefault(), "$ %.2f", peso * precio));
        } catch (NumberFormatException e) {
            tvMontoEstimado.setText("$ 0.00");
        }
    }

    private void guardar() {
        if (planificaciones.isEmpty()) {
            Toast.makeText(this, "No hay planificaciones disponibles. Cree una en M4: Planificación Faenas.", Toast.LENGTH_LONG).show();
            return;
        }

        String pesoStr = etPesoCapturado.getText().toString().trim();
        String precioStr = etPrecioPorKg.getText().toString().trim();

        if (pesoStr.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Ingrese el peso capturado y el precio por kg", Toast.LENGTH_SHORT).show();
            return;
        }

        float peso, precio;
        try {
            peso = Float.parseFloat(pesoStr);
            precio = Float.parseFloat(precioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores numéricos inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (peso <= 0 || precio <= 0) {
            Toast.makeText(this, "El peso y el precio deben ser mayores a cero", Toast.LENGTH_SHORT).show();
            return;
        }

        float monto = peso * precio;
        PlanificacionFaena planSel = planificaciones.get(spPlanificacion.getSelectedItemPosition());
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        long id = db.insertarLiquidacion(
                planSel.getId(),
                peso, precio, monto,
                etCapitan.getText().toString().trim(),
                etObs.getText().toString().trim(),
                fecha);

        if (id != -1) {
            Toast.makeText(this, String.format(Locale.getDefault(),
                    "Pago registrado: $%.2f por %.1f kg", monto, peso), Toast.LENGTH_LONG).show();
            limpiarFormulario();
            cargarLiquidaciones();
        } else {
            Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminar() {
        if (idLiqSeleccionada == null) return;
        if (db.eliminarLiquidacion(idLiqSeleccionada)) {
            Toast.makeText(this, "Liquidación eliminada", Toast.LENGTH_SHORT).show();
            cargarLiquidaciones();
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarFormulario() {
        etPesoCapturado.setText("");
        etPrecioPorKg.setText("");
        etCapitan.setText("");
        etObs.setText("");
        tvMontoEstimado.setText("$ 0.00");
        if (!planificaciones.isEmpty()) spPlanificacion.setSelection(0);
    }
}
