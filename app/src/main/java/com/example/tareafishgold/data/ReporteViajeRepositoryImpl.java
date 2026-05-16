package com.example.tareafishgold.data;

import com.example.tareafishgold.BaseDatosSQLite;
import com.example.tareafishgold.contract.IReporteViajeRepository;
import com.example.tareafishgold.model.ViajeReporte;

import java.util.List;

/**
 * Implementación del repositorio M6 (Reporte de Viajes).
 * Los reportes se guardan directamente en la BD, tabla reportes_guardados.
 */
public class ReporteViajeRepositoryImpl implements IReporteViajeRepository {

    private final BaseDatosSQLite db;

    public ReporteViajeRepositoryImpl(BaseDatosSQLite db) {
        this.db = db;
    }

    @Override
    public List<ViajeReporte> listarTodosLosViajes() {
        return db.listarTodosLosViajes();
    }

    @Override
    public List<ViajeReporte> buscarViajes(String filtro) {
        return db.buscarViajesConEstadisticas(filtro == null ? "" : filtro);
    }

    @Override
    public ViajeReporte obtenerViajeCompleto(long viajeId) {
        return db.obtenerViajeCompleto(viajeId);
    }

    @Override
    public List<ViajeReporte> viajePorEstado(String estado) {
        return db.listarViajePorEstado(estado);
    }

    @Override
    public long guardarReporte(ViajeReporte reporte) {
        return db.guardarReporteEnBD(reporte);
    }

    @Override
    public List<ViajeReporte> obtenerReportesGuardados() {
        return db.obtenerReportesGuardados();
    }

    @Override
    public List<ViajeReporte> buscarReportesGuardados(String filtro) {
        return db.buscarReportesGuardados(filtro == null ? "" : filtro);
    }

    @Override
    public boolean eliminarReporte(long viajeId) {
        return db.eliminarReporteDeBD(viajeId);
    }
}