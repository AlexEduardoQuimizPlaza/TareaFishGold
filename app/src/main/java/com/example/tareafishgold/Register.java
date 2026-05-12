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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Locale;

public class Register extends AppCompatActivity {

    private TextInputEditText etCedula, etNombres, etApellidos, etEdad, etFecha;
    private Spinner spNacionalidad, spGenero;
    private RadioGroup rgEstadoCivil;
    private RatingBar ratingIngles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

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
            // Evita que salga el teclado al tocar la fecha
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
        String edad = etEdad != null ? etEdad.getText().toString().trim() : "";
        String fecha = etFecha != null ? etFecha.getText().toString() : "";

        // Validación básica
        if (cedula.isEmpty() || nombres.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

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

        String nivelIngles = ratingIngles != null ? String.valueOf(ratingIngles.getRating()) : "0.0";

        String data = "Cédula: " + cedula + "\n" +
                "Nombres: " + nombres + "\n" +
                "Apellidos: " + apellidos + "\n" +
                "Edad: " + edad + "\n" +
                "Fecha de Nacimiento: " + fecha + "\n" +
                "Nacionalidad: " + nacionalidad + "\n" +
                "Género: " + genero + "\n" +
                "Estado Civil: " + estadoCivil + "\n" +
                "Nivel de Inglés: " + nivelIngles + "\n" +
                "-----------------------------------\n";

        guardarSD(data);
    }

    private void guardarSD(String datos) {
        File f = new File(getExternalFilesDir(null), "RegistrarUsuario.txt");
        // Uso de try-with-resources para cerrar flujos automáticamente
        try (FileOutputStream fos = new FileOutputStream(f, true);
             OutputStreamWriter out = new OutputStreamWriter(fos)) {

            out.write(datos);
            Log.d("FishGold", "Datos guardados en: " + f.getAbsolutePath());
            Toast.makeText(this, "Se ha grabado correctamente en FishGold", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("SD_Error", "Error al guardar: " + e.getMessage());
            Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
        }
    }

    public void recuperar(View v) {
        File f = new File(getExternalFilesDir(null), "RegistrarUsuario.txt");
        if (!f.exists()) {
            Toast.makeText(this, "No hay datos para recuperar", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(f);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea).append("\n");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Historial de Registros");
            builder.setMessage(sb.toString());
            builder.setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
            builder.show();

        } catch (Exception e) {
            Log.e("SD_Error", "Error al recuperar: " + e.getMessage());
            Toast.makeText(this, "Error al leer el archivo", Toast.LENGTH_SHORT).show();
        }
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
                // Formato profesional: DD/MM/YYYY
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
        Toast.makeText(this, "Formulario reiniciado", Toast.LENGTH_SHORT).show();
    }
}