package com.example.tareafishgold.model;

public class RegistroAsistencia {

    private final long id;
    private final long planificacionId;
    private final String trabajadorCedula;
    private final String trabajadorNombre;
    private final String fechaRegistro;
    private final String estado;
    private final float horas;

    public RegistroAsistencia(long id, long planificacionId, String trabajadorCedula,
                              String trabajadorNombre, String fechaRegistro,
                              String estado, float horas) {
        this.id = id;
        this.planificacionId = planificacionId;
        this.trabajadorCedula = trabajadorCedula;
        this.trabajadorNombre = trabajadorNombre;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
        this.horas = horas;
    }

    public long getId() {
        return id;
    }

    public long getPlanificacionId() {
        return planificacionId;
    }

    public String getTrabajadorCedula() {
        return trabajadorCedula;
    }

    public String getTrabajadorNombre() {
        return trabajadorNombre;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public String getEstado() {
        return estado;
    }

    public float getHoras() {
        return horas;
    }
}
