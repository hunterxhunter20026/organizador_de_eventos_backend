# Organizador de Eventos — Archetype Backend

Este es el **esqueleto arquitectónico**, no la implementación completa del
backlog. El objetivo es dejarte la estructura hexagonal correcta y la
conexión real a base de datos ya probadas, para que construyas el resto
del backlog sobre esta base con confianza de que el cableado funciona.

## Qué SÍ está completo y funcional

- Arquitectura hexagonal completa: `domain/` (puro, sin Spring) →
  `application/` (casos de uso) → `infrastructure/` (adaptadores)
- **Conexión real a PostgreSQL**: Flyway crea el esquema, JPA persiste,
  todo verificable con `docker compose up --build`
- **3 casos de uso funcionando extremo a extremo**:
  1. `POST /api/eventos` — crear evento
  2. `GET /api/eventos` — listar eventos (prueba la lectura completa)
  3. `POST /api/eventos/{id}/subtareas` — agregar subtarea al agregado
- **El flujo transaccional más importante del dominio, completo**:
  `POST /api/eventos/{id}/subtareas/{id}/reprogramar` — reprograma una
  subtarea y evalúa sobrecarga diaria de forma atómica
  (`ConflictoSobrecargaService`, con test unitario de casos límite)
- Tests unitarios reales (Mockito, sin BD) para el motor de reglas y el
  caso de uso transaccional

## Qué NO está — a propósito

| Falta | Por qué se dejó fuera | Dónde engancharlo |
|---|---|---|
| Autenticación (JWT, Organizador) | Es la pieza más grande no relacionada con "estructura+conexión" | `EventoController.ORGANIZADOR_DEMO` es el único lugar que sabe que no hay auth — reemplázalo por `@AuthenticationPrincipal UUID` |
| Editar/eliminar evento y subtarea | CRUD repetitivo, mismo patrón que Crear/Agregar | Copia `CrearEventoUseCase`, cambia el verbo |
| Marcar progreso (`estado`) | No añade nada nuevo al patrón ya probado | Mismo patrón que `AgregarSubtareaUseCase` |
| Vista "Hoy" / priorización | Es un Domain Service nuevo (Strategy/Chain), no una extensión del que ya existe | Nuevo archivo en `domain/priorizacion/`, ver conversación de diseño anterior si la tienes |
| Configuración de límite diario por usuario | Depende de que exista Organizador (auth) | `ReprogramarSubtareaUseCase` usa `LimiteDiario.porDefecto()` como marcador — la línea a cambiar está comentada en el archivo |
| Frontend | Fuera de alcance de este pedido — "backend únicamente" | — |

## Arrancar

```bash
cp .env.example .env
# edita .env con tu propia DB_PASSWORD
docker compose up --build
```

```bash
curl -X POST localhost:8080/api/eventos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Boda de prueba","fecha":"2026-12-01","ubicacion":"Salón X"}'

curl localhost:8080/api/eventos
```

## Tests

```bash
mvn test
```
