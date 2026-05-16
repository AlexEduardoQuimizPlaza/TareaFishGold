package com.example.tareafishgold.contract;

import com.example.tareafishgold.model.RegistroAsistencia;

import java.util.List;

/**
 * Contrato público del módulo M5 (Control Asistencia).
 * Depende de {@link IFaenaPlanificacionRepository} para planificaciones y tripulación.
 */
public interface IAsistenciaRepository {

    List<RegistroAsistencia> listarAsistencia(long planificacionId);

    boolean registrarAsistencia(long planificacionId, String trabajadorCedula,
                                String fechaRegistro, String estado, float horas);

    boolean eliminarAsistencia(long registroId);
}
