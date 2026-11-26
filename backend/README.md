# allport-api — Sesión 8: DTOs y paginación

Backend Spring Boot del **Test de Valores de Allport** (asignatura Servicios Web, Universidad Americana).
Aplica las dos buenas prácticas de la guía de la Sesión 8 sobre el recurso **resultados del test**.

- Java 21 · Spring Boot 3.5 · Spring Data JPA · Bean Validation · PostgreSQL
- Puerto **9090** (el mismo que espera el frontend React de `../`)

## Ejecutar

1. Crear la base de datos en PostgreSQL:
   ```sql
   CREATE DATABASE allport_db;
   ```
2. Ajustar usuario y contraseña en `src/main/resources/application.properties`, o usar variables de entorno:
   `DB_URL`, `DB_USER`, `DB_PASSWORD`.
3. Levantar la API desde IntelliJ (`AllportApiApplication`) o con Maven:
   ```bash
   mvn spring-boot:run
   ```
4. En el primer arranque se cargan 6 valores y 12 resultados de ejemplo (datos ficticios), así ya se puede probar la página 0 y la página 1.
5. Pruebas automáticas (usan H2 en memoria, no necesitan PostgreSQL): `mvn test`

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/resultados` | Registra un resultado recibiendo **`ResultadoTestRequest`** (DTO). 201 / 400 / 409 |
| GET | `/api/v1/resultados?page=0&size=5&sort=id,asc&carrera=` | Listado **paginado** |
| GET | `/api/v1/resultados/{id}` | Detalle. 404 si no existe |
| GET | `/api/v1/valores?q=&page=&size=` | Catálogo de las 6 dimensiones (lo consume el frontend) |
| GET | `/api/v1/valores/{id}` | Detalle de un valor |

## Parte I · DTO de entrada

**Antes:** el POST habría recibido la entidad `ResultadoTest` directamente.

**Ahora:** recibe `dto/ResultadoTestRequest`, que solo trae lo que el cliente debe enviar:
carnet, nombre, carrera y los seis puntajes. **No** incluye `id`, `valorDominante` ni `fechaAplicacion`, porque los genera el sistema.

Reglas de validación:

| Campo | Reglas |
|---|---|
| `carnet` | `@NotBlank`, `@Pattern` solo dígitos (5–20) |
| `nombreEstudiante` | `@NotBlank`, `@Size(min=3, max=100)` |
| `carrera` | `@NotBlank`, `@Size(min=3, max=80)` |
| cada puntaje | `@NotNull`, `@Min(0)`, `@Max(80)` |
| los seis puntajes | `@AssertTrue`: deben **sumar 240**, como en el Estudio de Valores de Allport-Vernon-Lindzey |

Flujo por capas:

- **Controller** (`ResultadoTestController`): recibe `@Valid @RequestBody ResultadoTestRequest`, delega y responde `201 Created` con el encabezado `Location`.
- **Service** (`ResultadoTestService`): valida que el carnet no esté repetido (409), convierte el DTO en entidad, calcula el `valorDominante` y devuelve un `ResultadoTestResponse`.
- **Repository** (`ResultadoTestRepository`): persiste con Spring Data JPA.
- **`GlobalExceptionHandler`**: un DTO inválido devuelve **400** con el detalle por campo:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Los datos enviados no son válidos",
  "path": "/api/v1/resultados",
  "errores": {
    "carnet": "El carnet debe tener solo dígitos (entre 5 y 20)",
    "nombreEstudiante": "El nombre del estudiante es obligatorio",
    "teorico": "El puntaje teórico no puede ser negativo"
  }
}
```

## Parte II · Paginación

- El GET recibe `Pageable` (`page`, `size`, `sort`) y se lo pasa al Repository (`findAll(pageable)` / `findByCarreraContainingIgnoreCase(carrera, pageable)`). La base de datos aplica `LIMIT/OFFSET`: **no** se traen todos los registros para dividirlos a mano.
- Valores por defecto (`@PageableDefault`): `page=0`, `size=5`, orden por `id` ascendente.
- Tamaño máximo permitido: 50 (`spring.data.web.pageable.max-page-size`).
- La respuesta (`PaginaResponse`) permite saber qué página se devolvió:

```json
{
  "content": [ { "id": 6, "carnet": "20230006", "...": "..." } ],
  "number": 1,
  "size": 5,
  "numberOfElements": 5,
  "totalElements": 12,
  "totalPages": 3,
  "first": false,
  "last": false
}
```

## Parte III · Demostración en Postman

Importar `postman/Sesion8_DTO_Paginacion_Allport.postman_collection.json` (variable `baseUrl = http://localhost:9090`).
Cada petición trae pruebas automáticas en la pestaña *Tests*.

| Prueba | Petición | Resultado esperado |
|---|---|---|
| POST válido | 1 | 201 Created, `valorDominante` calculado por el sistema (`SOC`) |
| POST inválido | 2 y 3 | 400 con los campos que fallaron |
| POST carnet repetido | 4 | 409 Conflict |
| GET página 0 y 1 | 6 y 7 | `number` 0 y 1, registros 1–5 y 6–10 |
| GET con otro tamaño | 8 y 9 | Cambia `size` y la cantidad de elementos en `content` |

## Parte IV · Análisis

**1. ¿Qué problema evitó el uso del DTO?**
Sin DTO, el cliente podía enviar campos que solo debe definir el sistema: el `id` (y con él pisar un registro existente), la `fechaAplicacion` o el `valorDominante`, que así podría no coincidir con los puntajes. Ahora esos campos se ignoran y el sistema los genera. Además, los datos inválidos (carnet con letras, puntajes negativos o que no suman 240) se rechazan con un 400 antes de llegar a la base de datos, y la API ya no depende de la estructura interna de la tabla.

**2. Responsabilidades de cada capa**
- **Controller:** atender HTTP. Recibe y valida el DTO con `@Valid`, lee los parámetros de paginación, llama al Service y elige el código de respuesta (201, 200). No tiene lógica de negocio.
- **Service:** reglas de negocio. Revisa que el carnet no esté repetido, convierte DTO ↔ entidad, calcula el valor dominante y arma la respuesta paginada.
- **Repository:** acceso a datos. Guarda y consulta con Spring Data JPA, y aplica la paginación en la consulta SQL.

**3. Ventaja de la paginación cuando crecen los registros**
Si toda la universidad hace el test, la tabla puede tener miles de resultados. Con paginación, cada consulta lee y envía solo una página (por ejemplo 5 o 20 filas): baja el uso de memoria y de red, la respuesta es más rápida y el frontend puede mostrar la tabla por partes con los datos `totalPages` y `totalElements`. El límite de `size` (máximo 50) también evita que un cliente pida todo de una vez.
