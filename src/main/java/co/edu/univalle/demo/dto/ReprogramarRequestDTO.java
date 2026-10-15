package co.edu.univalle.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuerpo de la petición para reprogramar una tarea logística. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReprogramarRequestDTO {
    /** Nueva fecha objetivo propuesta para la gestión. */
    private LocalDate nuevoPlazo;
    /** Nuevo tiempo estimado opcional; si se omite se conservan las horas actuales. */
    private BigDecimal horasEstimadas;
}
