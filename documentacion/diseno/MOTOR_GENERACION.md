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

## Primera versión implementada

1. Convertir modelo conceptual a modelo intermedio.
2. Validar antes de generar.
3. Registrar trabajo de generación.
4. Generar proyecto Spring Boot CRUD básico.
5. Crear ZIP descargable.
6. Registrar artefacto generado.

## Pendiente de mejora

1. Confirmar compilación real con Maven instalado.
2. Agregar relaciones uno a muchos en el backend generado.
3. Agregar relaciones uno a uno.
4. Agregar relaciones muchos a muchos.
5. Generar DTO relacionales más completos.
