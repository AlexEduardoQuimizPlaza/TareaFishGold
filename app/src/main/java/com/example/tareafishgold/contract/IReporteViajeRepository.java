package com.example.tareafishgold.contract;

import com.example.tareafishgold.model.ViajeReporte;

import java.util.List;

public interface IReporteViajeRepository {

    List<ViajeReporte> listarTodosLosViajes();

    List<ViajeReporte> buscarViajes(String filtro);

    ViajeReporte obtenerViajeCompleto(long viajeId);

    List<ViajeReporte> viajePorEstado(String estado);


    long guardarReporte(ViajeReporte reporte);
    List<ViajeReporte> obtenerReportesGuardados();

    List<ViajeReporte> buscarReportesGuardados(String filtro);

    boolean eliminarReporte(long viajeId);
}