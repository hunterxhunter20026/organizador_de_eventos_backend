package co.edu.univalle.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.univalle.demo.exception.BusinessException;
import co.edu.univalle.demo.exception.ResourceNotFoundException;
import co.edu.univalle.demo.model.UsuarioModel;
import co.edu.univalle.demo.repository.UsuarioRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de usuarios organizadores.
 */
@Service
public class UsuarioService {

    /** Repositorio de acceso a datos de usuarios. */
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param usuarioRepository repositorio de usuarios
     */
    public UsuarioService(final UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Crea un nuevo usuario validando la unicidad de su correo electrónico.
     * 
     * @param usuario datos del usuario a crear
     * @return el usuario creado con su id asignado
     * @throws BusinessException si el email ya está registrado
     */
    @Transactional
    public UsuarioModel crear(final UsuarioModel usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new BusinessException(
                "Ya existe un usuario con el email: " + usuario.getEmail()
            );
        }
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza los datos de un usuario existente.
     * 
     * @param usuario datos actualizados del usuario con id válido
     * @return el usuario actualizado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Transactional
    public UsuarioModel actualizar(final UsuarioModel usuario) {
        usuarioRepository.findById(usuario.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario con id " + usuario.getId() + " no encontrado"
            ));
        return usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario por su id.
     * 
     * @param id identificador del usuario a eliminar
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Transactional
    public void eliminar(final Long id) {
        usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario con id " + id + " no encontrado"
            ));
        usuarioRepository.deleteById(id);
    }

    /**
     * Retorna todos los usuarios registrados.
     * 
     * @return lista de usuarios
     */
    @Transactional(readOnly = true)
    public List<UsuarioModel> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su id.
     * 
     * @param id identificador del usuario
     * @return el usuario encontrado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Transactional(readOnly = true)
    public UsuarioModel obtenerPorId(final Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario con id " + id + " no encontrado"
            ));
    }

    /**
     * Busca usuarios cuyo nombre contenga el texto indicado.
     * 
     * @param nombre fragmento del nombre a buscar
     * @return lista de usuarios que coinciden
     */
    @Transactional(readOnly = true)
    public List<UsuarioModel> buscarPorNombre(final String nombre) {
        return usuarioRepository.findAllByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Busca un usuario por su email.
     * 
     * @param email correo electrónico del usuario
     * @return Optional con el usuario si existe
     */
    @Transactional(readOnly = true)
    public Optional<UsuarioModel> buscarPorEmail(final String email) {
        return usuarioRepository.findByEmail(email);
    }

}