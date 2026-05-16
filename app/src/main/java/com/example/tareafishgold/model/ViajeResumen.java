package com.example.tareafishgold.model;

public class ViajeResumen {

    private final long id;
    private final String codigo;
    private final String embarcacion;
    private final String destino;
    private final String estado;

    public ViajeResumen(long id, String codigo, String embarcacion, String destino, String estado) {
        this.id = id;
        this.codigo = codigo;
        this.embarcacion = embarcacion;
        this.destino = destino;
        this.estado = estado;
    }

    public long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getEmbarcacion() {
        return embarcacion;
    }

    public String getDestino() {
        return destino;
    }

    public String getEstado() {
        return estado;
    }

    public String getEtiquetaSpinner() {
        return codigo + " — " + embarcacion + " (" + estado + ")";
    }
}
