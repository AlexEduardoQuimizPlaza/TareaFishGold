# FishGold — Sistema de Gestión de Faenas Pesqueras

> Versión beta · Android · SQLite local  
> Proyecto académico — Universidad de Guayaquil

---

## Descripción general

**FishGold** es una aplicación Android desarrollada para centralizar la gestión operativa de centros de acopio pesquero. Opera exclusivamente bajo la supervisora, eliminando registros físicos y hojas de cálculo para el control de mano de obra y producción.

El flujo completo del sistema se compone de **cinco módulos encadenados**:

```
[Login] → [Trabajador] → [Planificación] → [Faena] → [Liquidación]
```

Cada módulo depende de los datos generados por el anterior. Los dos módulos documentados en detalle en este archivo son **Trabajador** y **Planificación**, ya que son la base de datos que alimenta al resto del sistema.

---

## Módulo 1 — Gestión de Trabajadores
> Responsable: Ricardo Steven Castro Agudo  
> RF-1 · RF-2 · RF-3

### ¿Qué hace?

Permite a la supervisora mantener el **padrón de personal pesquero** del sistema. Es el primer paso antes de cualquier operación: sin trabajadores registrados, no es posible armar tripulaciones ni calcular pagos.

### Campos que gestiona

| Campo | Tipo | Regla |
|---|---|---|
| Cédula / DNI | Texto numérico | Obligatorio · Único · Máx. 10 dígitos · No editable tras registro |
| Nombre Completo | Texto | Obligatorio · Máx. 150 caracteres |
| Rol / Cargo | Lista | Capitán / Motorista / Pescador / Cocinero / Ayudante |
| Teléfono | Numérico | Máx. 10 dígitos |
| Dirección | Texto libre | Opcional |
| Estado | Lista | **Activo** (por defecto) / Inactivo |

La fecha y hora de registro se captura automáticamente al guardar.

### Operaciones disponibles (CRUD)

- **Guardar** — registra un nuevo trabajador. Falla si la cédula ya existe.
- **Actualizar** — edita todos los campos excepto la cédula (integridad histórica).
- **Eliminar** — baja definitiva del registro.
- **Búsqueda en tiempo real** — filtra la lista mientras se escribe por cédula, nombre o cargo.

Al seleccionar un registro de la lista, sus datos se cargan automáticamente en el formulario para edición o eliminación.

### Regla crítica: Estado Activo / Inactivo

Un trabajador marcado como **Inactivo** desaparece de todas las listas de selección de tripulación en los módulos siguientes. Esto evita asignar personal de baja a nuevos viajes sin eliminarlo físicamente de la base de datos.

---

## Módulo 2 — Planificación de Faenas
> Responsable: Miguel Eduardo Nuñez Fuentes  
> RF-4 · RF-5 · RF-6

### ¿Qué hace?

Permite a la supervisora configurar **viajes de pesca** antes del zarpe: asignar embarcación, destino, fecha de salida, meta de captura en kilogramos y armar la tripulación desde el padrón de trabajadores activos.

### Campos que gestiona

| Campo | Tipo | Regla |
|---|---|---|
| Código de Viaje | Alfanumérico | Obligatorio · Único · No editable tras creación (ej: V-001) |
| Embarcación | Texto | Obligatorio · Máx. 100 caracteres |
| Destino / Ruta | Texto | Ruta o zona de pesca |
| Fecha de Salida | Fecha (DatePicker) | Formato YYYY-MM-DD |
| Meta de Pesca (Kg) | Decimal | Debe ser > 0 |
| Estado | Lista | **Pendiente** / En Curso / Finalizado |
| Tripulación | Multi-selección | Solo trabajadores con estado Activo |

### Operaciones disponibles

- **Guardar** — crea el viaje y vincula la tripulación en la tabla `faena_asistencia` en una sola operación.
- **Actualizar** — edita todos los campos excepto el código. Al cambiar la tripulación, se reemplaza la selección anterior completa.
- **Búsqueda en tiempo real** — filtra por código de viaje o nombre de embarcación.
- **Viaje Finalizado → Solo lectura** — si el estado es "Finalizado", el formulario se bloquea y no se puede editar. Esto protege la integridad de los datos ya liquidados.

### Validación de meta

La meta de pesca debe ser un valor decimal positivo mayor a cero. El sistema rechaza valores negativos o cero antes de guardar.

---

## Tabla de base de datos generada por estos módulos

```sql
-- Creada por Módulo Trabajador
CREATE TABLE trabajadores (
    cedula          TEXT PRIMARY KEY,
    nombre_completo TEXT NOT NULL,
    rol             TEXT,
    telefono        TEXT,
    direccion       TEXT,
    estado          TEXT DEFAULT 'Activo',
    fecha_registro  TEXT
);

-- Creada por Módulo Planificación
CREATE TABLE viajes (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo_viaje TEXT UNIQUE NOT NULL,
    embarcacion  TEXT,
    destino      TEXT,
    fecha_salida TEXT,
    meta_kg      REAL,
    estado       TEXT DEFAULT 'Pendiente'
);

-- Tabla relacional: tripulación asignada a cada viaje
CREATE TABLE faena_asistencia (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    viaje_id          INTEGER,  -- FK → viajes.id
    trabajador_cedula TEXT      -- FK → trabajadores.cedula
);
```

---

## Cómo se integran con los módulos restantes

