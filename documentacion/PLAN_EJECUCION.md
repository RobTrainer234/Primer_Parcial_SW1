# Plan de ejecución por fases

## Criterio rector

El proyecto se construye de atrás hacia adelante pensando en la defensa: primero debe funcionar el flujo completo mínimo y luego se mejoran detalles.

```text
Modelo conceptual → Validación → Generación → Swagger → Flutter → IA local → AWS
```

## Fase 0: Base del repositorio

**Objetivo:** dejar listo el monorepo y la documentación inicial.

- Crear carpetas principales.
- Definir arquitectura.
- Preparar esqueletos de backend, frontend, móvil, infraestructura y evidencias.
- Crear guía de planificación.

**Resultado esperado:** el equipo entiende dónde va cada parte del sistema.

## Fase 1: Backend CASE básico

**Objetivo:** implementar la administración de proyectos.

- Crear proyecto.
- Listar proyectos.
- Editar proyecto.
- Eliminar proyecto.
- Configurar PostgreSQL.
- Configurar Swagger.

**Resultado esperado:** existe una API base usable.

## Fase 2: Modelo conceptual

**Objetivo:** persistir el modelo de negocio.

- Crear modelo conceptual.
- Crear entidades.
- Crear atributos.
- Definir claves primarias.
- Definir tipos de datos.
- Guardar posición visual.

**Resultado esperado:** se puede representar un modelo simple.

## Fase 3: Relaciones y cardinalidades

**Objetivo:** completar el modelado conceptual.

- Relación uno a uno.
- Relación uno a muchos.
- Relación muchos a muchos.
- Edición y eliminación de relaciones.
- Visualización en frontend.

**Resultado esperado:** se puede modelar un negocio real.

## Fase 4: Validación

**Objetivo:** impedir generación inválida.

Validaciones mínimas:

- Entidad sin nombre.
- Entidad duplicada.
- Atributo duplicado.
- Entidad sin clave primaria.
- Relación incompleta.
- Tipo no soportado.
- Nombre incompatible con Java.
- Referencia inexistente.

**Resultado esperado:** el sistema informa errores claros antes de generar.

## Fase 5: Modelo intermedio

**Objetivo:** separar editor visual y generador.

- Convertir modelo persistido en estructura limpia.
- Normalizar nombres.
- Convertir tipos conceptuales a tipos Java.
- Preparar relaciones.

**Resultado esperado:** el generador trabaja con datos estables y no con detalles del canvas.

## Fase 6: Generador determinista

**Objetivo:** producir un backend Spring Boot funcional.

- Plantillas para proyecto Maven.
- Entidades JPA.
- DTO.
- Repositorios.
- Servicios.
- Controladores.
- Manejador de excepciones.
- Swagger/OpenAPI.
- Configuración PostgreSQL.
- ZIP descargable.

**Resultado esperado:** el backend generado compila y ejecuta.

## Fase 7: Frontend CASE

**Objetivo:** permitir uso visual durante la defensa.

- Lista de proyectos.
- Editor de modelo.
- Panel de propiedades.
- Panel de validación.
- Panel de generación y descarga.

**Resultado esperado:** el docente puede ver el flujo completo desde la interfaz.

## Fase 8: Flutter

**Objetivo:** consumir el backend generado.

- Cliente HTTP configurable.
- Pantallas CRUD reutilizables.
- Lectura de descriptor de entidades.
- Manejo básico de errores.

**Resultado esperado:** la app móvil consume datos reales del backend generado.

## Fase 9: Asistente local offline

**Objetivo:** cumplir la exigencia de IA local.

- Contexto local de la aplicación.
- Base de conocimiento local.
- Respuestas guiadas por entidad y operación.
- Prueba sin Internet.

**Resultado esperado:** el asistente responde aun sin conexión.

## Fase 10: AWS y cierre

**Objetivo:** preparar defensa y despliegue.

- Despliegue frontend.
- Despliegue backend.
- Base PostgreSQL.
- Evidencias.
- Simulacros con clínica, biblioteca y ventas.

**Resultado esperado:** existen versión local, versión AWS y documentación defendible.
