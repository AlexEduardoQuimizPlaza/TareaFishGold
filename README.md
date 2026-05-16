# TareaFishGold — Puerto Seguro

Aplicación Android (Java) para gestión portuaria pesquera de la empresa **Puerto Seguro**. Cubre el ciclo completo: autenticación de supervisores, registro de trabajadores, creación de viajes con asignación de tripulación, planificación de faenas, control de asistencia y liquidación de pagos por captura.

---

## Flujo completo del programa

El sistema sigue un ciclo de trabajo lineal donde cada módulo depende del anterior:

```
LOGIN
  │
  ▼
[M1] Supervisores ──── Gestión interna del sistema
  │
  ▼
[M2] Trabajadores ──── Registrar tripulantes (Capitán, Motorista, Pescador...)
  │
  ▼
[M3] Viajes + Tripulación ──── Crear el viaje y asignar quiénes van a bordo
  │
  ▼
[M4] Planificación de Faenas ──── Definir las jornadas de pesca del viaje
  │
  ├──► [M5] Control de Asistencia ──── Registrar quién asistió a cada faena
  │
  └──► [M6] Liquidación y Pagos ──── Calcular el pago según kg capturados
                                          │
                                          ▼
                                   [M7] Reporte de Viaje ──── Consultar y guardar resumen final
```

### Paso a paso detallado

**1. Login**
El supervisor ingresa con su cédula y contraseña. Existe un usuario demo precargado para pruebas. Al entrar se abre el menú lateral con acceso a todos los módulos.

**2. Trabajadores (Castro)**
Antes de crear cualquier viaje se deben registrar los trabajadores de la empresa. Cada uno tiene: cédula, nombre, rol, teléfono, dirección y estado (Activo/Inactivo). Solo los trabajadores **Activos** están disponibles para asignar como tripulación.

**3. Viajes + Tripulación (Nuñez)**
Se crea el viaje registrando: código único (ej. V-001), embarcación, destino/ruta, fecha de salida, meta de pesca en kg y estado (Pendiente/En Curso/Finalizado). Desde este mismo módulo se pulsa **"Asignar Tripulación"** para seleccionar qué trabajadores activos van en ese viaje. Sin este paso, los módulos M4, M5 y M6 no tienen datos con qué trabajar.

**4. Planificación de Faenas (Quimiz)**
Una vez que existe el viaje, se crean planificaciones de faena: jornadas específicas de pesca con nombre, fechas de inicio y fin, turno (Mañana/Tarde/Noche) y estado (Programada/En ejecución/Completada/Cancelada). Cada planificación queda vinculada a un viaje. Un viaje puede tener múltiples planificaciones.

**5. Control de Asistencia (Villamar)**
Se selecciona una planificación creada en M4, luego el trabajador de la tripulación y se registra su asistencia para una fecha dada: Presente, Ausente, Tarde o Justificado, junto con las horas trabajadas.

**6. Liquidación y Pagos (Nuñez)**
Se selecciona una planificación y se ingresa el peso real capturado (kg) y el precio por kg. El sistema calcula automáticamente el monto total. Se registra el capitán responsable y observaciones. Cada registro queda en el historial de liquidaciones.

**7. Reporte de Viaje (Alejandro)**
Vista consolidada de todos los viajes con estadísticas: cuántos trabajadores tuvo, cuántas faenas, cuántos registros de asistencia. Permite filtrar por estado y guardar un snapshot del reporte para consulta offline.

---

## Módulos del equipo

| # | Módulo | Responsable | Activity | Descripción |
|---|--------|-------------|----------|-------------|
| M1 | Autenticación y Supervisores | Base | `LoginActivity`, `Register`, `GestionSupervisorActivity` | Login, registro y CRUD de supervisores |
| M2 | Gestión de Trabajadores | Castro | `TrabajadorActivity` | CRUD de tripulantes con búsqueda en tiempo real |
| M3 | Gestión de Viajes y Tripulación | Nuñez | `ViajeActivity` | CRUD de viajes + asignación de tripulación por viaje |
| M4 | Planificación de Faenas | Quimiz | `PlanificacionFaenaActivity` | Planificaciones de faena vinculadas a un viaje |
| M5 | Control de Asistencia | Villamar | `ControlAsistenciaActivity` | Asistencia por faena y trabajador |
| M6 | Liquidación y Pagos | Nuñez | `LiquidacionPagoActivity` | Pago por kg capturado, cálculo automático de monto |
| M7 | Reporte de Viaje | Alejandro | `ReporteViajeActivity` | Estadísticas por viaje y reportes guardados |

