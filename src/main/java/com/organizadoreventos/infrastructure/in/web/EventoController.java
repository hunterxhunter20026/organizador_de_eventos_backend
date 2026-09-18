package com.organizadoreventos.infrastructure.in.web;

import com.organizadoreventos.application.dto.Comandos.CrearEventoCommand;
import com.organizadoreventos.application.dto.EventoDto;
import com.organizadoreventos.application.evento.CrearEventoUseCase;
import com.organizadoreventos.application.evento.ListarEventosUseCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * ARCHITECTURAL TRACE: Infrastructure Layer — Adaptador de entrada (REST)
 * Traza: US-001, US-004
 *
 * [Archetype] Sin autenticación todavía: se usa un organizadorId fijo
 * ({@link #ORGANIZADOR_DEMO}) en vez de leerlo de un JWT/sesión. Cuando
 * agregues auth, reemplaza ORGANIZADOR_DEMO por @AuthenticationPrincipal
 * UUID — ningún otro archivo de este flujo necesita cambiar.
 */
@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private static final UUID ORGANIZADOR_DEMO = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final CrearEventoUseCase crearEventoUseCase;
    private final ListarEventosUseCase listarEventosUseCase;

    public EventoController(CrearEventoUseCase crearEventoUseCase, ListarEventosUseCase listarEventosUseCase) {
        this.crearEventoUseCase = crearEventoUseCase;
        this.listarEventosUseCase = listarEventosUseCase;
    }

    public record CrearEventoRequest(@NotBlank String nombre, @NotNull LocalDate fecha, @NotBlank String ubicacion) { }

    @PostMapping
    public ResponseEntity<EventoDto> crear(@Validated @RequestBody CrearEventoRequest request) {
        EventoDto creado = crearEventoUseCase.ejecutar(
                new CrearEventoCommand(ORGANIZADOR_DEMO, request.nombre(), request.fecha(), request.ubicacion()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<EventoDto>> listar() {
        return ResponseEntity.ok(listarEventosUseCase.ejecutar(ORGANIZADOR_DEMO));
    }
}
