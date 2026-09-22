package co.edu.univalle.demo.controller;

import co.edu.univalle.demo.model.EventoModel;
import co.edu.univalle.demo.service.EventoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la gestión de eventos.
 */
@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    /** Servicio de lógica de negocio para eventos. */
    private final EventoService eventoService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param eventoService servicio de eventos
     */
    public EventoController(final EventoService eventoService) {
        this.eventoService = eventoService;
    }

    /**
     * Crea un nuevo evento en el sistema.
     * 
     * @param evento datos del evento a crear
     * @return el evento creado con código HTTP 201
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoModel crear(@RequestBody final EventoModel evento) {
        return eventoService.crear(evento);
    }

    /**
     * Actualiza un evento existente.
     * 
     * @param id identificador del evento
     * @param evento datos actualizados
     * @return el evento actualizado
     */
    @PutMapping("/{id}")
    public EventoModel actualizar(
            @PathVariable final Long id,
            @RequestBody final EventoModel evento) {
        evento.setId(id);
        return eventoService.actualizar(evento);
    }

    /**
     * Elimina un evento por su identificador.
     * 
     * @param id identificador del evento
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable final Long id) {
        eventoService.eliminar(id);
    }

    /**
     * Obtiene la lista de todos los eventos registrados.
     * 
     * @return lista de eventos
     */
    @GetMapping
    public List<EventoModel> obtenerTodos() {
        return eventoService.obtenerTodos();
    }

    /**
     * Obtiene un evento específico por su ID.
     * 
     * @param id identificador del evento
     * @return el evento encontrado
     */
    @GetMapping("/{id}")
    public EventoModel obtenerPorId(@PathVariable final Long id) {
        return eventoService.obtenerPorId(id);
    }

    /**
     * Busca eventos cuyo nombre coincida parcialmente.
     * 
     * @param nombre fragmento del nombre
     * @return lista de eventos coincidentes
     */
    @GetMapping("/buscar")
    public List<EventoModel> buscarPorNombre(@RequestParam final String nombre) {
        return eventoService.buscarPorNombre(nombre);
    }

    /**
     * Obtiene todos los eventos creados por un usuario específico.
     * 
     * @param usuarioId identificador del usuario organizador
     * @return lista de eventos del usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public List<EventoModel> buscarPorUsuarioId(@PathVariable final Long usuarioId) {
        return eventoService.buscarPorUsuarioId(usuarioId);
    }

    /**
     * Filtra los eventos según su estado actual.
     * 
     * @param estado estado del evento (ej. Planificación)
     * @return lista de eventos con ese estado
     */
    @GetMapping("/estado")
    public List<EventoModel> buscarPorEstado(@RequestParam final String estado) {
        return eventoService.buscarPorEstado(estado);
    }

}