package com.example.tareafishgold;

import com.example.tareafishgold.contract.IAsistenciaRepository;
import com.example.tareafishgold.contract.IFaenaPlanificacionRepository;
import com.example.tareafishgold.model.PlanificacionFaena;
import com.example.tareafishgold.model.RegistroAsistencia;
import com.example.tareafishgold.model.ViajeResumen;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Pruebas de integración M4 + M5 con repositorios simulados (mocks en memoria).
 */
public class ModulosIntegracionTest {

    private FakeFaenaRepository planRepo;
    private FakeAsistenciaRepository asistenciaRepo;

    @Before
    public void setUp() {
        planRepo = new FakeFaenaRepository();
        asistenciaRepo = new FakeAsistenciaRepository(planRepo);
    }

    @Test
    public void usuarioInicial_tieneCredencialesQuemadas() {
        assertEquals("0956856306", UsuarioInicial.CEDULA);
        assertEquals(UsuarioInicial.CEDULA, UsuarioInicial.PASSWORD);
        assertNotNull(UsuarioInicial.ROL_DESCRIPCION);
    }

    @Test
    public void m4_creaPlanificacion_y_m5_registraAsistencia() {
        long viajeId = 1L;
        planRepo.viajes.add(new ViajeResumen(viajeId, "V-TEST", "Barco A", "Galápagos", "Pendiente"));
        planRepo.tripulacion.put(viajeId, Collections.singletonList("1234567890"));

        long planId = planRepo.crearPlanificacion(
                viajeId, "Faena prueba", "2026-05-01", "2026-05-02",
                "Mañana", "Programada", "Test integración");

        assertTrue(planId > 0);

        List<String> trip = planRepo.obtenerTripulacionPorPlanificacion(planId);
        assertEquals(1, trip.size());

        boolean registrado = asistenciaRepo.registrarAsistencia(
                planId, "1234567890", "2026-05-01", "Presente", 8f);
        assertTrue(registrado);

        List<RegistroAsistencia> lista = asistenciaRepo.listarAsistencia(planId);
        assertEquals(1, lista.size());
        assertEquals("Presente", lista.get(0).getEstado());
    }

    @Test
    public void m4_actualizaPlanificacion_existente() {
        planRepo.viajes.add(new ViajeResumen(2L, "V-002", "Barco B", "Manta", "En Curso"));
        long id = planRepo.crearPlanificacion(2L, "Faena A", "2026-05-10", "2026-05-11",
                "Tarde", "Programada", "");

        boolean ok = planRepo.actualizarPlanificacion(id, "Faena A actualizada",
                "2026-05-10", "2026-05-12", "Noche", "En ejecución", "Cambio turno");
        assertTrue(ok);

        PlanificacionFaena plan = planRepo.obtenerPlanificacion(id);
        assertEquals("Faena A actualizada", plan.getNombreFaena());
        assertEquals("En ejecución", plan.getEstado());
    }

    @Test
    public void m5_sobrescribeAsistencia_mismoDiaYTrabajador() {
        planRepo.viajes.add(new ViajeResumen(3L, "V-003", "Barco C", "Esmeraldas", "Pendiente"));
        planRepo.tripulacion.put(3L, Collections.singletonList("9999999999"));
        long planId = planRepo.crearPlanificacion(3L, "Faena B", "2026-05-15", "2026-05-16",
                "Mañana", "Programada", "");

        asistenciaRepo.registrarAsistencia(planId, "9999999999", "2026-05-15", "Tarde", 4f);
        asistenciaRepo.registrarAsistencia(planId, "9999999999", "2026-05-15", "Presente", 8f);

        List<RegistroAsistencia> lista = asistenciaRepo.listarAsistencia(planId);
        assertEquals(1, lista.size());
        assertEquals("Presente", lista.get(0).getEstado());
        assertEquals(8f, lista.get(0).getHoras(), 0.01f);
    }

    private static class FakeFaenaRepository implements IFaenaPlanificacionRepository {
        final List<ViajeResumen> viajes = new ArrayList<>();
        final List<PlanificacionFaena> planes = new ArrayList<>();
        final Map<Long, List<String>> tripulacion = new HashMap<>();
        private long nextId = 1;

        @Override
        public List<ViajeResumen> listarViajesActivos() {
            return new ArrayList<>(viajes);
        }

        @Override
        public List<PlanificacionFaena> buscarPlanificaciones(String filtro) {
            return new ArrayList<>(planes);
        }

        @Override
        public PlanificacionFaena obtenerPlanificacion(long id) {
            for (PlanificacionFaena p : planes) {
                if (p.getId() == id) return p;
            }
            return null;
        }

        @Override
        public long crearPlanificacion(long viajeId, String nombreFaena, String fechaInicio,
                                       String fechaFin, String turno, String estado, String observaciones) {
            ViajeResumen viaje = null;
            for (ViajeResumen v : viajes) {
                if (v.getId() == viajeId) {
                    viaje = v;
                    break;
                }
            }
            String codigo = viaje != null ? viaje.getCodigo() : "?";
            long id = nextId++;
            planes.add(new PlanificacionFaena(id, viajeId, codigo, nombreFaena,
                    fechaInicio, fechaFin, turno, estado, observaciones));
            tripulacion.put(id, new ArrayList<>(tripulacion.getOrDefault(viajeId, Collections.emptyList())));
            return id;
        }

        @Override
        public boolean actualizarPlanificacion(long id, String nombreFaena, String fechaInicio,
                                               String fechaFin, String turno, String estado, String observaciones) {
            PlanificacionFaena actual = obtenerPlanificacion(id);
            if (actual == null) return false;
            planes.remove(actual);
            planes.add(new PlanificacionFaena(id, actual.getViajeId(), actual.getCodigoViaje(),
                    nombreFaena, fechaInicio, fechaFin, turno, estado, observaciones));
            return true;
        }

        @Override
        public boolean eliminarPlanificacion(long id) {
            PlanificacionFaena p = obtenerPlanificacion(id);
            if (p == null) return false;
            planes.remove(p);
            tripulacion.remove(id);
            return true;
        }

        @Override
        public List<String> obtenerTripulacionPorPlanificacion(long planificacionId) {
            return new ArrayList<>(tripulacion.getOrDefault(planificacionId, Collections.emptyList()));
        }
    }

    private static class FakeAsistenciaRepository implements IAsistenciaRepository {
        private final FakeFaenaRepository planRepo;
        private final List<RegistroAsistencia> registros = new ArrayList<>();
        private long nextId = 1;

        FakeAsistenciaRepository(FakeFaenaRepository planRepo) {
            this.planRepo = planRepo;
        }

        @Override
        public List<RegistroAsistencia> listarAsistencia(long planificacionId) {
            List<RegistroAsistencia> out = new ArrayList<>();
            for (RegistroAsistencia r : registros) {
                if (r.getPlanificacionId() == planificacionId) {
                    out.add(r);
                }
            }
            return out;
        }

        @Override
        public boolean registrarAsistencia(long planificacionId, String trabajadorCedula,
                                           String fechaRegistro, String estado, float horas) {
            registros.removeIf(r -> r.getPlanificacionId() == planificacionId
                    && r.getTrabajadorCedula().equals(trabajadorCedula)
                    && r.getFechaRegistro().equals(fechaRegistro));
            registros.add(new RegistroAsistencia(
                    nextId++, planificacionId, trabajadorCedula,
                    "Trabajador " + trabajadorCedula, fechaRegistro, estado, horas));
            return true;
        }

        @Override
        public boolean eliminarAsistencia(long registroId) {
            return registros.removeIf(r -> r.getId() == registroId);
        }
    }
}
