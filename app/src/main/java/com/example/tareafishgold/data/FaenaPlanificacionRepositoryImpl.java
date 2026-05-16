package com.example.tareafishgold.data;

import com.example.tareafishgold.BaseDatosSQLite;
import com.example.tareafishgold.contract.IFaenaPlanificacionRepository;
import com.example.tareafishgold.model.PlanificacionFaena;
import com.example.tareafishgold.model.ViajeResumen;

import java.util.ArrayList;
import java.util.List;

public class FaenaPlanificacionRepositoryImpl implements IFaenaPlanificacionRepository {

    private final BaseDatosSQLite db;

    public FaenaPlanificacionRepositoryImpl(BaseDatosSQLite db) {
        this.db = db;
    }

    @Override
    public List<ViajeResumen> listarViajesActivos() {
        return db.listarViajesActivos();
    }

    @Override
    public List<PlanificacionFaena> buscarPlanificaciones(String filtro) {
        return db.buscarPlanificacionesFaena(filtro == null ? "" : filtro);
    }

    @Override
    public PlanificacionFaena obtenerPlanificacion(long id) {
        return db.obtenerPlanificacionFaena(id);
    }

    @Override
    public long crearPlanificacion(long viajeId, String nombreFaena, String fechaInicio,
                                   String fechaFin, String turno, String estado, String observaciones) {
        return db.insertarPlanificacionFaena(viajeId, nombreFaena, fechaInicio, fechaFin, turno, estado, observaciones);
    }

    @Override
    public boolean actualizarPlanificacion(long id, String nombreFaena, String fechaInicio,
                                           String fechaFin, String turno, String estado, String observaciones) {
        return db.actualizarPlanificacionFaena(id, nombreFaena, fechaInicio, fechaFin, turno, estado, observaciones);
    }

    @Override
    public boolean eliminarPlanificacion(long id) {
        return db.eliminarPlanificacionFaena(id);
    }

    @Override
    public List<String> obtenerTripulacionPorPlanificacion(long planificacionId) {
        long viajeId = db.obtenerViajeIdDePlanificacion(planificacionId);
        List<String> cedulas = new ArrayList<>();
        if (viajeId < 0) {
            return cedulas;
        }
        android.database.Cursor cursor = db.obtenerTripulacionViaje(String.valueOf(viajeId));
        if (cursor.moveToFirst()) {
            do {
                cedulas.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cedulas;
    }
}
