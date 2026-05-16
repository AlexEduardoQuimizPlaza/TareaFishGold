package com.example.tareafishgold;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.tareafishgold.model.PlanificacionFaena;
import com.example.tareafishgold.model.RegistroAsistencia;
import com.example.tareafishgold.model.ViajeReporte;
import com.example.tareafishgold.model.ViajeResumen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BaseDatosSQLite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PuertoSeguro.db";
    private static final int DATABASE_VERSION = 6;

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

    // --- Tabla faena_asistencia (tripulación por viaje) ---
    private static final String TABLE_FAENA = "faena_asistencia";
    private static final String FAENA_ID = "id";
    private static final String FAENA_VIAJE_ID = "viaje_id";
    private static final String FAENA_CEDULA = "trabajador_cedula";

    // --- M4: planificaciones de faena ---
    private static final String TABLE_PLAN_FAENA = "planificaciones_faena";
    private static final String PF_ID = "id";
    private static final String PF_VIAJE_ID = "viaje_id";
    private static final String PF_NOMBRE = "nombre_faena";
    private static final String PF_INICIO = "fecha_inicio";
    private static final String PF_FIN = "fecha_fin";
    private static final String PF_TURNO = "turno";
    private static final String PF_ESTADO = "estado";
    private static final String PF_OBS = "observaciones";

    // --- M5: control de asistencia ---
    private static final String TABLE_ASISTENCIA = "control_asistencia";
    private static final String ASIS_ID = "id";
    private static final String ASIS_PLAN_ID = "planificacion_id";
    private static final String ASIS_CEDULA = "trabajador_cedula";
    private static final String ASIS_FECHA = "fecha_registro";
    private static final String ASIS_ESTADO = "estado";
    private static final String ASIS_HORAS = "horas";

    private static final String TABLE_PLAN_ROLES    = "planificacion_roles";
    private static final String PR_ID               = "id";
    private static final String PR_VIAJE_ID         = "viaje_id";
    private static final String PR_CEDULA           = "trabajador_cedula";
    private static final String PR_ROL              = "rol";
    private static final String PR_TURNO            = "turno";
    private static final String PR_FECHA_ASIGNACION = "fecha_asignacion";
    private static final String PR_ESTADO           = "estado";
    private static final String PR_OBS              = "observaciones";


    private static final String TABLE_REPORTES = "reportes_guardados";
    private static final String REP_ID = "id";
    private static final String REP_VIAJE_ID = "viaje_id";
    private static final String REP_CODIGO = "codigo";
    private static final String REP_EMBARCACION = "embarcacion";
    private static final String REP_DESTINO = "destino";
    private static final String REP_FECHA_SALIDA = "fecha_salida";
    private static final String REP_META_KG = "meta_kg";
    private static final String REP_ESTADO = "estado";
    private static final String REP_TRAB = "total_trabajadores";
    private static final String REP_FAENAS = "total_faenas";
    private static final String REP_ASIS = "registros_asistencia";
    private static final String REP_FECHA_GEN = "fecha_generacion";

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

        db.execSQL("CREATE TABLE " + TABLE_PLAN_FAENA + " (" +
                PF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PF_VIAJE_ID + " INTEGER NOT NULL, " +
                PF_NOMBRE + " TEXT NOT NULL, " +
                PF_INICIO + " TEXT, " +
                PF_FIN + " TEXT, " +
                PF_TURNO + " TEXT, " +
                PF_ESTADO + " TEXT DEFAULT 'Programada', " +
                PF_OBS + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_ASISTENCIA + " (" +
                ASIS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ASIS_PLAN_ID + " INTEGER NOT NULL, " +
                ASIS_CEDULA + " TEXT NOT NULL, " +
                ASIS_FECHA + " TEXT NOT NULL, " +
                ASIS_ESTADO + " TEXT NOT NULL, " +
                ASIS_HORAS + " REAL DEFAULT 0, " +
                "UNIQUE(" + ASIS_PLAN_ID + ", " + ASIS_CEDULA + ", " + ASIS_FECHA + "))");
        db.execSQL("CREATE TABLE " + TABLE_REPORTES + " (" +
                REP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                REP_VIAJE_ID + " INTEGER NOT NULL, " +
                REP_CODIGO + " TEXT NOT NULL, " +
                REP_EMBARCACION + " TEXT, " +
                REP_DESTINO + " TEXT, " +
                REP_FECHA_SALIDA + " TEXT, " +
                REP_META_KG + " REAL, " +
                REP_ESTADO + " TEXT, " +
                REP_TRAB + " INTEGER, " +
                REP_FAENAS + " INTEGER, " +
                REP_ASIS + " INTEGER, " +
                REP_FECHA_GEN + " TEXT, " +
                "UNIQUE(" + REP_VIAJE_ID + "))");

        }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASISTENCIA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAN_FAENA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAENA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIAJES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRABAJADORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUPERVISORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTES);
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

    public boolean existeSupervisor(String cedula) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SUPERVISORES, new String[]{COL_CEDULA},
                COL_CEDULA + " = ?", new String[]{cedula}, null, null, null);
        boolean ok = cursor.getCount() > 0;
        cursor.close();
        return ok;
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

    public String obtenerNombreTrabajador(String cedula) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + TRAB_NOMBRE + " FROM " + TABLE_TRABAJADORES +
                        " WHERE " + TRAB_CEDULA + " = ?",
                new String[]{cedula});
        String nombre = cedula;
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0);
        }
        cursor.close();
        return nombre;
    }

    // ===== M4: PLANIFICACIONES DE FAENA =====

    public List<ViajeResumen> listarViajesActivos() {
        List<ViajeResumen> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + VIAJE_ID + ", " + VIAJE_CODIGO + ", " + VIAJE_EMBARCACION + ", " +
                        VIAJE_DESTINO + ", " + VIAJE_ESTADO +
                        " FROM " + TABLE_VIAJES +
                        " WHERE " + VIAJE_ESTADO + " != 'Finalizado'" +
                        " ORDER BY " + VIAJE_ID + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                lista.add(new ViajeResumen(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public long insertarPlanificacionFaena(long viajeId, String nombreFaena, String fechaInicio,
                                           String fechaFin, String turno, String estado,
                                           String observaciones) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PF_VIAJE_ID, viajeId);
        values.put(PF_NOMBRE, nombreFaena);
        values.put(PF_INICIO, fechaInicio);
        values.put(PF_FIN, fechaFin);
        values.put(PF_TURNO, turno);
        values.put(PF_ESTADO, estado);
        values.put(PF_OBS, observaciones);
        return db.insert(TABLE_PLAN_FAENA, null, values);
    }

    public boolean actualizarPlanificacionFaena(long id, String nombreFaena, String fechaInicio,
                                                String fechaFin, String turno, String estado,
                                                String observaciones) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PF_NOMBRE, nombreFaena);
        values.put(PF_INICIO, fechaInicio);
        values.put(PF_FIN, fechaFin);
        values.put(PF_TURNO, turno);
        values.put(PF_ESTADO, estado);
        values.put(PF_OBS, observaciones);
        return db.update(TABLE_PLAN_FAENA, values, PF_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean eliminarPlanificacionFaena(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ASISTENCIA, ASIS_PLAN_ID + " = ?", new String[]{String.valueOf(id)});
        return db.delete(TABLE_PLAN_FAENA, PF_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public List<PlanificacionFaena> buscarPlanificacionesFaena(String filtro) {
        List<PlanificacionFaena> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String like = "%" + filtro + "%";
        Cursor cursor = db.rawQuery(
                "SELECT p." + PF_ID + ", p." + PF_VIAJE_ID + ", v." + VIAJE_CODIGO + ", p." + PF_NOMBRE +
                        ", p." + PF_INICIO + ", p." + PF_FIN + ", p." + PF_TURNO +
                        ", p." + PF_ESTADO + ", p." + PF_OBS +
                        " FROM " + TABLE_PLAN_FAENA + " p" +
                        " INNER JOIN " + TABLE_VIAJES + " v ON p." + PF_VIAJE_ID + " = v." + VIAJE_ID +
                        " WHERE p." + PF_NOMBRE + " LIKE ? OR v." + VIAJE_CODIGO + " LIKE ?" +
                        " ORDER BY p." + PF_ID + " DESC",
                new String[]{like, like});
        if (cursor.moveToFirst()) {
            do {
                lista.add(mapearPlanificacion(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public PlanificacionFaena obtenerPlanificacionFaena(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT p." + PF_ID + ", p." + PF_VIAJE_ID + ", v." + VIAJE_CODIGO + ", p." + PF_NOMBRE +
                        ", p." + PF_INICIO + ", p." + PF_FIN + ", p." + PF_TURNO +
                        ", p." + PF_ESTADO + ", p." + PF_OBS +
                        " FROM " + TABLE_PLAN_FAENA + " p" +
                        " INNER JOIN " + TABLE_VIAJES + " v ON p." + PF_VIAJE_ID + " = v." + VIAJE_ID +
                        " WHERE p." + PF_ID + " = ?",
                new String[]{String.valueOf(id)});
        PlanificacionFaena plan = null;
        if (cursor.moveToFirst()) {
            plan = mapearPlanificacion(cursor);
        }
        cursor.close();
        return plan;
    }

    private PlanificacionFaena mapearPlanificacion(Cursor cursor) {
        return new PlanificacionFaena(
                cursor.getLong(0),
                cursor.getLong(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8));
    }

    public long obtenerViajeIdDePlanificacion(long planificacionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + PF_VIAJE_ID + " FROM " + TABLE_PLAN_FAENA +
                        " WHERE " + PF_ID + " = ?",
                new String[]{String.valueOf(planificacionId)});
        long viajeId = -1;
        if (cursor.moveToFirst()) {
            viajeId = cursor.getLong(0);
        }
        cursor.close();
        return viajeId;
    }

    // ===== M5: CONTROL DE ASISTENCIA =====

    public List<RegistroAsistencia> listarAsistenciaPorPlanificacion(long planificacionId) {
        List<RegistroAsistencia> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT a." + ASIS_ID + ", a." + ASIS_PLAN_ID + ", a." + ASIS_CEDULA +
                        ", t." + TRAB_NOMBRE + ", a." + ASIS_FECHA + ", a." + ASIS_ESTADO + ", a." + ASIS_HORAS +
                        " FROM " + TABLE_ASISTENCIA + " a" +
                        " LEFT JOIN " + TABLE_TRABAJADORES + " t ON a." + ASIS_CEDULA + " = t." + TRAB_CEDULA +
                        " WHERE a." + ASIS_PLAN_ID + " = ?" +
                        " ORDER BY a." + ASIS_FECHA + " DESC, t." + TRAB_NOMBRE,
                new String[]{String.valueOf(planificacionId)});
        if (cursor.moveToFirst()) {
            do {
                lista.add(new RegistroAsistencia(
                        cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3) != null ? cursor.getString(3) : cursor.getString(2),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getFloat(6)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public boolean registrarAsistencia(long planificacionId, String trabajadorCedula,
                                       String fechaRegistro, String estado, float horas) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ASIS_PLAN_ID, planificacionId);
        values.put(ASIS_CEDULA, trabajadorCedula);
        values.put(ASIS_FECHA, fechaRegistro);
        values.put(ASIS_ESTADO, estado);
        values.put(ASIS_HORAS, horas);

        long id = db.insertWithOnConflict(TABLE_ASISTENCIA, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return id != -1;
    }

    public boolean eliminarRegistroAsistencia(long registroId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ASISTENCIA, ASIS_ID + " = ?", new String[]{String.valueOf(registroId)}) > 0;
    }

    //  M2: Reporte de Viaje

    public List<ViajeReporte> listarTodosLosViajes() {
        List<ViajeReporte> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " +
                        "v." + VIAJE_ID + ", v." + VIAJE_CODIGO + ", v." + VIAJE_EMBARCACION + ", " +
                        "v." + VIAJE_DESTINO + ", v." + VIAJE_FECHA + ", v." + VIAJE_META + ", v." + VIAJE_ESTADO + ", " +
                        "COUNT(DISTINCT f." + FAENA_CEDULA + ") as trab_count, " +
                        "COUNT(DISTINCT p." + PF_ID + ") as faena_count, " +
                        "COUNT(DISTINCT a." + ASIS_ID + ") as asis_count " +
                        "FROM " + TABLE_VIAJES + " v " +
                        "LEFT JOIN " + TABLE_FAENA + " f ON v." + VIAJE_ID + " = f." + FAENA_VIAJE_ID + " " +
                        "LEFT JOIN " + TABLE_PLAN_FAENA + " p ON v." + VIAJE_ID + " = p." + PF_VIAJE_ID + " " +
                        "LEFT JOIN " + TABLE_ASISTENCIA + " a ON p." + PF_ID + " = a." + ASIS_PLAN_ID + " " +
                        "GROUP BY v." + VIAJE_ID + " " +
                        "ORDER BY v." + VIAJE_ID + " DESC",
                null);
        if (cursor.moveToFirst()) {
            do {
                lista.add(new ViajeReporte(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getFloat(5),
                        cursor.getString(6),
                        cursor.getInt(7),
                        cursor.getInt(8),
                        cursor.getInt(9),
                        ""
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }


    public List<ViajeReporte> buscarViajesConEstadisticas(String filtro) {
        List<ViajeReporte> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String like = "%" + filtro + "%";
        Cursor cursor = db.rawQuery(
                "SELECT " +
                        "v." + VIAJE_ID + ", v." + VIAJE_CODIGO + ", v." + VIAJE_EMBARCACION + ", " +
                        "v." + VIAJE_DESTINO + ", v." + VIAJE_FECHA + ", v." + VIAJE_META + ", v." + VIAJE_ESTADO + ", " +
                        "COUNT(DISTINCT f." + FAENA_CEDULA + ") as trab_count, " +
                        "COUNT(DISTINCT p." + PF_ID + ") as faena_count, " +
                        "COUNT(DISTINCT a." + ASIS_ID + ") as asis_count " +
                        "FROM " + TABLE_VIAJES + " v " +
                        "LEFT JOIN " + TABLE_FAENA + " f ON v." + VIAJE_ID + " = f." + FAENA_VIAJE_ID + " " +
                        "LEFT JOIN " + TABLE_PLAN_FAENA + " p ON v." + VIAJE_ID + " = p." + PF_VIAJE_ID + " " +
                        "LEFT JOIN " + TABLE_ASISTENCIA + " a ON p." + PF_ID + " = a." + ASIS_PLAN_ID + " " +
                        "WHERE v." + VIAJE_CODIGO + " LIKE ? OR v." + VIAJE_EMBARCACION + " LIKE ? OR v." + VIAJE_DESTINO + " LIKE ? " +
                        "GROUP BY v." + VIAJE_ID + " " +
                        "ORDER BY v." + VIAJE_ID + " DESC",
                new String[]{like, like, like});
        if (cursor.moveToFirst()) {
            do {
                lista.add(new ViajeReporte(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getFloat(5),
                        cursor.getString(6),
                        cursor.getInt(7),
                        cursor.getInt(8),
                        cursor.getInt(9),
                        ""
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }


    public ViajeReporte obtenerViajeCompleto(long viajeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " +
                        "v." + VIAJE_ID + ", v." + VIAJE_CODIGO + ", v." + VIAJE_EMBARCACION + ", " +
                        "v." + VIAJE_DESTINO + ", v." + VIAJE_FECHA + ", v." + VIAJE_META + ", v." + VIAJE_ESTADO + ", " +
                        "COUNT(DISTINCT f." + FAENA_CEDULA + ") as trab_count, " +
                        "COUNT(DISTINCT p." + PF_ID + ") as faena_count, " +
                        "COUNT(DISTINCT a." + ASIS_ID + ") as asis_count " +
                        "FROM " + TABLE_VIAJES + " v " +
                        "LEFT JOIN " + TABLE_FAENA + " f ON v." + VIAJE_ID + " = f." + FAENA_VIAJE_ID + " " +
                        "LEFT JOIN " + TABLE_PLAN_FAENA + " p ON v." + VIAJE_ID + " = p." + PF_VIAJE_ID + " " +
                        "LEFT JOIN " + TABLE_ASISTENCIA + " a ON p." + PF_ID + " = a." + ASIS_PLAN_ID + " " +
                        "WHERE v." + VIAJE_ID + " = ? " +
                        "GROUP BY v." + VIAJE_ID,
                new String[]{String.valueOf(viajeId)});
        ViajeReporte viaje = null;
        if (cursor.moveToFirst()) {
            viaje = new ViajeReporte(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getFloat(5),
                    cursor.getString(6),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getInt(9),
                    ""
            );
        }
        cursor.close();
        return viaje;
    }

    public List<ViajeReporte> listarViajePorEstado(String estado) {
        List<ViajeReporte> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " +
                        "v." + VIAJE_ID + ", v." + VIAJE_CODIGO + ", v." + VIAJE_EMBARCACION + ", " +
                        "v." + VIAJE_DESTINO + ", v." + VIAJE_FECHA + ", v." + VIAJE_META + ", v." + VIAJE_ESTADO + ", " +
                        "COUNT(DISTINCT f." + FAENA_CEDULA + ") as trab_count, " +
                        "COUNT(DISTINCT p." + PF_ID + ") as faena_count, " +
                        "COUNT(DISTINCT a." + ASIS_ID + ") as asis_count " +
                        "FROM " + TABLE_VIAJES + " v " +
                        "LEFT JOIN " + TABLE_FAENA + " f ON v." + VIAJE_ID + " = f." + FAENA_VIAJE_ID + " " +
                        "LEFT JOIN " + TABLE_PLAN_FAENA + " p ON v." + VIAJE_ID + " = p." + PF_VIAJE_ID + " " +
                        "LEFT JOIN " + TABLE_ASISTENCIA + " a ON p." + PF_ID + " = a." + ASIS_PLAN_ID + " " +
                        "WHERE v." + VIAJE_ESTADO + " = ? " +
                        "GROUP BY v." + VIAJE_ID + " " +
                        "ORDER BY v." + VIAJE_ID + " DESC",
                new String[]{estado});
        if (cursor.moveToFirst()) {
            do {
                lista.add(new ViajeReporte(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getFloat(5),
                        cursor.getString(6),
                        cursor.getInt(7),
                        cursor.getInt(8),
                        cursor.getInt(9),
                        ""
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
    public long guardarReporteEnBD(ViajeReporte reporte) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REP_VIAJE_ID, reporte.getId());
        values.put(REP_CODIGO, reporte.getCodigo());
        values.put(REP_EMBARCACION, reporte.getEmbarcacion());
        values.put(REP_DESTINO, reporte.getDestino());
        values.put(REP_FECHA_SALIDA, reporte.getFechaSalida());
        values.put(REP_META_KG, reporte.getMetaKg());
        values.put(REP_ESTADO, reporte.getEstado());
        values.put(REP_TRAB, reporte.getTotalTrabajadores());
        values.put(REP_FAENAS, reporte.getTotalFaenas());
        values.put(REP_ASIS, reporte.getRegistrosAsistencia());
        values.put(REP_FECHA_GEN, reporte.getFechaGeneracion());
        return db.insertWithOnConflict(TABLE_REPORTES, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<ViajeReporte> obtenerReportesGuardados() {
        List<ViajeReporte> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + REP_ID + ", " + REP_VIAJE_ID + ", " + REP_CODIGO + ", " +
                        REP_EMBARCACION + ", " + REP_DESTINO + ", " + REP_FECHA_SALIDA + ", " +
                        REP_META_KG + ", " + REP_ESTADO + ", " + REP_TRAB + ", " +
                        REP_FAENAS + ", " + REP_ASIS + ", " + REP_FECHA_GEN +
                        " FROM " + TABLE_REPORTES +
                        " ORDER BY " + REP_ID + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                lista.add(new ViajeReporte(
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getFloat(6),
                        cursor.getString(7),
                        cursor.getInt(8),
                        cursor.getInt(9),
                        cursor.getInt(10),
                        cursor.getString(11)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public List<ViajeReporte> buscarReportesGuardados(String filtro) {
        List<ViajeReporte> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String like = "%" + filtro + "%";
        Cursor cursor = db.rawQuery(
                "SELECT " + REP_ID + ", " + REP_VIAJE_ID + ", " + REP_CODIGO + ", " +
                        REP_EMBARCACION + ", " + REP_DESTINO + ", " + REP_FECHA_SALIDA + ", " +
                        REP_META_KG + ", " + REP_ESTADO + ", " + REP_TRAB + ", " +
                        REP_FAENAS + ", " + REP_ASIS + ", " + REP_FECHA_GEN +
                        " FROM " + TABLE_REPORTES +
                        " WHERE " + REP_CODIGO + " LIKE ? OR " + REP_EMBARCACION + " LIKE ? OR " +
                        REP_DESTINO + " LIKE ? " +
                        " ORDER BY " + REP_ID + " DESC",
                new String[]{like, like, like});
        if (cursor.moveToFirst()) {
            do {
                lista.add(new ViajeReporte(
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getFloat(6),
                        cursor.getString(7),
                        cursor.getInt(8),
                        cursor.getInt(9),
                        cursor.getInt(10),
                        cursor.getString(11)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public boolean eliminarReporteDeBD(long viajeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_REPORTES,
                REP_VIAJE_ID + " = ?", new String[]{String.valueOf(viajeId)}) > 0;
    }
}
