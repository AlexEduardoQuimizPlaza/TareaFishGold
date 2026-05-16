# TareaFishGold — Puerto Seguro

Aplicación Android (Java) para gestión portuaria pesquera: autenticación de supervisores, gestión de trabajadores, planificación de viajes y faenas, control de asistencia y liquidación de pagos por captura.

---

## Módulos del equipo

| # | Módulo | Responsable | Activity | Descripción |
|---|--------|-------------|----------|-------------|
| M1 | Autenticación y Supervisores | Base | `LoginActivity`, `Register`, `GestionSupervisorActivity` | Login, registro y CRUD de supervisores |
| M2 | Gestión de Trabajadores | Castro | `TrabajadorActivity` | CRUD de tripulantes: cédula, nombre, rol, teléfono, dirección, estado |
| M3 | Reporte de Viaje | Alejandro | `ReporteViajeActivity` | Consulta, filtro por estado y guardado de reportes de viajes |
| M4 | Planificación de Faenas | Quimiz | `PlanificacionFaenaActivity` | Crea planificaciones de faena asociadas a viajes; gestiona turno, fechas y estado |
| M5 | Control de Asistencia | Villamar | `ControlAsistenciaActivity` | Registra asistencia (Presente/Ausente/Tarde/Justificado) y horas por planificación |
| M6 | Liquidación y Pagos | Nuñez | `LiquidacionPagoActivity` | Ingresa peso capturado y precio/kg; calcula monto a pagar; historial de liquidaciones |

---

## Descripción de cada módulo

### M1 — Autenticación y Supervisores
- Pantalla de login con cédula y contraseña.
- Registro de nuevos supervisores.
- Usuario demo precargado (sin necesidad de registro).
- CRUD de supervisores: buscar por cédula, actualizar datos, eliminar.

### M2 — Gestión de Trabajadores (Castro)
- Alta, edición y baja de trabajadores de la embarcación.
- Campos: cédula, nombre completo, rol (Capitán/Motorista/Pescador/Cocinero/Ayudante), teléfono, dirección, estado (Activo/Inactivo).
- Búsqueda en tiempo real por cédula, nombre o rol.
- Solo trabajadores **Activos** aparecen disponibles para asignar a viajes.

### M3 — Reporte de Viaje (Alejandro)
- Lista todos los viajes con estadísticas: total trabajadores, faenas y registros de asistencia.
- Filtro por estado (Pendiente / En Curso / Finalizado) y búsqueda en tiempo real.
- Permite guardar y eliminar reportes para consulta offline.
- Vista de detalle con datos completos del viaje seleccionado.

### M4 — Planificación de Faenas (Quimiz)
- Crea planificaciones de faena vinculadas a un viaje activo.
- Campos: nombre de faena, viaje asociado, fecha inicio/fin, turno (Mañana/Tarde/Noche), estado (Programada/En ejecución/Completada/Cancelada), observaciones.
- Búsqueda en tiempo real por nombre de faena o código de viaje.
- Las planificaciones creadas aquí son consumidas por M5 y M6.

### M5 — Control de Asistencia (Villamar)
- Selecciona una planificación de faena (M4) y registra asistencia de cada trabajador.
- Estados disponibles: Presente, Ausente, Tarde, Justificado.
- Registra fecha y horas trabajadas por jornada.
- Lista los registros del día/faena seleccionada con opción de eliminar.

### M6 — Liquidación y Pagos (Nuñez)
- Selecciona una planificación creada en M4 desde un combobox.
- Ingresa el **precio por kg** y el **peso real capturado (kg)**.
- Calcula automáticamente el **monto a pagar** en tiempo real (peso × precio/kg).
- Registra capitán responsable y observaciones.
- Historial de liquidaciones con opción de eliminar registros.

---

## Dependencias entre módulos

```
M1 (login/supervisores)
    │
    ├──> M2 (trabajadores) ──────────────────────┐
    │                                             │
    ├──> M3 (reportes de viaje)                  │ tripulación
    │         ↑                                  │
    │    viajes (BD)                             ↓
    │         │                     M4 (planificaciones de faena)
    │         └──────────────────────────┬───────┘
    │                                    │
    │                                    ├──> M5 (asistencia)
    │                                    │
    │                                    └──> M6 (liquidación/pagos)
```

