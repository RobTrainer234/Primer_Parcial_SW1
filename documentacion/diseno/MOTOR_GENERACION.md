# Diseño del motor de generación

## Principio

El motor de generación usa reglas y plantillas. La IA puede ayudar durante el desarrollo, pero no debe ser la responsable directa de generar código crítico.

## Flujo

```text
Modelo conceptual persistido
→ Validación
→ Modelo intermedio
→ Reglas de transformación
→ Plantillas
→ Archivos generados
→ ZIP descargable
```

## Entradas

- Nombre del proyecto.
- Nombre del paquete.
- Entidades.
- Atributos.
- Tipos de datos.
- Claves primarias.
- Relaciones.
- Cardinalidades.

## Salidas

- Proyecto Maven Spring Boot.
- Entidades JPA.
- DTO.
- Repositorios.
- Servicios.
- Controladores REST.
- Manejador de excepciones.
- Configuración PostgreSQL.
- Swagger/OpenAPI.
- README del backend generado.

## Primera versión recomendada

1. Generar entidades sin relaciones.
2. Generar CRUD básico.
3. Confirmar que compila.
4. Agregar relaciones uno a muchos.
5. Agregar relaciones uno a uno.
6. Agregar relaciones muchos a muchos.
