# gestion-academica-allport-web

> **Taller #1 (Sesión 4) · rama `taller-1`:** propuesta de arquitectura, configuración del entorno y checklist de Postman en [`docs/taller-1/`](docs/taller-1/README.md).

> **Sesión 8 · DTOs y paginación:** el backend Spring Boot está en [`backend/`](backend/README.md) (DTO de entrada validado en `POST /api/v1/resultados` y paginación con `Pageable` en el GET, más la colección de Postman y el análisis).

Frontend React + TypeScript + Vite del proyecto de **Servicios Web** (Sesión 12 — Consumo de API REST desde React) para el **Test de Valores de Allport**.

## Ejecutar

```bash
pnpm install
cp .env.example .env        # ajustar VITE_API_PROXY_TARGET al puerto real de Spring Boot
pnpm dev                    # http://localhost:5173
```

- `VITE_API_BASE_URL` (`/api/v1`): prefijo de todas las rutas de la API.
- Proxy de Vite (`vite.config.ts`): reenvía `/api` a `VITE_API_PROXY_TARGET` (por defecto `http://localhost:9090`), evitando problemas de CORS en desarrollo.
- `VITE_USE_MOCK=true`: datos simulados locales, solo para desarrollar sin backend. Para la demostración use `false` con la API real levantada.

## Estructura

```
src/
  shared/api/httpClient.ts      Cliente HTTP compartido (URL base, Bearer, ApiError, 401/403)
  shared/api/mockData.ts        Datos simulados (solo VITE_USE_MOCK=true)
  features/valores/
    types.ts                    Tipos TypeScript del contrato
    valoresApi.ts               Funciones de acceso a datos (sin componentes)
    useValores.ts               Hooks de TanStack Query
    ValoresPage.tsx             Tabla, búsqueda, detalle y estados carga/vacío/error
```

## Contrato de la API a verificar (Actividad 1)

Rutas y esquema **supuestos**: contrastar con Postman / OpenAPI del backend y ajustar `types.ts` y `valoresApi.ts` si difieren.

| Recurso | Método y ruta | Parámetros | Respuesta esperada |
|---|---|---|---|
| Valores | `GET /api/v1/valores` | `q` (búsqueda, opcional) | Arreglo `Valor[]` o `Page<Valor>` de Spring (`content`) |
| Valor | `GET /api/v1/valores/{id}` | `id` | `Valor` |

`Valor`: `{ id: number, codigo: string, nombre: string, descripcion: string }`

- Autenticación: si el endpoint exige Bearer, guardar el token con `tokenStore.set(...)`; el cliente lo envía en `Authorization`. 401 y 403 se muestran con mensajes claros, sin exponer el token.

## Qué cubre la guía

- Actividad 2: tipo TS, función de acceso separada, vista con carga / sin resultados / error de red o servidor, y TanStack Query (sin fetch durante el render).
- Actividad 3: opción A (detalle al seleccionar) y opción B (búsqueda por parámetro `q`).
- Actividad 4: comparar con Postman usando la pestaña Network del navegador (URL, código HTTP, respuesta).
