# Frontend CASE

Frontend web Angular para crear modelos conceptuales y ejecutar la generación del backend.

## Responsabilidades implementadas

- Crear proyecto.
- Crear modelo conceptual.
- Crear entidad.
- Crear atributos.
- Validar modelo.
- Generar backend.
- Descargar ZIP generado.

## Pendiente

- Editor visual con canvas.
- Relaciones desde interfaz.
- Edición y eliminación desde UI.
- Mejoras visuales finales.

## Organización

```text
src/app/componente-raiz.*       Pantalla funcional principal.
src/app/nucleo/modelos-case.ts  Tipos del backend CASE.
src/app/nucleo/servicio-api-case.ts Cliente HTTP.
```

## Ejecutar

Primero levantar el backend CASE:

```bash
docker compose -f ../infraestructura/local/docker-compose.yml up --build -d
```

Luego instalar dependencias y ejecutar Angular:

```bash
npm install
npm run iniciar
```

Abrir:

```text
http://localhost:4200
```

## Compilar

```bash
npm run compilar
```

## Backend esperado

La interfaz apunta por ahora a:

```text
http://localhost:8080
```