### Con Módulo Faena (RF-7, RF-9) — Elizabeth Villamar

El módulo de Faena **consume directamente** los datos de Trabajador y Planificación:

- **RF-7 — Registrar faena**: la lista desplegable de viajes disponibles proviene de la tabla `viajes` filtrando por estado `'Pendiente'` o `'En Curso'`. La lista de trabajadores disponibles proviene de `trabajadores` filtrando por `estado = 'Activo'`. Al confirmar, se inserta un registro en `faena_asistencia` con timestamp automático.

- **RF-9 — Buscar faena**: la tabla de auditoría se construye con un `JOIN` entre `faena_asistencia`, `viajes` y `trabajadores` para mostrar código de viaje, nombre del trabajador y fecha/hora de registro. Es de solo lectura.

**Consulta SQL sugerida para RF-9:**
```sql
SELECT v.codigo_viaje, t.nombre_completo, fa.fecha_asistencia
FROM faena_asistencia fa
INNER JOIN viajes v     ON fa.viaje_id = v.id
INNER JOIN trabajadores t ON fa.trabajador_cedula = t.cedula
WHERE v.codigo_viaje LIKE ? OR t.nombre_completo LIKE ?
ORDER BY fa.fecha_asistencia DESC;
```

> **Nota para Villamar:** la tabla `faena_asistencia` ya existe y es creada por el módulo de Planificación. Se recomienda agregar la columna `fecha_asistencia TEXT` al momento de que RF-7 registre asistencia manual, o usar una tabla separada `asistencia_manual` para no pisar los datos de tripulación pre-asignada.

---

### Con Módulo Liquidación (RF-11, RF-13) — Dereck Estrada

El módulo de Liquidación **cierra el ciclo** calculando el pago en base al peso real registrado:

- **RF-11 — Buscar liquidación**: requiere un `INNER JOIN` entre la tabla `liquidaciones` (que Estrada debe crear) y la tabla `viajes` para mostrar código de viaje, peso total, monto pagado, responsable y fecha de cierre.

- **RF-13 — Configurar precio unitario**: el precio por kilogramo se almacena en una tabla `configuracion_pago` con un registro único (ID = 1, lógica upsert). Este valor se multiplica contra el peso capturado para calcular el monto final.

**Estructura sugerida para las tablas de Liquidación:**
```sql
CREATE TABLE configuracion_pago (
    id             INTEGER PRIMARY KEY,  -- siempre ID = 1
    precio_por_kg  REAL NOT NULL
);

CREATE TABLE liquidaciones (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    viaje_id       INTEGER,   -- FK → viajes.id
    peso_total_kg  REAL,
    monto_final    REAL,      -- peso_total_kg * precio_por_kg
    responsable    TEXT,      -- cédula del capitán del viaje
    fecha_cierre   TEXT
);
```

**Flujo de cálculo esperado (RF-13 → RF-11):**
```
peso_total_kg (registrado en faena)
    × precio_por_kg (tabla configuracion_pago, ID = 1)
    = monto_final  →  INSERT en liquidaciones
                   →  UPDATE viajes SET estado = 'Finalizado'
```

> **Nota para Estrada:** al guardar la liquidación, se debe cambiar el estado del viaje correspondiente a `'Finalizado'` en la tabla `viajes`. Esto activará automáticamente el bloqueo de solo lectura implementado en el módulo de Planificación (RF-5).

---

## Dependencias entre módulos (resumen)

```
Trabajador (Castro)
    └──► Planificación (Nuñez)    — usa trabajadores activos para tripulación
              └──► Faena (Villamar) — usa viajes Pendiente/En Curso y trabajadores Activos
                        └──► Liquidación (Estrada) — usa viajes y peso para calcular pago
                                  └──► cierra el viaje → bloquea edición en Planificación
```

---

## Estructura del proyecto

```
app/src/main/java/com/example/tareafishgold/
├── LoginActivity.java              — Autenticación (RF-12)
├── Register.java                   — Registro de supervisores
├── MainActivity.java               — Pantalla principal con Navigation Drawer
├── BaseDatosSQLite.java            — Helper SQLite: todas las tablas y métodos CRUD
├── TrabajadorActivity.java         — Módulo Trabajador (RF-1, RF-2, RF-3)
├── PlanificacionActivity.java      — Módulo Planificación (RF-4, RF-5, RF-6)
└── GestionSupervisorActivity.java  — CRUD de supervisoras del sistema

app/src/main/res/layout/
├── activity_login.xml
├── activity_register.xml
├── activity_main.xml
├── activity_trabajador.xml
├── activity_planificacion.xml
└── activity_gestion_supervisor.xml
```

---

## Estado de implementación

| Módulo | Responsable | Estado | RF cubiertos |
|---|---|---|---|
| Login | Sistema | ✅ Implementado | RF-12 |
| Registro Supervisoras | Sistema | ✅ Implementado | — |
| **Trabajador** | Castro | ✅ Implementado | RF-1, RF-2, RF-3 |
| **Planificación** | Nuñez | ✅ Implementado | RF-4, RF-5, RF-6 |
| Faena | Villamar | ⏳ Pendiente | RF-7, RF-9 |
| Liquidación | Estrada | ⏳ Pendiente | RF-11, RF-13 |

---

*FishGold — Proyecto académico Grupo 4 · 2026*