- **M4** depende de los viajes registrados en la BD y la tripulación asignada en M2.
- **M5** depende de las planificaciones de **M4** y la tripulación del viaje.
- **M6** depende de las planificaciones de **M4** para el combobox de selección.

---

## Base de datos (SQLite v7)

| Tabla | Módulo | Descripción |
|-------|--------|-------------|
| `supervisores` | M1 | Cuentas de acceso al sistema |
| `trabajadores` | M2 | Tripulantes registrados |
| `viajes` | M3/Base | Viajes con embarcación, destino, meta y estado |
| `faena_asistencia` | Base | Tripulación asignada por viaje |
| `planificaciones_faena` | M4 | Planificaciones de faena por viaje |
| `control_asistencia` | M5 | Registros de asistencia por planificación |
| `liquidaciones` | M6 | Pagos por captura: peso, precio/kg, monto total |
| `reportes_guardados` | M3 | Snapshots de reportes de viaje guardados |

> Al actualizar la versión de BD, `onUpgrade` recrea todas las tablas. En desarrollo, desinstale la app si ve datos inconsistentes.

---

## Usuario demo (sin registro)

Al abrir la app se crea automáticamente un supervisor de prueba:

| Campo | Valor |
|-------|-------|
| Usuario (cédula) | `0956856306` |
| Contraseña | `0956856306` |

Opciones de entrada:
1. Pulsar **「Entrar sin registro (usuario demo)」** en la pantalla de login.
2. Usar **Ingresar** con los datos ya precargados.

---

## Cómo ejecutar

### Requisitos

- Android Studio (Ladybug o superior)
- JDK 11+
- Dispositivo/emulador con **API 26+** (Android 8.0)

### Pasos

```bash
git clone <url-del-repositorio>
cd TareaFishGold
```

En Android Studio: **File → Open** → seleccionar la carpeta `TareaFishGold` → esperar sync de Gradle → **Run** (▶).

### Flujo de prueba recomendado

1. **Login** → botón *Entrar sin registro* → pantalla principal con drawer.
2. **M2 — Trabajadores (Castro):** registre al menos un trabajador activo.
3. **M4 — Planificación Faenas (Quimiz):** cree un viaje (desde la BD) y luego una planificación ligada a él.
4. **M5 — Control Asistencia (Villamar):** seleccione la planificación → registre Presente/Ausente.
5. **M6 — Liquidación y Pagos (Nuñez):** seleccione la planificación → ingrese peso y precio/kg → pulse *Finalizar y pagar*.
6. **M3 — Reporte de Viaje (Alejandro):** consulte el resumen del viaje y guarde el reporte.

---

## Estructura del proyecto

```
app/src/main/java/com/example/tareafishgold/
├── LoginActivity.java
├── Register.java
├── MainActivity.java
├── GestionSupervisorActivity.java      # M1
├── TrabajadorActivity.java             # M2
├── ReporteViajeActivity.java           # M3
├── PlanificacionFaenaActivity.java     # M4
├── ControlAsistenciaActivity.java      # M5
├── LiquidacionPagoActivity.java        # M6
├── BaseDatosSQLite.java                # SQLite v7 — todas las tablas
├── UsuarioInicial.java
├── contract/
│   ├── IFaenaPlanificacionRepository.java
│   └── IAsistenciaRepository.java
├── data/
│   ├── FaenaPlanificacionRepositoryImpl.java
│   └── AsistenciaRepositoryImpl.java
└── model/
    ├── PlanificacionFaena.java
    ├── RegistroAsistencia.java
    ├── Liquidacion.java
    ├── ViajeResumen.java
    └── ViajeReporte.java
```

---

## Integrantes

| Nombre | Módulo |
|--------|--------|
| Alejandro Jonathan | M3 — Reporte de Viaje |
| Castro Ricardo | M2 — Gestión de Trabajadores |
| Nuñez Miguel | M6 — Liquidación y Pagos |
| Quimiz Alex | M4 — Planificación de Faenas |
| Villamar Elizabeth | M5 — Control de Asistencia |
