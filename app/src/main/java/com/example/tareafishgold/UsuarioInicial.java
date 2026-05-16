package com.example.tareafishgold;

/**
 * Usuario quemado para ingresar sin registro (M1 / autenticación demo).
 */
public final class UsuarioInicial {

    public static final String CEDULA = "0956856306";
    public static final String PASSWORD = "0956856306";
    public static final String NOMBRES = "Usuario";
    public static final String APELLIDOS = "Demo FishGold";
    public static final String ROL_DESCRIPCION = "Software Engineering Student";

    private UsuarioInicial() {
    }

    public static void asegurarCuentaDemo(BaseDatosSQLite db) {
        if (!db.existeSupervisor(CEDULA)) {
            db.insertarSupervisor(
                    CEDULA,
                    NOMBRES,
                    APELLIDOS,
                    22,
                    "2003-01-15",
                    "Ecuatoriana",
                    "Masculino",
                    "Soltero",
                    3.5f,
                    PASSWORD);
        }
    }
}
