package co.edu.univalle.demo.service;

import co.edu.univalle.demo.exception.BusinessException;
import co.edu.univalle.demo.exception.ResourceNotFoundException;
import co.edu.univalle.demo.exception.ValidacionException;
import co.edu.univalle.demo.model.TareaLogisticaModel;
import co.edu.univalle.demo.model.UsuarioModel;
import co.edu.univalle.demo.repository.TareaLogisticaRepository;
import co.edu.univalle.demo.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TareaLogisticaRepository tareaLogisticaRepository;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, tareaLogisticaRepository);
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private UsuarioModel buildUsuario() {
        return UsuarioModel.builder()
                .id(1L)
                .nombre("Ana María")
                .email("ana.gomez@univalle.edu.co")
                .passwordHash("hashedpassword123")
                .limiteHorasDiarias(new BigDecimal("8.00"))
                .build();
    }

    // ── Crear ─────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Crear usuario exitosamente cuando el email es único")
    void whenCreateUsuario_withUniqueEmail_thenReturnSavedUsuario() {
        var usuario = buildUsuario();

        given(usuarioRepository.findByEmail(usuario.getEmail()))
                .willReturn(Optional.empty());
        given(usuarioRepository.save(usuario)).willReturn(usuario);

        UsuarioModel result = usuarioService.crear(usuario);

        verify(usuarioRepository).save(usuario);
        assertEquals(usuario, result);
    }

    @Test
    @DisplayName("Lanzar BusinessException al crear usuario con email duplicado")
    void whenCreateUsuario_withDuplicateEmail_thenThrowBusinessException() {
        var usuario = buildUsuario();

        given(usuarioRepository.findByEmail(usuario.getEmail()))
                .willReturn(Optional.of(usuario));

        assertThrows(BusinessException.class,
                () -> usuarioService.crear(usuario));

        verify(usuarioRepository, never()).save(usuario);
    }

    // ── Actualizar ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Actualizar usuario exitosamente cuando existe")
    void whenUpdateUsuario_withExistingId_thenReturnUpdatedUsuario() {
        var usuario = buildUsuario();

        given(usuarioRepository.findById(usuario.getId()))
                .willReturn(Optional.of(usuario));
        given(usuarioRepository.save(usuario)).willReturn(usuario);

        UsuarioModel result = usuarioService.actualizar(usuario);

        verify(usuarioRepository).save(usuario);
        assertEquals(usuario, result);
    }

    @Test
    @DisplayName("Lanzar ResourceNotFoundException al actualizar usuario inexistente")
    void whenUpdateUsuario_withNonExistingId_thenThrowResourceNotFoundException() {
        var usuario = buildUsuario();

        given(usuarioRepository.findById(usuario.getId()))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.actualizar(usuario));

        verify(usuarioRepository, never()).save(usuario);
    }

    // ── Eliminar ──────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Eliminar usuario exitosamente cuando existe")
    void whenDeleteUsuario_withExistingId_thenDeleteFromRepository() {
        var usuario = buildUsuario();

        given(usuarioRepository.findById(usuario.getId()))
                .willReturn(Optional.of(usuario));

        usuarioService.eliminar(usuario.getId());

        verify(usuarioRepository).deleteById(usuario.getId());
    }

    @Test
    @DisplayName("Lanzar ResourceNotFoundException al eliminar usuario inexistente")
    void whenDeleteUsuario_withNonExistingId_thenThrowResourceNotFoundException() {
        given(usuarioRepository.findById(99L))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.eliminar(99L));

        verify(usuarioRepository, never()).deleteById(99L);
    }

    // ── Obtener todos ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("Retornar lista de todos los usuarios")
    void whenGetAllUsuarios_thenReturnList() {
        var usuarios = List.of(buildUsuario());

        given(usuarioRepository.findAll()).willReturn(usuarios);

        List<UsuarioModel> result = usuarioService.obtenerTodos();

        verify(usuarioRepository).findAll();
        assertEquals(usuarios, result);
    }

    // ── Obtener por id ────────────────────────────────────────────────────────
    @Test
    @DisplayName("Retornar usuario por id cuando existe")
    void whenGetUsuarioById_withExistingId_thenReturnUsuario() {
        var usuario = buildUsuario();

        given(usuarioRepository.findById(usuario.getId()))
                .willReturn(Optional.of(usuario));

        UsuarioModel result = usuarioService.obtenerPorId(usuario.getId());

        assertEquals(usuario, result);
    }

    @Test
    @DisplayName("Lanzar ResourceNotFoundException al buscar id inexistente")
    void whenGetUsuarioById_withNonExistingId_thenThrowResourceNotFoundException() {
        given(usuarioRepository.findById(99L))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.obtenerPorId(99L));
    }

    // ── Buscar por nombre ─────────────────────────────────────────────────────
    @Test
    @DisplayName("Retornar usuarios que coinciden con el nombre buscado")
    void whenSearchByNombre_thenReturnMatchingUsuarios() {
        var usuarios = List.of(buildUsuario());

        given(usuarioRepository.findAllByNombreContainingIgnoreCase("Ana"))
                .willReturn(usuarios);

        List<UsuarioModel> result = usuarioService.buscarPorNombre("Ana");

        assertEquals(usuarios, result);
    }

    // ── Buscar por email ──────────────────────────────────────────────────────
    @Test
    @DisplayName("Retornar usuario cuando el email existe")
    void whenSearchByEmail_withExistingEmail_thenReturnUsuario() {
        var usuario = buildUsuario();

        given(usuarioRepository.findByEmail(usuario.getEmail()))
                .willReturn(Optional.of(usuario));

        Optional<UsuarioModel> result = usuarioService.buscarPorEmail(usuario.getEmail());

        assertTrue(result.isPresent());
        assertEquals(usuario, result.get());
    }

    @Test
    @DisplayName("Retornar Optional vacío cuando el email no existe")
    void whenSearchByEmail_withNonExistingEmail_thenReturnEmpty() {
        given(usuarioRepository.findByEmail("noexiste@univalle.edu.co"))
                .willReturn(Optional.empty());

        Optional<UsuarioModel> result = usuarioService.buscarPorEmail("noexiste@univalle.edu.co");

        assertTrue(result.isEmpty());
    }

    // ── Límite diario vs. gestiones existentes (US-12) ────────────────────────
    private TareaLogisticaModel buildTarea(final String titulo, final String horas, final String estado) {
        return TareaLogisticaModel.builder()
                .id(7L)
                .usuarioId(1L)
                .titulo(titulo)
                .horasEstimadas(new BigDecimal(horas))
                .estado(estado)
                .build();
    }

    @Test
    @DisplayName("Rechazar un límite diario menor a las horas de una gestión pendiente")
    void whenUpdateLimite_belowPendingTaskHours_thenThrowValidacionException() {
        var usuario = buildUsuario();
        given(usuarioRepository.findById(1L)).willReturn(Optional.of(usuario));
        given(tareaLogisticaRepository.findByUsuarioId(1L))
                .willReturn(List.of(buildTarea("Decoraciones", "5.00", TareaLogisticaModel.ESTADO_PENDIENTE)));

        var ex = assertThrows(ValidacionException.class,
                () -> usuarioService.actualizarLimiteDiario(1L, new BigDecimal("4")));

        assertTrue(ex.getMessage().contains("Decoraciones"));
        assertTrue(ex.getMessage().contains("5 h"));
        verify(usuarioRepository, never()).save(usuario);
        assertEquals(new BigDecimal("8.00"), usuario.getLimiteHorasDiarias());
    }

    @Test
    @DisplayName("Permitir un límite diario igual a las horas de la gestión pendiente más larga")
    void whenUpdateLimite_equalToLongestPendingTask_thenSave() {
        var usuario = buildUsuario();
        given(usuarioRepository.findById(1L)).willReturn(Optional.of(usuario));
        given(tareaLogisticaRepository.findByUsuarioId(1L))
                .willReturn(List.of(
                        buildTarea("Decoraciones", "5.00", TareaLogisticaModel.ESTADO_PENDIENTE),
                        buildTarea("Invitaciones", "1", TareaLogisticaModel.ESTADO_PENDIENTE)));

        BigDecimal result = usuarioService.actualizarLimiteDiario(1L, new BigDecimal("5"));

        assertEquals(new BigDecimal("5"), result);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Las gestiones completadas o pospuestas no impiden bajar el límite diario")
    void whenUpdateLimite_belowNonPendingTasks_thenSave() {
        var usuario = buildUsuario();
        given(usuarioRepository.findById(1L)).willReturn(Optional.of(usuario));
        given(tareaLogisticaRepository.findByUsuarioId(1L))
                .willReturn(List.of(
                        buildTarea("Reservar salón", "5", "Completada"),
                        buildTarea("Catering", "6", "Pospuesta")));

        BigDecimal result = usuarioService.actualizarLimiteDiario(1L, new BigDecimal("4"));

        assertEquals(new BigDecimal("4"), result);
        verify(usuarioRepository).save(usuario);
    }
}