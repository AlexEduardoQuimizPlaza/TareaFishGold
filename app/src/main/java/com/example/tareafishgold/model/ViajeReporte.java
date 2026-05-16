package com.example.tareafishgold.model;

/**
 * Modelo M6: Reporte de Viajes
 * Agrupa información de un viaje con sus estadísticas asociadas.
 */
public class ViajeReporte {

    private final long id;
    private final String codigo;
    private final String embarcacion;
    private final String destino;
    private final String fechaSalida;
    private final float metaKg;
    private final String estado;
    private final int totalTrabajadores;
    private final int totalFaenas;
    private final int registrosAsistencia;
    private final String fechaGeneracion;

    public ViajeReporte(long id, String codigo, String embarcacion, String destino,
                        String fechaSalida, float metaKg, String estado,
                        int totalTrabajadores, int totalFaenas, int registrosAsistencia,
                        String fechaGeneracion) {
        this.id = id;
        this.codigo = codigo;
        this.embarcacion = embarcacion;
        this.destino = destino;
        this.fechaSalida = fechaSalida;
        this.metaKg = metaKg;
        this.estado = estado;
        this.totalTrabajadores = totalTrabajadores;
        this.totalFaenas = totalFaenas;
        this.registrosAsistencia = registrosAsistencia;
        this.fechaGeneracion = fechaGeneracion;
    }

    public static ViajeReporte fromJSON(String json) {
        try {
            json = json.replace("{", "").replace("}", "");

            long id = extraerLong(json, "id");
            String codigo = extraerString(json, "codigo");
            String embarcacion = extraerString(json, "embarcacion");
            String destino = extraerString(json, "destino");
            String fechaSalida = extraerString(json, "fechaSalida");
            float metaKg = extraerFloat(json, "metaKg");
            String estado = extraerString(json, "estado");
            int totalTrabajadores = extraerInt(json, "totalTrabajadores");
            int totalFaenas = extraerInt(json, "totalFaenas");
            int registrosAsistencia = extraerInt(json, "registrosAsistencia");
            String fechaGeneracion = extraerString(json, "fechaGeneracion");

            return new ViajeReporte(id, codigo, embarcacion, destino, fechaSalida,
                    metaKg, estado, totalTrabajadores, totalFaenas,
                    registrosAsistencia, fechaGeneracion);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extraerString(String json, String key) {
        String pattern = "\"" + key + "\":\"([^\"]*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    private static long extraerLong(String json, String key) {
        String pattern = "\"" + key + "\":(\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? Long.parseLong(m.group(1)) : 0;
    }

    private static int extraerInt(String json, String key) {
        return (int) extraerLong(json, key);
    }

    private static float extraerFloat(String json, String key) {
        String pattern = "\"" + key + "\":(\\d+\\.?\\d*)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? Float.parseFloat(m.group(1)) : 0f;
    }

    public long getId()                    { return id; }
    public String getCodigo()              { return codigo; }
    public String getEmbarcacion()         { return embarcacion; }
    public String getDestino()             { return destino; }
    public String getFechaSalida()         { return fechaSalida; }
    public float getMetaKg()               { return metaKg; }
    public String getEstado()              { return estado; }
    public int getTotalTrabajadores()      { return totalTrabajadores; }
    public int getTotalFaenas()            { return totalFaenas; }
    public int getRegistrosAsistencia()    { return registrosAsistencia; }
    public String getFechaGeneracion()     { return fechaGeneracion; }


    public String getEtiquetaLista() {
        return codigo + " - " + embarcacion + " → " + destino +
                " [" + estado + "] • " + totalTrabajadores + " trab. • " + totalFaenas + " faenas";
    }

    /**
     * Detalle completo formateado con saltos de línea.
     */
    public String getDetalleCompleto() {
        return "Código: " + codigo + "\n" +
                "Embarcación: " + embarcacion + "\n" +
                "Destino: " + destino + "\n" +
                "Fecha Salida: " + fechaSalida + "\n" +
                "Meta (kg): " + metaKg + "\n" +
                "Estado: " + estado + "\n" +
                "Trabajadores: " + totalTrabajadores + "\n" +
                "Faenas: " + totalFaenas + "\n" +
                "Registros de Asistencia: " + registrosAsistencia + "\n" +
                "Generado: " + fechaGeneracion;
    }

    /**
     * Serializa el reporte a JSON para guardar en SharedPreferences.
     */
    public String toJSON() {
        return "{" +
                "\"id\":" + id + "," +
                "\"codigo\":\"" + codigo + "\"," +
                "\"embarcacion\":\"" + embarcacion + "\"," +
                "\"destino\":\"" + destino + "\"," +
                "\"fechaSalida\":\"" + fechaSalida + "\"," +
                "\"metaKg\":" + metaKg + "," +
                "\"estado\":\"" + estado + "\"," +
                "\"totalTrabajadores\":" + totalTrabajadores + "," +
                "\"totalFaenas\":" + totalFaenas + "," +
                "\"registrosAsistencia\":" + registrosAsistencia + "," +
                "\"fechaGeneracion\":\"" + fechaGeneracion + "\"" +
                "}";
    }
}