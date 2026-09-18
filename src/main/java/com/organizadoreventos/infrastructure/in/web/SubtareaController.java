package com.organizadoreventos.infrastructure.in.web;

import com.organizadoreventos.application.dto.Comandos.AgregarSubtareaCommand;
import com.organizadoreventos.application.dto.Comandos.ReprogramarSubtareaCommand;
import com.organizadoreventos.application.dto.EventoDto;
import com.organizadoreventos.application.subtarea.AgregarSubtareaUseCase;
import com.organizadoreventos.application.subtarea.ReprogramarSubtareaUseCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * ARCHITECTURAL TRACE: Infrastructure Layer — Adaptador de entrada (REST)
 * Traza: US-005, US-012, US-013, US-014
 *
 * POST .../reprogramar es el punto de entrada del flujo transaccional de
 * referencia (ver javadoc de ReprogramarSubtareaUseCase).
 */
@RestController
@RequestMapping("/api/eventos/{eventoId}/subtareas")
public class SubtareaController {

    private final AgregarSubtareaUseCase agregarSubtareaUseCase;
    private final ReprogramarSubtareaUseCase reprogramarSubtareaUseCase;

    public SubtareaController(AgregarSubtareaUseCase agregarSubtareaUseCase,
                               ReprogramarSubtareaUseCase reprogramarSubtareaUseCase) {
        this.agregarSubtareaUseCase = agregarSubtareaUseCase;
        this.reprogramarSubtareaUseCase = reprogramarSubtareaUseCase;
    }

    public record AgregarSubtareaRequest(@NotBlank String nombre, String categoria,
                                          @NotNull LocalDate plazo, @Positive double horasEstimadas,
                                          boolean criticidadProveedorAlta) { }

    public record ReprogramarRequest(@NotNull LocalDate nuevaFecha) { }

    @PostMapping
    public ResponseEntity<EventoDto> agregar(@PathVariable UUID eventoId,
                                              @Validated @RequestBody AgregarSubtareaRequest request) {
        EventoDto resultado = agregarSubtareaUseCase.ejecutar(new AgregarSubtareaCommand(
                eventoId, request.nombre(), request.categoria(), request.plazo(),
                request.horasEstimadas(), request.criticidadProveedorAlta()));
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/{subtareaId}/reprogramar")
    public ResponseEntity<ReprogramarSubtareaUseCase.Resultado> reprogramar(
            @PathVariable UUID eventoId, @PathVariable UUID subtareaId,
            @Validated @RequestBody ReprogramarRequest request) {
        var resultado = reprogramarSubtareaUseCase.ejecutar(
                new ReprogramarSubtareaCommand(eventoId, subtareaId, request.nuevaFecha()));
        return ResponseEntity.ok(resultado);
    }
}
