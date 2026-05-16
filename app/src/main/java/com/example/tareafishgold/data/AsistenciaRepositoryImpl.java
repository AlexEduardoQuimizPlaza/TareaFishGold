package com.example.tareafishgold.data;

import com.example.tareafishgold.BaseDatosSQLite;
import com.example.tareafishgold.contract.IAsistenciaRepository;
import com.example.tareafishgold.model.RegistroAsistencia;

import java.util.List;

public class AsistenciaRepositoryImpl implements IAsistenciaRepository {

    private final BaseDatosSQLite db;

    public AsistenciaRepositoryImpl(BaseDatosSQLite db) {
        this.db = db;
    }

    @Override
    public List<RegistroAsistencia> listarAsistencia(long planificacionId) {
        return db.listarAsistenciaPorPlanificacion(planificacionId);
    }

    @Override
    public boolean registrarAsistencia(long planificacionId, String trabajadorCedula,
                                       String fechaRegistro, String estado, float horas) {
        return db.registrarAsistencia(planificacionId, trabajadorCedula, fechaRegistro, estado, horas);
    }

    @Override
    public boolean eliminarAsistencia(long registroId) {
        return db.eliminarRegistroAsistencia(registroId);
    }
}
