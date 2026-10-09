# Flujo de sobrecarga diaria

## Regla de manejo

Antes de crear o cambiar la fecha/horas de una gestión, el backend suma las horas
estimadas de las gestiones **Pendientes** del usuario para el día afectado. La
gestión que se está editando se excluye de la suma anterior para no contarla dos
veces. Si el total resultante es mayor que el límite diario configurado, no se
guarda el cambio y se responde `409 Conflict`.

Las gestiones **Completadas** y **Pospuestas** no ocupan horas del día. El límite
por defecto es de 6 horas; se puede configurar entre 1 y 16 horas. La regla
previene una sobrecarga sin bloquear el trabajo: conserva la tarea al detectar
el problema y permite corregir la fecha o las horas. Posponerla es una opción
para una gestión existente; una gestión nueva todavía no tiene un estado que se
pueda posponer.

## Autenticación

Los endpoints siguientes requieren el token de sesión del usuario:

```http
Authorization: Bearer <token>
Content-Type: application/json
```

El servidor determina al usuario desde el token; no se debe enviar un `usuarioId`
en el cuerpo de las peticiones.

## Endpoints relacionados

| Método y ruta | Uso | Respuestas relevantes |
|---|---|---|
| `GET /api/configuracion/limite-diario` | Consultar el límite diario vigente. | `200` |
| `PUT /api/configuracion/limite-diario` | Cambiar el límite diario. | `200`; validación si queda fuera de 1–16 h |
| `GET /api/eventos/{eventoId}/tareas` | Listar las gestiones del evento. | `200` |
| `POST /api/eventos/{eventoId}/tareas` | Crear una gestión Pendiente. | `201`; `409` si el día se sobrecarga |
| `PUT /api/tareas/{id}/reprogramar` | Cambiar la fecha de una gestión. | `200`; `409` si el día destino se sobrecarga |
| `PUT /api/tareas/{id}` | Actualizar nombre, plazo y horas; la interfaz lo usa para aplicar una reducción de horas. | `200`; `409` si el cambio se sobrecarga |
| `PATCH /api/tareas/{id}/estado` | Marcar una gestión existente como Completada o Pospuesta. | `200` |

### Ejemplos de petición

Crear una gestión:

```http
POST /api/eventos/12/tareas
```

```json
{
  "titulo": "Confirmar el servicio de sonido",
  "fechaLimite": "2026-10-12",
  "horasEstimadas": 2
}
```

Reprogramar una gestión:

```http
PUT /api/tareas/34/reprogramar
```

```json
{
  "nuevoPlazo": "2026-10-12"
}
```

Reducir horas o cambiar el plazo de una gestión:

```http
PUT /api/tareas/34
```

```json
{
  "titulo": "Confirmar el servicio de sonido",
  "descripcion": "Coordinar llegada y montaje",
  "fechaLimite": "2026-10-12",
  "horasEstimadas": 1
}
```

Posponer una gestión existente:

```http
PATCH /api/tareas/34/estado
```

```json
{
  "estado": "Pospuesta",
  "nota": "Pendiente de confirmar el horario con el proveedor"
}
```

Actualizar el límite:

```http
PUT /api/configuracion/limite-diario
```

```json
{
  "limiteHorasDiarias": 6
}
```

### Ejemplo de respuesta de conflicto

```http
HTTP/1.1 409 Conflict
Content-Type: application/json
```

```json
{
  "status": 409,
  "message": "Quedarías con 7h de gestión planificadas (límite 6h)",
  "path": "/api/tareas/34/reprogramar",
  "timestamp": "2026-10-09T11:40:00",
  "horasTotales": 7,
  "limite": 6,
  "opciones": [
    "Mover la gestión a otro día",
    "Reducir las horas estimadas",
    "Posponer la gestión"
  ]
}
```

La interfaz transforma este resultado en acciones. Al reprogramar una gestión
existente se puede elegir otra fecha, reducir horas o posponer. Al crear una
gestión nueva se puede elegir otra fecha o reducir horas; posponer no aplica
hasta que exista la gestión. Si la alternativa vuelve a exceder el límite, el
servidor devuelve el conflicto y la interfaz conserva el aviso para intentar
otra opción. Una resolución exitosa muestra un mensaje de confirmación y
actualiza la lista.

## Registro de reprogramaciones

Los intentos de reprogramación se guardan en la tabla de historial con fecha
anterior, fecha propuesta y la marca/detalle de conflicto, incluso cuando no se
completa el cambio por sobrecarga. La operación conserva ese registro aunque
devuelva `409 Conflict`. Actualmente no hay un endpoint REST para consultar ese
historial; la evidencia del registro debe obtenerse desde la base de datos o las
herramientas de administración, no desde una ruta de API.

## Bitácora de iteración

| Fecha | Observación | Cambio | Estado de verificación |
|---|---|---|---|
| 2026-10-09 | El servicio detectaba la sobrecarga y enviaba alternativas, pero la pantalla solo las mostraba como texto y no permitía ejecutarlas. | Se añadieron acciones para cambiar fecha, reducir horas y posponer una gestión existente. Para una gestión nueva, las alternativas son cambiar fecha o reducir horas. | La implementación y el mapeo a los endpoints están documentados; el recorrido manual con usuario autenticado y las capturas quedan pendientes. |
| 2026-10-09 | Faltaba retroalimentación inmediata tras resolver el conflicto y había que evitar fechas inválidas o una reducción que no redujera las horas. También se debía conservar el intento fallido en el historial. | Se añadieron un diálogo accesible, validaciones antes de enviar, cancelación sin cambios y confirmación con `role="status"`. Los errores devueltos por la API quedan visibles para volver a intentar. La transacción de reprogramación conserva el registro aunque responda con conflicto. | `npm run build` y `gradlew compileJava` pasan. `gradlew test` está bloqueado al compilar pruebas existentes de `EventoServiceTest`, cuyas firmas de constructor y métodos no coinciden con `EventoService`; no se ejecutó una prueba interactiva del flujo. |

## Capturas de evidencia UX/HCI

1. Configuración mostrando el límite diario.
2. Gestiones Pendientes y horas del día antes de provocar el conflicto.
3. Conflicto tras crear o reprogramar, con el total, el límite y las alternativas.
4. Formulario de la alternativa elegida, por ejemplo fecha nueva o menos horas.
5. Confirmación de éxito y lista ya actualizada; tomar una captura distinta para
   la acción de posponer.
6. Validación de una fecha vacía/inválida o de horas que no sean menores.
7. Herramienta API mostrando la respuesta `409` con `horasTotales`, `limite` y
   `opciones` (sin incluir el token).
8. Registro de historial visible en la base de datos, si se presenta como
   evidencia adicional. No rotularlo como endpoint REST.
