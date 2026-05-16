# TareaFishGold — Puerto Seguro

Aplicación Android (Java) para gestión portuaria pesquera: supervisores, trabajadores, planificación de viajes/faenas y control de asistencia.

## Módulos del equipo

| Módulo | Responsable | Estado en app |
|--------|-------------|---------------|
| M1 — Autenticación / supervisores | Base | Login, registro, CRUD supervisores |
| M2 — Clasificación de roles | Alejandro | Placeholder (menú) |
| Trabajadores | Castro | `TrabajadorActivity` |
| Planificación viajes | Nuñez | `PlanificacionActivity` |
| **M4 — Planificación Faenas** | **Quimi** | `PlanificacionFaenaActivity` |
| **M5 — Control Asistencia** | **Villamar** | `ControlAsistenciaActivity` |

## Dependencias entre módulos

```
M1 (login) ──┬──> M4 (planificaciones de faena sobre viajes)
M3/Nuñez ────┘         │
 (viajes + tripulación)  └──> M5 (asistencia por planificación + tripulación)
```

- **M4** consume viajes activos y tripulación definida en **Planificación de Faenas (Nuñez)**.
- **M5** consume planificaciones de **M4** y la tripulación del viaje asociado.

### Contratos de integración (interfaces)

| Interfaz | Uso |
|----------|-----|
| `IFaenaPlanificacionRepository` | CRUD de planificaciones; listar viajes; tripulación por planificación |
| `IAsistenciaRepository` | Registrar y listar asistencia por planificación |

Implementaciones: `FaenaPlanificacionRepositoryImpl`, `AsistenciaRepositoryImpl` (paquete `data`).

Ejemplo para M5:

```java
BaseDatosSQLite db = new BaseDatosSQLite(context);
IFaenaPlanificacionRepository m4 = new FaenaPlanificacionRepositoryImpl(db);
IAsistenciaRepository m5 = new AsistenciaRepositoryImpl(db);

List<PlanificacionFaena> planes = m4.buscarPlanificaciones("");
long planId = planes.get(0).getId();
m5.registrarAsistencia(planId, "1234567890", "2026-05-15", "Presente", 8f);
```

## Usuario demo (sin registro)

Al abrir la app se crea automáticamente un supervisor de prueba:

| Campo | Valor |
|-------|-------|
| Usuario (cédula) | `0956856306` |
| Contraseña | `0956856306` |

Opciones de entrada:

1. Pulsar **「Entrar sin registro (usuario demo)」** en la pantalla de login.
2. Usar **Ingresar** con los datos ya precargados.

## Cómo ejecutar paso a paso

### Requisitos

- Android Studio (Ladybug o superior recomendado)
- JDK 11+
- Dispositivo/emulador con **API 26+** (Android 8.0)

### 1. Clonar y abrir el proyecto

```bash
git clone <url-del-repositorio>
cd TareaFishGold
```

En Android Studio: **File → Open** → seleccionar la carpeta `TareaFishGold`.

### 2. Sincronizar Gradle

- Espere a que termine **Sync Project with Gradle Files**.
- Si falla, use **File → Invalidate Caches / Restart**.

### 3. Ejecutar en emulador o dispositivo

1. Conecte un teléfono con depuración USB **o** cree un AVD (API 26+).
2. Seleccione el módulo **app** y el dispositivo.
3. Pulse **Run** (▶) o `Shift+F10`.

### 4. Flujo de prueba recomendado

1. **Login:** botón *Entrar sin registro* → pantalla principal con drawer.
2. **Trabajadores:** registre al menos un trabajador activo.
3. **Planificación de Faenas (Nuñez):** cree un viaje y asigne tripulación.
4. **M4 — Planificación Faenas (Quimi):** menú lateral → cree una planificación ligada al viaje.
5. **M5 — Control Asistencia (Villamar):** seleccione la planificación → registre Presente/Ausente/Tarde.

### 5. Pruebas unitarias

En Android Studio: clic derecho en `app/src/test/java/.../ModulosIntegracionTest` → **Run**.

O desde terminal (en la raíz del proyecto):

```bash
./gradlew test
```

En Windows:

```powershell
.\gradlew.bat test
```

Incluye 4 casos de integración M4+M5 con mocks en memoria.

## Estructura relevante

```
app/src/main/java/com/example/tareafishgold/
├── UsuarioInicial.java
├── PlanificacionFaenaActivity.java    # M4
├── ControlAsistenciaActivity.java     # M5
├── contract/                          # Interfaces públicas
├── data/                              # Implementaciones
├── model/                             # DTOs
└── BaseDatosSQLite.java               # SQLite v4
```

### Tablas nuevas (v4)

- `planificaciones_faena` — planificaciones M4
- `control_asistencia` — registros M5

> **Nota:** al actualizar la versión de BD, `onUpgrade` recrea las tablas. En desarrollo, desinstale la app si ve datos inconsistentes.

## Checklist de entrega M4 / M5

- [x] Compilación sin errores
- [x] Navegación desde el drawer a M4 y M5
- [x] Interfaces `IFaenaPlanificacionRepository` / `IAsistenciaRepository`
- [x] M5 depende de planificaciones M4 y tripulación de viajes
- [x] Usuario demo sin registro
- [x] Pruebas unitarias de integración (4 casos)

## Integrantes

ALEJANDRO JONATHAN · CASTRO RICARDO · NUÑEZ MIGUEL · QUIMIZ ALEX · VILLAMAR ELIZABETH