---

## Descripción de cada módulo

### M1 — Autenticación y Supervisores
- Pantalla de login con cédula y contraseña (contraseña inicial = cédula).
- Registro de nuevos supervisores con datos completos.
- Usuario demo precargado (ver sección abajo).
- CRUD de supervisores: buscar por cédula, actualizar datos y eliminar.

### M2 — Gestión de Trabajadores (Castro)
- Alta, edición y baja de trabajadores.
- Campos: cédula (único, inmutable), nombre completo, rol (Capitán / Motorista / Pescador / Cocinero / Ayudante), teléfono, dirección, estado (Activo/Inactivo).
- Búsqueda en tiempo real por cédula, nombre o rol.
- Solo trabajadores **Activos** aparecen en el selector de tripulación de M3.

### M3 — Gestión de Viajes y Tripulación (Nuñez)
- CRUD completo de viajes: código único, embarcación, destino/ruta, fecha de salida, meta de pesca (kg), estado.
- **Diferenciador clave:** botón **"Asignar Tripulación"** — abre un diálogo con checklist de trabajadores activos para seleccionar quiénes participan en el viaje. El resumen de la tripulación se muestra en pantalla.
- Al guardar o actualizar el viaje, la tripulación queda registrada en la base de datos.
- Al eliminar un viaje, elimina en cascada sus planificaciones de faena y registros de asistencia.
- Búsqueda en tiempo real por código de viaje o nombre de embarcación.

### M4 — Planificación de Faenas (Quimiz)
- Crea planificaciones de faena vinculadas a un viaje activo (requiere que existan viajes creados en M3).
- Campos: nombre de faena, viaje asociado, fecha inicio/fin (DatePicker), turno (Mañana/Tarde/Noche), estado (Programada/En ejecución/Completada/Cancelada), observaciones.
- Búsqueda en tiempo real por nombre de faena o código de viaje.
- Las planificaciones creadas aquí son el punto de partida para M5 y M6.

### M5 — Control de Asistencia (Villamar)
- Selecciona una planificación de faena (M4) — muestra el viaje y turno asociado.
- Selecciona el trabajador de la tripulación asignada al viaje.
- Registra: fecha (DatePicker, por defecto hoy), estado de asistencia, horas trabajadas.
- Lista los registros de la faena seleccionada; pulsación larga para eliminar un registro.

### M6 — Liquidación y Pagos (Nuñez)
- Selecciona una planificación (M4) desde un spinner.
- Ingresa el **peso real capturado (kg)** y el **precio por kg**.
- Calcula automáticamente el **monto total** en tiempo real.
- Registra capitán responsable y observaciones.
- Historial de liquidaciones con opción de eliminar.

### M7 — Reporte de Viaje (Alejandro)
- Vista consolidada de todos los viajes con estadísticas agregadas: total de trabajadores en tripulación, total de faenas planificadas, total de registros de asistencia.
- Filtro por estado (Todos / Pendiente / En Curso / Finalizado) y búsqueda en tiempo real.
- Pestaña de reportes guardados: permite guardar un snapshot del estado actual de un viaje para consulta posterior sin conexión.
- Vista de detalle del viaje seleccionado.

---

## Dependencias entre módulos

```
M1 (login / supervisores)
    │
    ├──► M2 (trabajadores)
    │         │
    │         │ lista de activos para tripulación
    │         ▼
    │     M3 (viajes + tripulación) ◄─────── base del sistema
    │         │
    │         │ viaje_id
    │         ▼
    │     M4 (planificaciones de faena)
    │         │
    │         ├──► M5 (control de asistencia)
    │         │
    │         └──► M6 (liquidación y pagos)
    │
    └──► M7 (reporte de viaje) ◄───── lee viajes + faenas + asistencia
```

- **M3** depende de **M2**: solo muestra trabajadores activos en el selector de tripulación.
- **M4** depende de **M3**: el spinner de viajes carga los viajes Pendiente o En Curso.
- **M5** depende de **M4**: el spinner de planificaciones carga las faenas activas.
- **M6** depende de **M4**: el spinner de planificaciones permite elegir la faena a liquidar.
- **M7** lee transversalmente: viajes, faena_asistencia, planificaciones_faena y control_asistencia.

---

## Base de datos (SQLite — versión 7)

