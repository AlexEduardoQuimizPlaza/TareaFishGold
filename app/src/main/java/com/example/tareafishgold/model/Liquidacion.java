package com.example.tareafishgold.model;

import java.util.Locale;

public class Liquidacion {

    private final long id;
    private final long planificacionId;
    private final String nombrePlanificacion;
    private final float pesoCapturaKg;
    private final float precioPorKg;
    private final float montoTotal;
    private final String capitanResponsable;
    private final String observaciones;
    private final String fechaRegistro;

    public Liquidacion(long id, long planificacionId, String nombrePlanificacion,
                       float pesoCapturaKg, float precioPorKg, float montoTotal,
                       String capitanResponsable, String observaciones, String fechaRegistro) {
        this.id = id;
        this.planificacionId = planificacionId;
        this.nombrePlanificacion = nombrePlanificacion;
        this.pesoCapturaKg = pesoCapturaKg;
        this.precioPorKg = precioPorKg;
        this.montoTotal = montoTotal;
        this.capitanResponsable = capitanResponsable;
        this.observaciones = observaciones;
        this.fechaRegistro = fechaRegistro;
    }

    public long getId() { return id; }
    public long getPlanificacionId() { return planificacionId; }
    public String getNombrePlanificacion() { return nombrePlanificacion; }
    public float getPesoCapturaKg() { return pesoCapturaKg; }
    public float getPrecioPorKg() { return precioPorKg; }
    public float getMontoTotal() { return montoTotal; }
    public String getCapitanResponsable() { return capitanResponsable; }
    public String getObservaciones() { return observaciones; }
    public String getFechaRegistro() { return fechaRegistro; }

    public String getEtiquetaLista() {
        String capit = (capitanResponsable != null && !capitanResponsable.isEmpty())
                ? capitanResponsable : "—";
        return String.format(Locale.getDefault(),
                "%s\n%.1f kg  |  $%.2f  |  %s  |  %s",
                nombrePlanificacion, pesoCapturaKg, montoTotal, capit, fechaRegistro);
    }
}
