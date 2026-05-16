package com.example.tareafishgold;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class Register extends AppCompatActivity {

    private TextInputEditText etCedula, etNombres, etApellidos, etEdad, etFecha;
    private Spinner spNacionalidad, spGenero;
    private RadioGroup rgEstadoCivil;
    private RatingBar ratingIngles;
    private BaseDatosSQLite dbHelper; // Instancia para manejar SQLite

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Inicializar el ayudante de la base de datos
        dbHelper = new BaseDatosSQLite(this);

        vincularComponentes();

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        configurarSpinners();

        if (etFecha != null) {
            etFecha.setFocusable(false);
            etFecha.setOnClickListener(v -> mostrarCalendario());
        }

        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        Button btnRecuperar = findViewById(R.id.btnRecuperar);
        Button btnBorrar = findViewById(R.id.btnBorrar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        if (btnRegistrar != null) btnRegistrar.setOnClickListener(this::guardar);
        if (btnRecuperar != null) btnRecuperar.setOnClickListener(this::recuperar);
        if (btnBorrar != null) btnBorrar.setOnClickListener(v -> vaciarFormulario());
        if (btnCancelar != null) btnCancelar.setOnClickListener(v -> finish());
    }

    private void vincularComponentes() {
        etCedula = findViewById(R.id.etCedula);
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etEdad = findViewById(R.id.etEdad);
        etFecha = findViewById(R.id.etFechaNacimiento);
        spNacionalidad = findViewById(R.id.spNacionalidad);
        spGenero = findViewById(R.id.spGenero);
        rgEstadoCivil = findViewById(R.id.rgEstadoCivil);
        ratingIngles = findViewById(R.id.ratingIngles);
    }

    public void guardar(View v) {
        String cedula = etCedula != null ? etCedula.getText().toString().trim() : "";
        String nombres = etNombres != null ? etNombres.getText().toString().trim() : "";
        String apellidos = etApellidos != null ? etApellidos.getText().toString().trim() : "";
        String edadStr = etEdad != null ? etEdad.getText().toString().trim() : "";
        String fecha = etFecha != null ? etFecha.getText().toString() : "";

        if (cedula.isEmpty() || nombres.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int edad = edadStr.isEmpty() ? 0 : Integer.parseInt(edadStr);
        String nacionalidad = (spNacionalidad != null && spNacionalidad.getSelectedItem() != null)
                ? spNacionalidad.getSelectedItem().toString() : "";
        String genero = (spGenero != null && spGenero.getSelectedItem() != null)
                ? spGenero.getSelectedItem().toString() : "";

        String estadoCivil = "";
        if (rgEstadoCivil != null) {
            int selectedId = rgEstadoCivil.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton rb = findViewById(selectedId);
                if (rb != null) estadoCivil = rb.getText().toString();
            }
        }

        float nivelIngles = ratingIngles != null ? ratingIngles.getRating() : 0.0f;

        // El supervisor usará su cédula como contraseña inicial para el login
        String password = cedula;

        // Guardar en la base de datos SQLite
        boolean insertado = dbHelper.insertarSupervisor(
                cedula, nombres, apellidos, edad, fecha,
                nacionalidad, genero, estadoCivil, nivelIngles, password
        );

        if (insertado) {
            Toast.makeText(this, "Supervisor registrado correctamente", Toast.LENGTH_SHORT).show();
            vaciarFormulario();
        } else {
            Toast.makeText(this, "Error: El supervisor ya existe", Toast.LENGTH_SHORT).show();
        }
    }

    public void recuperar(View v) {
        // Obtenemos los datos directamente desde SQLite
        String datos = dbHelper.obtenerTodosLosSupervisores();

        if (datos.isEmpty()) {
            Toast.makeText(this, "No hay supervisores registrados", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Supervisores Registrados");
        builder.setMessage(datos);
        builder.setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void configurarSpinners() {
        ArrayAdapter<CharSequence> adp1 = ArrayAdapter.createFromResource(this,
                R.array.nacionalidades, android.R.layout.simple_spinner_item);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spNacionalidad != null) spNacionalidad.setAdapter(adp1);

        ArrayAdapter<CharSequence> adp2 = ArrayAdapter.createFromResource(this,
                R.array.generos, android.R.layout.simple_spinner_item);
        adp2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spGenero != null) spGenero.setAdapter(adp2);
    }

    private void mostrarCalendario() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            if (etFecha != null) {
                etFecha.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, (month + 1), year));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }

    private void vaciarFormulario() {
        TextInputEditText[] campos = {etCedula, etNombres, etApellidos, etEdad, etFecha};
        for (TextInputEditText campo : campos) {
            if (campo != null) campo.setText("");
        }
        if (spNacionalidad != null) spNacionalidad.setSelection(0);
        if (spGenero != null) spGenero.setSelection(0);
        if (rgEstadoCivil != null) rgEstadoCivil.clearCheck();
        if (ratingIngles != null) ratingIngles.setRating(0f);
        if (etCedula != null) etCedula.requestFocus();
    }
}