package com.organizadoreventos.infrastructure.config;

import com.organizadoreventos.domain.planificacion.ConflictoSobrecargaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ARCHITECTURAL TRACE: Infrastructure Layer — Wiring (Dependency Injection)
 * ConflictoSobrecargaService es un POJO deliberadamente libre de anotaciones
 * de Spring (dominio testeable en aislamiento). Este es el único lugar
 * donde se le da ciclo de vida como bean.
 */
@Configuration
public class BeanConfig {

    @Bean
    public ConflictoSobrecargaService conflictoSobrecargaService() {
        return new ConflictoSobrecargaService();
    }
}
