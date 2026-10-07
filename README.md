# IF0009-Lab13-C5C089

MedPharm Express — Laboratorio 13, Desarrollo de Software IV.
Carné: C5C089.

## Ejecución

Requisitos: Java 21 o superior, Node.js compatible con Angular 19 y npm.

En una terminal:

```powershell
cd medpharm-backend
.\mvnw.cmd spring-boot:run
```

En otra terminal:

```powershell
cd medpharm-frontend
npm ci
npm start
```

Abrir http://localhost:4200. La API se ejecuta en http://localhost:8080/api/v1.
La base H2 es temporal y vuelve a cargar los datos de `schema.sql` y `data.sql` al iniciar.

| Usuario | Contraseña | Rol |
| --- | --- | --- |
| medico1 | password123 | MEDICO |
| farma1 | password123 | FARMACEUTICO |

El médico crea recetas con varios medicamentos. El listado permite filtrar y despachar o cancelar recetas pendientes. El stock se descuenta al crear la receta y se devuelve si se cancela.

## Depuración del error 401

Spring Security protege `/api/v1/recetas` mediante JWT. Si Angular no registra `authInterceptor` en `app.config.ts`, la petición sale sin el encabezado `Authorization`, aunque el token esté guardado en `localStorage`. El filtro no puede identificar al usuario y el servidor responde `401 Unauthorized`.

El interceptor lee el token de `AuthService`, clona la solicitud dirigida a la API y agrega `Authorization: Bearer <token>`. Se registra con:

```typescript
provideHttpClient(withInterceptors([authInterceptor]))
```

CORS permite el origen `http://localhost:4200`, la cabecera `Authorization` y las solicitudes preflight. Un problema de CORS es distinto de un 401: el preflight comprueba los permisos del origen y los encabezados; el JWT identifica al usuario en la petición posterior.

La prueba automatizada comprueba el 401 sin token y el acceso correcto después del login. Queda pendiente la captura en el navegador `docs/error_jwt_401.png`.

Para reproducirla:

1. Cambiar temporalmente el registro por `provideHttpClient()` en `app.config.ts`.
2. Iniciar sesión y abrir `/recetas`.
3. En las herramientas del navegador, abrir Network y recargar. Seleccionar la petición `recetas` y comprobar el estado 401 y la ausencia de `Authorization`.
4. Guardar la captura como `docs/error_jwt_401.png`.
5. Restaurar `provideHttpClient(withInterceptors([authInterceptor]))`.

## Verificación

```powershell
cd medpharm-backend
.\mvnw.cmd test
```

Las pruebas cubren login, rechazo sin token y con token inválido, CORS, consultas, validaciones, stock y cambios de estado.

```powershell
cd medpharm-frontend
npm run build
```

