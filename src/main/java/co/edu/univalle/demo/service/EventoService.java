package co.edu.univalle.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.univalle.demo.exception.ResourceNotFoundException;
import co.edu.univalle.demo.model.EventoModel;
import co.edu.univalle.demo.repository.EventoRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de eventos.
 */
@Service
public class EventoService {

    /** Repositorio de acceso a datos de eventos. */
    private final EventoRepository eventoRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param eventoRepository repositorio de eventos
     */
    public EventoService(final EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    /**
     * Crea un nuevo evento en el sistema.
     * 
     * @param evento datos del evento a crear
     * @return el evento creado con su id asignado
     */
    @Transactional
    public EventoModel crear(final EventoModel evento) {
        return eventoRepository.save(evento);
    }

    /**
     * Actualiza los datos de un evento existente.
     * 
     * @param evento datos actualizados del evento con id válido
     * @return el evento actualizado
     * @throws ResourceNotFoundException si el evento no existe
     */
    @Transactional
    public EventoModel actualizar(final EventoModel evento) {
        eventoRepository.findById(evento.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Evento con id " + evento.getId() + " no encontrado"
            ));
        return eventoRepository.save(evento);
    }

    /**
     * Elimina un evento por su id.
     * 
     * @param id identificador del evento a eliminar
     * @throws ResourceNotFoundException si el evento no existe
     */
    @Transactional
    public void eliminar(final Long id) {
        eventoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Evento con id " + id + " no encontrado"
            ));
        eventoRepository.deleteById(id);
    }

    /**
     * Retorna todos los eventos registrados.
     * 
     * @return lista de eventos
     */
    @Transactional(readOnly = true)
    public List<EventoModel> obtenerTodos() {
        return eventoRepository.findAll();
    }

    /**
     * Busca un evento por su id.
     * 
     * @param id identificador del evento
     * @return el evento encontrado
     * @throws ResourceNotFoundException si el evento no existe
     */
    @Transactional(readOnly = true)
    public EventoModel obtenerPorId(final Long id) {
        return eventoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Evento con id " + id + " no encontrado"
            ));
    }

    /**
     * Busca eventos cuyo nombre contenga el texto indicado.
     * 
     * @param nombre fragmento del nombre a buscar
     * @return lista de eventos que coinciden
     */
    @Transactional(readOnly = true)
    public List<EventoModel> buscarPorNombre(final String nombre) {
        return eventoRepository.findAllByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Busca todos los eventos asociados a un usuario específico.
     * 
     * @param usuarioId identificador del usuario organizador
     * @return lista de eventos creados por el usuario
     */
    @Transactional(readOnly = true)
    public List<EventoModel> buscarPorUsuarioId(final Long usuarioId) {
        return eventoRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Busca eventos filtrados por su estado actual.
     * 
     * @param estado estado del evento (ej. Planificación, En Progreso)
     * @return lista de eventos que coinciden con el estado
     */
    @Transactional(readOnly = true)
    public List<EventoModel> buscarPorEstado(final String estado) {
        return eventoRepository.findByEstado(estado);
    }

}