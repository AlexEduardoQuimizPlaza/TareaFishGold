package com.example.tareafishgold;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BaseDatosSQLite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PuertoSeguro.db";
    private static final int DATABASE_VERSION = 3;

    // --- Tabla supervisores ---
    private static final String TABLE_SUPERVISORES = "supervisores";
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

    // --- Tabla trabajadores ---
    private static final String TABLE_TRABAJADORES = "trabajadores";
    private static final String TRAB_CEDULA = "cedula";
    private static final String TRAB_NOMBRE = "nombre_completo";
    private static final String TRAB_ROL = "rol";
    private static final String TRAB_TELEFONO = "telefono";
    private static final String TRAB_DIRECCION = "direccion";
    private static final String TRAB_ESTADO = "estado";
    private static final String TRAB_FECHA_REG = "fecha_registro";

    // --- Tabla viajes ---
    private static final String TABLE_VIAJES = "viajes";
    private static final String VIAJE_ID = "id";
    private static final String VIAJE_CODIGO = "codigo_viaje";
    private static final String VIAJE_EMBARCACION = "embarcacion";
    private static final String VIAJE_DESTINO = "destino";
    private static final String VIAJE_FECHA = "fecha_salida";
    private static final String VIAJE_META = "meta_kg";
    private static final String VIAJE_ESTADO = "estado";

    // --- Tabla faena_asistencia ---
    private static final String TABLE_FAENA = "faena_asistencia";
    private static final String FAENA_ID = "id";
    private static final String FAENA_VIAJE_ID = "viaje_id";
    private static final String FAENA_CEDULA = "trabajador_cedula";

    public BaseDatosSQLite(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SUPERVISORES + " (" +
                COL_CEDULA + " TEXT PRIMARY KEY, " +
                COL_NOMBRES + " TEXT, " +
                COL_APELLIDOS + " TEXT, " +
                COL_EDAD + " INTEGER, " +
                COL_FECHA + " TEXT, " +
                COL_NACIONALIDAD + " TEXT, " +
                COL_GENERO + " TEXT, " +
                COL_ESTADO_CIVIL + " TEXT, " +
                COL_NIVEL_INGLES + " REAL, " +
                COL_PASSWORD + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_TRABAJADORES + " (" +
                TRAB_CEDULA + " TEXT PRIMARY KEY, " +
                TRAB_NOMBRE + " TEXT NOT NULL, " +
                TRAB_ROL + " TEXT, " +
                TRAB_TELEFONO + " TEXT, " +
                TRAB_DIRECCION + " TEXT, " +
                TRAB_ESTADO + " TEXT DEFAULT 'Activo', " +
                TRAB_FECHA_REG + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_VIAJES + " (" +
                VIAJE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VIAJE_CODIGO + " TEXT UNIQUE NOT NULL, " +
                VIAJE_EMBARCACION + " TEXT, " +
                VIAJE_DESTINO + " TEXT, " +
                VIAJE_FECHA + " TEXT, " +
                VIAJE_META + " REAL, " +
                VIAJE_ESTADO + " TEXT DEFAULT 'Pendiente')");

        db.execSQL("CREATE TABLE " + TABLE_FAENA + " (" +
                FAENA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FAENA_VIAJE_ID + " INTEGER, " +
                FAENA_CEDULA + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAENA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIAJES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRABAJADORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUPERVISORES);
        onCreate(db);
    }

    // ===== CRUD SUPERVISORES =====

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
        return db.insert(TABLE_SUPERVISORES, null, values) != -1;
    }

    public Cursor buscarSupervisorPorCedula(String cedula) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SUPERVISORES + " WHERE " + COL_CEDULA + " = ?",
                new String[]{cedula});
    }

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
        return db.update(TABLE_SUPERVISORES, values, COL_CEDULA + " = ?", new String[]{cedula}) > 0;
    }

    public boolean eliminarSupervisor(String cedula) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SUPERVISORES, COL_CEDULA + " = ?", new String[]{cedula}) > 0;
    }

    public boolean validarLogin(String cedula, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SUPERVISORES, null,
                COL_CEDULA + " = ? AND " + COL_PASSWORD + " = ?",
                new String[]{cedula, password}, null, null, null);
        boolean ok = cursor.getCount() > 0;
        cursor.close();
        return ok;
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

    // ===== CRUD TRABAJADORES =====

    public boolean insertarTrabajador(String cedula, String nombre, String rol,
                                      String telefono, String direccion, String estado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRAB_CEDULA, cedula);
        values.put(TRAB_NOMBRE, nombre);
        values.put(TRAB_ROL, rol);
        values.put(TRAB_TELEFONO, telefono);
        values.put(TRAB_DIRECCION, direccion);
        values.put(TRAB_ESTADO, estado);
        values.put(TRAB_FECHA_REG,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        return db.insert(TABLE_TRABAJADORES, null, values) != -1;
    }

    public boolean actualizarTrabajador(String cedula, String nombre, String rol,
                                        String telefono, String direccion, String estado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRAB_NOMBRE, nombre);
        values.put(TRAB_ROL, rol);
        values.put(TRAB_TELEFONO, telefono);
        values.put(TRAB_DIRECCION, direccion);
        values.put(TRAB_ESTADO, estado);
        return db.update(TABLE_TRABAJADORES, values, TRAB_CEDULA + " = ?", new String[]{cedula}) > 0;
    }

    public boolean eliminarTrabajador(String cedula) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TRABAJADORES, TRAB_CEDULA + " = ?", new String[]{cedula}) > 0;
    }

    // Búsqueda en tiempo real: filtra por cédula, nombre o rol
    public Cursor buscarTrabajadores(String filtro) {
        SQLiteDatabase db = this.getReadableDatabase();
        String like = "%" + filtro + "%";
        return db.rawQuery(
                "SELECT " + TRAB_CEDULA + ", " + TRAB_NOMBRE + ", " + TRAB_ROL + ", " +
                        TRAB_TELEFONO + ", " + TRAB_DIRECCION + ", " + TRAB_ESTADO +
                        " FROM " + TABLE_TRABAJADORES +
                        " WHERE " + TRAB_CEDULA + " LIKE ? OR " + TRAB_NOMBRE + " LIKE ? OR " + TRAB_ROL + " LIKE ?" +
                        " ORDER BY " + TRAB_NOMBRE,
                new String[]{like, like, like});
    }

    // Devuelve solo trabajadores activos (para selección de tripulación)
    public Cursor obtenerTrabajadoresActivos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT " + TRAB_CEDULA + ", " + TRAB_NOMBRE + ", " + TRAB_ROL +
                        " FROM " + TABLE_TRABAJADORES +
                        " WHERE " + TRAB_ESTADO + " = 'Activo'" +
                        " ORDER BY " + TRAB_NOMBRE, null);
    }

    // ===== CRUD VIAJES / PLANIFICACIÓN =====

    public long insertarViaje(String codigo, String embarcacion, String destino,
                              String fecha, float meta, String estado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VIAJE_CODIGO, codigo);
        values.put(VIAJE_EMBARCACION, embarcacion);
        values.put(VIAJE_DESTINO, destino);
        values.put(VIAJE_FECHA, fecha);
        values.put(VIAJE_META, meta);
        values.put(VIAJE_ESTADO, estado);
        return db.insert(TABLE_VIAJES, null, values);
    }

    public boolean actualizarViaje(String id, String embarcacion, String destino,
                                   String fecha, float meta, String estado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VIAJE_EMBARCACION, embarcacion);
        values.put(VIAJE_DESTINO, destino);
        values.put(VIAJE_FECHA, fecha);
        values.put(VIAJE_META, meta);
        values.put(VIAJE_ESTADO, estado);
        return db.update(TABLE_VIAJES, values, VIAJE_ID + " = ?", new String[]{id}) > 0;
    }

    // Búsqueda en tiempo real: filtra por código o embarcación
    public Cursor buscarViajes(String filtro) {
        SQLiteDatabase db = this.getReadableDatabase();
        String like = "%" + filtro + "%";
        return db.rawQuery(
                "SELECT " + VIAJE_ID + ", " + VIAJE_CODIGO + ", " + VIAJE_EMBARCACION + ", " +
                        VIAJE_DESTINO + ", " + VIAJE_FECHA + ", " + VIAJE_META + ", " + VIAJE_ESTADO +
                        " FROM " + TABLE_VIAJES +
                        " WHERE " + VIAJE_CODIGO + " LIKE ? OR " + VIAJE_EMBARCACION + " LIKE ?" +
                        " ORDER BY " + VIAJE_ID + " DESC",
                new String[]{like, like});
    }

    // ===== TRIPULACIÓN (faena_asistencia) =====

    public void guardarTripulacion(String viajeId, List<String> cedulas) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAENA, FAENA_VIAJE_ID + " = ?", new String[]{viajeId});
        for (String cedula : cedulas) {
            ContentValues values = new ContentValues();
            values.put(FAENA_VIAJE_ID, viajeId);
            values.put(FAENA_CEDULA, cedula);
            db.insert(TABLE_FAENA, null, values);
        }
    }

    public Cursor obtenerTripulacionViaje(String viajeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT " + FAENA_CEDULA + " FROM " + TABLE_FAENA +
                        " WHERE " + FAENA_VIAJE_ID + " = ?",
                new String[]{viajeId});
    }
}
