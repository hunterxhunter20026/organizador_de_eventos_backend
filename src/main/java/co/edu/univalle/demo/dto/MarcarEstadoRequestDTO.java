package co.edu.univalle.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuerpo de la petición para cambiar el estado de una tarea logística (US-09). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcarEstadoRequestDTO {
    /** Nuevo estado: "Pendiente", "Completada" o "Pospuesta". */
    private String estado;
    /** Nota opcional explicativa (ej. "esperando confirmación de salón"). */
    private String nota;
}
