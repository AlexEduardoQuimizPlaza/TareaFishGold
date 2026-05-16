package com.example.tareafishgold;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tareafishgold.contract.IReporteViajeRepository;
import com.example.tareafishgold.data.ReporteViajeRepositoryImpl;
import com.example.tareafishgold.model.ViajeReporte;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * M6: Reporte de Viajes
 * CRUD completo con reportes guardados en la BD (tabla reportes_guardados).
 */
public class ReporteViajeActivity extends AppCompatActivity {

    // ─── Vistas ──────────────────────────────────────────────────
    private TextInputEditText etBuscarViajes, etBuscarReportes;
    private Spinner spEstadoFiltro;
    private ListView lvViajes, lvReportesGuardados;
    private TextView tvDetalle;
    private Button btnGuardarReporte, btnEliminarReporte, btnLimpiar;
    private Button btnTabViajes, btnTabReportes;

    // ─── Datos ───────────────────────────────────────────────────
    private IReporteViajeRepository repository;
    private List<ViajeReporte> viajes = new ArrayList<>();
    private List<ViajeReporte> reportesGuardados = new ArrayList<>();
    private ArrayAdapter<String> listAdapterViajes, listAdapterReportes;
    private ViajeReporte viajeSeleccionado = null;
    private ViajeReporte reporteSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_viaje);

        BaseDatosSQLite db = new BaseDatosSQLite(this);
        repository = new ReporteViajeRepositoryImpl(db);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbarM6);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Vistas
        etBuscarViajes         = findViewById(R.id.etBuscarReporte);
        etBuscarReportes       = findViewById(R.id.etBuscarReportes);
        spEstadoFiltro         = findViewById(R.id.spEstadoFiltro);
        lvViajes               = findViewById(R.id.lvViajesReporte);
        lvReportesGuardados    = findViewById(R.id.lvReportesGuardados);
        tvDetalle              = findViewById(R.id.tvDetalleViaje);
        btnGuardarReporte      = findViewById(R.id.btnGuardarReporte);
        btnEliminarReporte     = findViewById(R.id.btnEliminarReporte);
        btnLimpiar             = findViewById(R.id.btnLimpiarReporte);
        btnTabViajes           = findViewById(R.id.btnTabViajes);
        btnTabReportes         = findViewById(R.id.btnTabReportes);

        configurarSpinnerEstados();

        listAdapterViajes = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, new ArrayList<>());
        lvViajes.setAdapter(listAdapterViajes);

        listAdapterReportes = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, new ArrayList<>());
        lvReportesGuardados.setAdapter(listAdapterReportes);

        cargarViajes();
        cargarReportesGuardados();

        etBuscarViajes.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                cargarViajes();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etBuscarReportes.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                cargarReportesGuardados();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        spEstadoFiltro.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                cargarViajes();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        lvViajes.setOnItemClickListener((parent, view, pos, id) -> {
            viajeSeleccionado = viajes.get(pos);
            mostrarDetalle(viajeSeleccionado);
            btnGuardarReporte.setVisibility(View.VISIBLE);
        });

        lvReportesGuardados.setOnItemClickListener((parent, view, pos, id) -> {
            reporteSeleccionado = reportesGuardados.get(pos);
            mostrarDetalle(reporteSeleccionado);
            btnEliminarReporte.setVisibility(View.VISIBLE);
        });

        btnGuardarReporte.setOnClickListener(v -> guardarReporte());
        btnEliminarReporte.setOnClickListener(v -> eliminarReporte());
        btnLimpiar.setOnClickListener(v -> limpiarDetalle());
        btnTabViajes.setOnClickListener(v -> mostrarTab(0));
        btnTabReportes.setOnClickListener(v -> mostrarTab(1));
    }


    private void configurarSpinnerEstados() {
        String[] estados = {"Todos", "Pendiente", "En Curso", "Finalizado"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstadoFiltro.setAdapter(adapter);
    }


    private void cargarViajes() {
        String filtro = etBuscarViajes.getText().toString();
        String estadoSeleccionado = spEstadoFiltro.getSelectedItem().toString();

        if ("Todos".equals(estadoSeleccionado)) {
            viajes = repository.buscarViajes(filtro);
        } else {
            viajes = repository.viajePorEstado(estadoSeleccionado);
            viajes = filtrarPorTexto(viajes, filtro);
        }

        listAdapterViajes.clear();
        for (ViajeReporte v : viajes) listAdapterViajes.add(v.getEtiquetaLista());
        listAdapterViajes.notifyDataSetChanged();
        limpiarDetalle();
    }


    private void cargarReportesGuardados() {
        String filtro = etBuscarReportes.getText().toString();

        if (filtro.isEmpty()) {
            reportesGuardados = repository.obtenerReportesGuardados();
        } else {
            reportesGuardados = repository.buscarReportesGuardados(filtro);
        }

        listAdapterReportes.clear();
        for (ViajeReporte r : reportesGuardados) listAdapterReportes.add(r.getEtiquetaLista());
        listAdapterReportes.notifyDataSetChanged();
    }


    private List<ViajeReporte> filtrarPorTexto(List<ViajeReporte> lista, String filtro) {
        if (filtro.isEmpty()) return lista;
        List<ViajeReporte> resultado = new ArrayList<>();
        String lower = filtro.toLowerCase();
        for (ViajeReporte v : lista) {
            if (v.getCodigo().toLowerCase().contains(lower) ||
                    v.getEmbarcacion().toLowerCase().contains(lower) ||
                    v.getDestino().toLowerCase().contains(lower)) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    private void mostrarDetalle(ViajeReporte viaje) {
        tvDetalle.setText(viaje.getDetalleCompleto());
        tvDetalle.setVisibility(View.VISIBLE);
    }

    private void limpiarDetalle() {
        tvDetalle.setText("");
        tvDetalle.setVisibility(View.GONE);
        btnGuardarReporte.setVisibility(View.GONE);
        btnEliminarReporte.setVisibility(View.GONE);
        viajeSeleccionado = null;
        reporteSeleccionado = null;
    }


    private void guardarReporte() {
        if (viajeSeleccionado == null) {
            Toast.makeText(this, "Selecciona un viaje primero", Toast.LENGTH_SHORT).show();
            return;
        }

        String ahora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        ViajeReporte reporteConFecha = new ViajeReporte(
                viajeSeleccionado.getId(),
                viajeSeleccionado.getCodigo(),
                viajeSeleccionado.getEmbarcacion(),
                viajeSeleccionado.getDestino(),
                viajeSeleccionado.getFechaSalida(),
                viajeSeleccionado.getMetaKg(),
                viajeSeleccionado.getEstado(),
                viajeSeleccionado.getTotalTrabajadores(),
                viajeSeleccionado.getTotalFaenas(),
                viajeSeleccionado.getRegistrosAsistencia(),
                ahora
        );

        long id = repository.guardarReporte(reporteConFecha);
        if (id != -1) {
            Toast.makeText(this, "Reporte guardado", Toast.LENGTH_SHORT).show();
            cargarReportesGuardados();
            limpiarDetalle();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarReporte() {
        if (reporteSeleccionado == null) {
            Toast.makeText(this, "Selecciona un reporte", Toast.LENGTH_SHORT).show();
            return;
        }

        if (repository.eliminarReporte(reporteSeleccionado.getId())) {
            Toast.makeText(this, "Reporte eliminado", Toast.LENGTH_SHORT).show();
            cargarReportesGuardados();
            limpiarDetalle();
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }


    private void mostrarTab(int tab) {
        if (tab == 0) {
            lvViajes.setVisibility(View.VISIBLE);
            lvReportesGuardados.setVisibility(View.GONE);
            etBuscarViajes.setVisibility(View.VISIBLE);
            etBuscarReportes.setVisibility(View.GONE);
            spEstadoFiltro.setVisibility(View.VISIBLE);
            btnTabViajes.setBackgroundColor(0xFF2196F3);
            btnTabReportes.setBackgroundColor(0xFF9E9E9E);
        } else {
            lvViajes.setVisibility(View.GONE);
            lvReportesGuardados.setVisibility(View.VISIBLE);
            etBuscarViajes.setVisibility(View.GONE);
            etBuscarReportes.setVisibility(View.VISIBLE);
            spEstadoFiltro.setVisibility(View.GONE);
            btnTabViajes.setBackgroundColor(0xFF9E9E9E);
            btnTabReportes.setBackgroundColor(0xFF2196F3);
        }
    }
}