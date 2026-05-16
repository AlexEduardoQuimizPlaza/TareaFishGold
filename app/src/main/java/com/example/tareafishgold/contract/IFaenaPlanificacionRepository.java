package com.example.tareafishgold.contract;

import com.example.tareafishgold.model.PlanificacionFaena;
import com.example.tareafishgold.model.ViajeResumen;

import java.util.List;

/**
 * Contrato público del módulo M4 (Planificación Faenas).
 * Consumido por M5 y documentado para integración del equipo.
 */
public interface IFaenaPlanificacionRepository {

    List<ViajeResumen> listarViajesActivos();

    List<PlanificacionFaena> buscarPlanificaciones(String filtro);

    PlanificacionFaena obtenerPlanificacion(long id);

    long crearPlanificacion(long viajeId, String nombreFaena, String fechaInicio,
                            String fechaFin, String turno, String estado, String observaciones);

    boolean actualizarPlanificacion(long id, String nombreFaena, String fechaInicio,
                                    String fechaFin, String turno, String estado, String observaciones);

    boolean eliminarPlanificacion(long id);

    List<String> obtenerTripulacionPorPlanificacion(long planificacionId);
}
