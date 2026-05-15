package com.example.tareafishgold;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BaseDatosSQLite extends SQLiteOpenHelper {

    // Datos de la DB alineados con el proyecto Puerto Seguro [cite: 8, 15]
    private static final String DATABASE_NAME = "PuertoSeguro.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_SUPERVISORES = "supervisores";

    // Nombres de columnas constantes para evitar errores de dedo
    private static final String COL_CEDULA = "cedula";
    private static final String COL_NOMBRES = "nombres";
    private static final String COL_APELLIDOS = "apellidos";
    private static final String COL_EDAD = "edad";
    private static final String COL_FECHA = "fecha_nacimiento";
    private static final String COL_NACIONALIDAD = "nacionalidad";
    private static final String COL_GENERO = "genero";
    private static final String COL_ESTADO_CIVIL = "estado_civil";
    private static final String COL_NIVEL_INGLES = "nivel_ingles";
    private static final String COL_PASSWORD = "password";

    public BaseDatosSQLite(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_SUPERVISORES + " (" +
                COL_CEDULA + " TEXT PRIMARY KEY, " +
                COL_NOMBRES + " TEXT, " +
                COL_APELLIDOS + " TEXT, " +
                COL_EDAD + " INTEGER, " +
                COL_FECHA + " TEXT, " +
                COL_NACIONALIDAD + " TEXT, " +
                COL_GENERO + " TEXT, " +
                COL_ESTADO_CIVIL + " TEXT, " +
                COL_NIVEL_INGLES + " REAL, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUPERVISORES);
        onCreate(db);
    }

    // --- MÉTODOS CRUD ---

    public boolean insertarSupervisor(String cedula, String nombres, String apellidos, int edad,
                                      String fecha, String nac, String gen, String ec,
                                      float ingles, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CEDULA, cedula);
        values.put(COL_NOMBRES, nombres);
        values.put(COL_APELLIDOS, apellidos);
        values.put(COL_EDAD, edad);
        values.put(COL_FECHA, fecha);
        values.put(COL_NACIONALIDAD, nac);
        values.put(COL_GENERO, gen);
        values.put(COL_ESTADO_CIVIL, ec);
        values.put(COL_NIVEL_INGLES, ingles);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_SUPERVISORES, null, values);
        return result != -1;
    }

    // NUEVO: Método para buscar un supervisor específico por su ID/Cédula
    public Cursor buscarSupervisorPorCedula(String cedula) {
        SQLiteDatabase db = this.getReadableDatabase();
        // SELECT * FROM supervisores WHERE cedula = 'x'
        return db.rawQuery("SELECT * FROM " + TABLE_SUPERVISORES + " WHERE " + COL_CEDULA + " = ?", new String[]{cedula});
    }

    // NUEVO: Método para actualizar registros existentes
    public boolean actualizarSupervisor(String cedula, String nombres, String apellidos, int edad,
                                        String fecha, String nac, String gen, String ec,
                                        float ingles, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRES, nombres);
        values.put(COL_APELLIDOS, apellidos);
        values.put(COL_EDAD, edad);
        values.put(COL_FECHA, fecha);
        values.put(COL_NACIONALIDAD, nac);
        values.put(COL_GENERO, gen);
        values.put(COL_ESTADO_CIVIL, ec);
        values.put(COL_NIVEL_INGLES, ingles);
        values.put(COL_PASSWORD, password);

        // Actualizamos donde la cédula coincida
        int numFilas = db.update(TABLE_SUPERVISORES, values, COL_CEDULA + " = ?", new String[]{cedula});
        return numFilas > 0;
    }

    // NUEVO: Método para eliminar un registro
    public boolean eliminarSupervisor(String cedula) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numFilas = db.delete(TABLE_SUPERVISORES, COL_CEDULA + " = ?", new String[]{cedula});
        return numFilas > 0;
    }

    // LÓGICA DE NEGOCIO / SEGURIDAD
    public boolean validarLogin(String cedula, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_CEDULA + " = ? AND " + COL_PASSWORD + " = ?";
        String[] selectionArgs = {cedula, password};

        Cursor cursor = db.query(TABLE_SUPERVISORES, null, selection, selectionArgs, null, null, null);
        boolean loginExitoso = cursor.getCount() > 0;
        cursor.close();
        return loginExitoso;
    }

    public String obtenerTodosLosSupervisores() {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUPERVISORES, null);

        if (cursor.moveToFirst()) {
            do {
                sb.append("ID/Cédula: ").append(cursor.getString(0)).append("\n")
                        .append("Nombre: ").append(cursor.getString(1)).append(" ").append(cursor.getString(2)).append("\n")
                        .append("-----------------------------------\n");
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sb.toString();
    }
}