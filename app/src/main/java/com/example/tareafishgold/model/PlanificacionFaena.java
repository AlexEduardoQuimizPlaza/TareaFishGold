package com.example.tareafishgold.model;

public class PlanificacionFaena {

    private final long id;
    private final long viajeId;
    private final String codigoViaje;
    private final String nombreFaena;
    private final String fechaInicio;
    private final String fechaFin;
    private final String turno;
    private final String estado;
    private final String observaciones;

    public PlanificacionFaena(long id, long viajeId, String codigoViaje, String nombreFaena,
                              String fechaInicio, String fechaFin, String turno,
                              String estado, String observaciones) {
        this.id = id;
        this.viajeId = viajeId;
        this.codigoViaje = codigoViaje;
        this.nombreFaena = nombreFaena;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.turno = turno;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public long getId() {
        return id;
    }

    public long getViajeId() {
        return viajeId;
    }

    public String getCodigoViaje() {
        return codigoViaje;
    }

    public String getNombreFaena() {
        return nombreFaena;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public String getTurno() {
        return turno;
    }

    public String getEstado() {
        return estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getEtiquetaLista() {
        return nombreFaena + " | " + codigoViaje + " [" + estado + "]";
    }
}