| Tabla | Módulo | Descripción |
|-------|--------|-------------|
| `supervisores` | M1 | Cuentas de acceso al sistema |
| `trabajadores` | M2 | Tripulantes: cédula, nombre, rol, estado |
| `viajes` | M3 | Viajes: código, embarcación, destino, meta, estado |
| `faena_asistencia` | M3 | Tripulación asignada por viaje (viaje_id → trabajador_cedula) |
| `planificaciones_faena` | M4 | Faenas por viaje: turno, fechas, estado |
| `control_asistencia` | M5 | Asistencia por planificación, trabajador y fecha |
| `liquidaciones` | M6 | Pagos: peso kg, precio/kg, monto total, capitán |
| `reportes_guardados` | M7 | Snapshots de reportes de viaje |

> **Nota de desarrollo:** al cambiar la versión de la BD, `onUpgrade` recrea todas las tablas. Si hay datos inconsistentes, desinstale la app del emulador para forzar `onCreate`.

---

## Usuario demo

Al iniciar la app se crea automáticamente:

| Campo | Valor |
|-------|-------|
| Cédula | `0956856306` |
| Contraseña | `0956856306` |

Formas de entrar:
1. Pulsar **"Entrar sin registro (usuario demo)"** en la pantalla de login.
2. Escribir los datos manualmente y pulsar **"Ingresar"**.

---

## Flujo de prueba recomendado

1. **Login** → botón *Entrar sin registro* → abre el drawer lateral.
2. **Trabajadores (Castro):** registre al menos 3 trabajadores activos con diferentes roles.
3. **Viajes (Nuñez):** cree un viaje (ej. código `V-001`, embarcación `Don Pepe`, destino `Golfo de Guayaquil`) → pulse *Asignar Tripulación* → seleccione los trabajadores → guarde.
4. **Planificación Faenas (Quimiz):** seleccione el viaje creado → cree una faena (ej. `Faena Nocturna`, turno `Noche`, estado `Programada`).
5. **Control Asistencia (Villamar):** seleccione la planificación → registre la asistencia de cada tripulante.
6. **Liquidación y Pagos (Nuñez):** seleccione la planificación → ingrese peso capturado y precio/kg → pulse *Registrar Liquidación*.
7. **Reporte de Viaje (Alejandro):** busque el viaje → verifique las estadísticas → pulse *Guardar Reporte*.

---

## Estructura del proyecto

```
app/src/main/java/com/example/tareafishgold/
├── LoginActivity.java
├── Register.java
├── MainActivity.java
├── GestionSupervisorActivity.java          # M1 — supervisores
├── TrabajadorActivity.java                 # M2 — trabajadores
├── ViajeActivity.java                      # M3 — viajes + tripulación
├── PlanificacionFaenaActivity.java         # M4 — planificación de faenas
├── ControlAsistenciaActivity.java          # M5 — control de asistencia
├── LiquidacionPagoActivity.java            # M6 — liquidación y pagos
├── ReporteViajeActivity.java               # M7 — reportes
├── BaseDatosSQLite.java                    # SQLite v7 — todas las tablas y CRUD
├── UsuarioInicial.java                     # Inyecta usuario demo al iniciar
├── contract/
│   ├── IFaenaPlanificacionRepository.java
│   ├── IAsistenciaRepository.java
│   └── IReporteViajeRepository.java
├── data/
│   ├── FaenaPlanificacionRepositoryImpl.java
│   ├── AsistenciaRepositoryImpl.java
│   └── ReporteViajeRepositoryImpl.java
└── model/
    ├── PlanificacionFaena.java
    ├── RegistroAsistencia.java
    ├── Liquidacion.java
    ├── ViajeResumen.java
    └── ViajeReporte.java
```

---

## Requisitos técnicos

- Android Studio Ladybug o superior
- JDK 11+
- Dispositivo o emulador con **API 26+** (Android 8.0 Oreo)

```bash
git clone <url-del-repositorio>
# Abrir carpeta TareaFishGold en Android Studio → esperar sync de Gradle → Run ▶
```

---

## Integrantes

| Nombre | Módulo |
|--------|--------|
| Alejandro Jonathan | M7 — Reporte de Viaje |
| Castro Ricardo | M2 — Gestión de Trabajadores |
| Nuñez Miguel | M3 — Viajes y Tripulación / M6 — Liquidación y Pagos |
| Quimiz Alex | M4 — Planificación de Faenas |
| Villamar Elizabeth | M5 — Control de Asistencia |
