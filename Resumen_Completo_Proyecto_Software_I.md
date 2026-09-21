# Resumen General del Proyecto – Herramienta CASE para Generación de Backend

## 1. Descripción general

El proyecto consiste en desarrollar una **herramienta CASE especializada en modelado conceptual de bases de datos**, orientada a acelerar el desarrollo de aplicaciones a partir de un diseño conceptual.

La herramienta permitirá que un desarrollador modele gráficamente un determinado negocio mediante:

- Entidades.
- Atributos.
- Claves primarias.
- Tipos de datos.
- Relaciones.
- Cardinalidades 1:1, 1:N y N:M.

Una vez finalizado el modelo conceptual, el sistema deberá **validarlo y generar automáticamente un backend funcional en Spring Boot**, preparado para trabajar con PostgreSQL y ser consumido por una aplicación móvil desarrollada en Flutter.

La solución también deberá incorporar un **asistente de inteligencia artificial local en la aplicación móvil**, capaz de ayudar al usuario a comprender y utilizar la aplicación incluso cuando no exista conexión a Internet.

El proyecto debe mantener una **versión local** y una **versión desplegada en AWS**.

---

# 2. Finalidad del proyecto

La finalidad principal es reducir el tiempo y el esfuerzo necesarios para transformar un diseño conceptual de base de datos en una estructura de software funcional.

En un proceso tradicional, después de diseñar el modelo conceptual, el desarrollador debe crear manualmente:

- Entidades.
- Relaciones.
- DTO.
- Repositorios.
- Servicios.
- Controladores.
- Configuración de la base de datos.
- Endpoints REST.
- Documentación de API.

La herramienta desarrollada automatizará una parte importante de este proceso.

El flujo principal será:

```text
Diseño conceptual
        ↓
Representación gráfica en la herramienta CASE
        ↓
Validación del modelo
        ↓
Modelo intermedio
        ↓
Motor de transformación
        ↓
Generación automática de código
        ↓
Backend Spring Boot
        ↓
PostgreSQL
        ↓
API REST
        ↓
Aplicación Flutter
        ↓
Asistente IA local
```

---

# 3. Escenario principal de evaluación

El proyecto debe estar preparado para la siguiente situación durante el examen:

1. El docente plantea un negocio.
2. El docente dibuja en la pizarra el diseño conceptual de la base de datos.
3. El equipo abre la herramienta CASE desarrollada.
4. Se reproduce el diseño conceptual dentro del sistema.
5. Se crean las entidades.
6. Se agregan los atributos.
7. Se definen las claves.
8. Se establecen las relaciones.
9. Se asignan las cardinalidades.
10. Se valida el modelo.
11. Se ejecuta la opción **Generar Backend**.
12. La herramienta genera un proyecto Spring Boot.
13. El proyecto generado se ejecuta.
14. Se conecta con PostgreSQL.
15. Se verifican los endpoints mediante Swagger/OpenAPI.
16. Se construye o adapta el frontend móvil en Flutter.
17. Flutter consume el backend generado.
18. La aplicación móvil incorpora el asistente de IA local.
19. Se demuestra el funcionamiento con Internet.
20. Se demuestra el funcionamiento básico del asistente sin Internet.
21. Se demuestra la versión local.
22. Se demuestra la versión desplegada en AWS.

El proyecto debe diseñarse de atrás hacia adelante considerando este escenario como el criterio principal de éxito.

---

# 4. Producto Mínimo Viable – MVP

Debido al corto tiempo disponible, el desarrollo debe concentrarse inicialmente en las funcionalidades imprescindibles.

## 4.1. Funcionalidades obligatorias del MVP

### Gestión de proyectos

- Crear proyecto.
- Consultar proyectos.
- Abrir proyecto.
- Editar información básica.
- Eliminar proyecto.

### Editor conceptual

- Crear un modelo conceptual.
- Crear entidades.
- Editar entidades.
- Eliminar entidades.
- Mover entidades dentro del lienzo.
- Agregar atributos.
- Editar atributos.
- Eliminar atributos.
- Definir tipos de datos.
- Definir clave primaria.
- Definir atributos obligatorios u opcionales.
- Definir atributos únicos.

### Relaciones

- Crear relación 1:1.
- Crear relación 1:N.
- Crear relación N:M.
- Modificar relación.
- Eliminar relación.
- Visualizar cardinalidades.

### Persistencia del modelo

- Guardar el diagrama.
- Recuperar el diagrama.
- Continuar trabajando posteriormente.

### Validación

El sistema deberá detectar, como mínimo:

- Entidades sin nombre.
- Entidades duplicadas.
- Atributos duplicados.
- Entidades sin clave primaria.
- Relaciones incompletas.
- Tipos de datos no soportados.
- Nombres incompatibles con la generación Java.
- Referencias a entidades inexistentes.

### Generación de backend

El backend generado deberá incluir:

- Entidades JPA.
- DTO.
- Repositories.
- Services.
- Controllers.
- Mappers si son necesarios.
- Manejo básico de excepciones.
- Relaciones JPA.
- Configuración PostgreSQL.
- Dependencias Maven.
- Swagger/OpenAPI.
- CRUD básicos.
- Archivo de configuración de la aplicación.

### Descarga

- Empaquetar el backend generado.
- Descargarlo como proyecto.
- Ejecutarlo sin modificar manualmente su estructura principal.

### Flutter

- Contar con una estructura móvil reutilizable.
- Consumir la API REST generada.
- Implementar las pantallas necesarias para el negocio evaluado.
- Reutilizar componentes para acelerar el desarrollo durante el examen.

### IA local

- Integrar un asistente dentro de Flutter.
- Ejecutar el modelo o motor de IA localmente.
- Funcionar sin conexión a Internet.
- Proporcionar ayuda relacionada con el funcionamiento de la aplicación.

### Despliegue

- Versión local.
- Versión desplegada en AWS.

---

# 5. Funcionalidades que no forman parte inicialmente del MVP

Las siguientes características podrán considerarse como ampliaciones futuras:

- Diagramas de secuencia dentro del editor CASE.
- Diagramas de actividades dentro del editor CASE.
- Diagramas de estados dentro del editor CASE.
- Ingeniería inversa completa.
- Generación automática total del frontend Flutter.
- Edición colaborativa en tiempo real.
- Control avanzado de versiones.
- Integración XMI completa.
- Generación por voz.
- Microservicios para cada funcionalidad.
- Soporte para múltiples lenguajes backend.
- Generación de arquitecturas empresariales complejas.

La prioridad es garantizar que funcione correctamente el flujo:

```text
Modelo conceptual
→ Validación
→ Generación Spring Boot
→ PostgreSQL
→ API REST
→ Flutter
→ IA local
```

---

# 6. Stack tecnológico

## Herramienta CASE

| Componente | Tecnología |
|---|---|
| Frontend web | Angular + TypeScript |
| Backend principal | Java + Spring Boot |
| Base de datos | PostgreSQL |
| Persistencia | Spring Data JPA / Hibernate |
| API | REST + JSON |
| Documentación de API | Swagger / OpenAPI |
| Modelado UML | UML 2.5 o superior |
| Control de versiones | Git + GitHub |
| Contenedores | Docker |
| Nube | AWS |

## Producto generado

| Componente | Tecnología |
|---|---|
| Backend generado | Spring Boot |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | PostgreSQL |
| API | REST |
| Documentación | Swagger / OpenAPI |
| Frontend móvil | Flutter + Dart |
| Asistente | IA local / modelo local |
| Despliegue | Local + AWS |

---

# 7. Arquitectura del software

## 7.1. Enfoque general

La solución utilizará:

- Arquitectura cliente-servidor.
- Monolito modular para el backend principal.
- Principios de Arquitectura Hexagonal.
- Desarrollo basado en componentes.
- API REST.
- Separación clara entre dominio, aplicación e infraestructura.
- Motor de generación desacoplado.
- Diseño preparado para crecimiento futuro.

No se utilizarán microservicios como arquitectura principal del MVP porque aumentarían innecesariamente la complejidad del proyecto.

---

# 8. Arquitectura general de la herramienta CASE

```text
┌────────────────────────────────────────────┐
│                  ANGULAR                   │
│                                            │
│ Gestión de proyectos                       │
│ Editor conceptual                         │
│ Validaciones visuales                      │
│ Gestión de generación                      │
└─────────────────────┬──────────────────────┘
                      │
                  REST / JSON
                      │
                      ▼
┌────────────────────────────────────────────┐
│             SPRING BOOT CASE               │
│                                            │
│ Project Management                         │
│ Conceptual Modeling                        │
│ Model Persistence                          │
│ Validation Engine                          │
│ Code Generation Engine                     │
│ Artifact Management                        │
└─────────────────────┬──────────────────────┘
                      │
                      ▼
                 PostgreSQL
```

---

# 9. Arquitectura Hexagonal del backend CASE

El backend principal se organizará en tres zonas principales.

## Dominio

Contiene las reglas principales de negocio.

Ejemplos:

- Project.
- ConceptualModel.
- ModelEntity.
- EntityAttribute.
- Relationship.
- Cardinality.
- GenerationRule.

El dominio no debe depender directamente de:

- Angular.
- PostgreSQL.
- AWS.
- almacenamiento físico.
- librerías externas específicas.

## Aplicación

Contiene los casos de uso.

Ejemplos:

- Crear proyecto.
- Guardar modelo.
- Validar modelo.
- Generar backend.
- Descargar artefacto.

## Infraestructura

Contiene las implementaciones tecnológicas.

Ejemplos:

- REST Controllers.
- JPA.
- PostgreSQL.
- almacenamiento de archivos.
- AWS.
- sistema de plantillas.
- ZIP del proyecto generado.

Dirección de dependencias:

```text
Infrastructure
      ↓
Application
      ↓
Domain
```

---

# 10. Módulos principales

## Project Management

Responsable de:

- Crear proyectos.
- Editarlos.
- Consultarlos.
- Eliminarlos.

## Conceptual Modeling

Responsable de:

- Entidades.
- Atributos.
- Claves.
- Relaciones.
- Cardinalidades.
- Posición de los elementos.

## Model Persistence

Responsable de:

- Guardar modelos.
- Recuperar modelos.
- Mantener su estructura.

## Validation Engine

Responsable de verificar que el modelo pueda ser convertido en software.

## Code Generation Engine

Responsable de transformar el modelo en código Spring Boot.

## Artifact Management

Responsable de:

- Empaquetar.
- almacenar.
- descargar.

los proyectos generados.

## Mobile Integration

Define la forma en que la API generada podrá ser consumida desde Flutter.

## Local AI Assistant

Responsable del funcionamiento del asistente local dentro de la aplicación móvil.

---

# 11. Modelo intermedio

Angular no generará directamente código Java.

El diagrama deberá transformarse primero en una estructura intermedia.

Ejemplo:

```json
{
  "project": "Clinica",
  "entities": [
    {
      "name": "Paciente",
      "attributes": [
        {
          "name": "id",
          "type": "Long",
          "primaryKey": true
        },
        {
          "name": "nombre",
          "type": "String"
        }
      ]
    }
  ],
  "relationships": []
}
```

Flujo:

```text
Angular
   ↓
Modelo visual
   ↓
Modelo intermedio
   ↓
Validación
   ↓
Reglas de transformación
   ↓
Templates
   ↓
Spring Boot
```

Esta separación permite cambiar el editor gráfico sin modificar completamente el generador.

---

# 12. Motor de generación

El motor deberá ser determinista.

No se dependerá exclusivamente de una IA generativa para crear el backend.

Se utilizarán reglas previamente establecidas.

Ejemplos:

```text
Entidad      → @Entity
Clave        → @Id
1:1          → @OneToOne
1:N          → @OneToMany / @ManyToOne
N:M          → @ManyToMany
```

Pipeline:

```text
Modelo conceptual
       ↓
Validation Engine
       ↓
Intermediate Model
       ↓
Transformation Rules
       ↓
Template Engine
       ↓
Source Code
       ↓
Project Packager
       ↓
backend.zip
```

---

# 13. Arquitectura del backend generado

El backend generado utilizará una arquitectura en capas sencilla.

```text
Controller
    ↓
DTO
    ↓
Service
    ↓
Repository
    ↓
Entity
    ↓
PostgreSQL
```

Estructura aproximada:

```text
src/main/java/
│
├── controller/
├── dto/
├── entity/
├── repository/
├── service/
├── mapper/
├── exception/
└── config/
```

---

# 14. DTO en Spring Boot

Los DTO permitirán evitar que las entidades de persistencia sean expuestas directamente mediante la API.

Objetivos:

- Desacoplar persistencia de comunicación.
- Controlar la información enviada.
- Evitar problemas en relaciones.
- Facilitar validaciones.
- Mejorar mantenimiento.

Flujo:

```text
Flutter / Cliente
       ↓
RequestDTO
       ↓
Controller
       ↓
Service
       ↓
Entity
       ↓
Repository
```

Respuesta:

```text
Repository
    ↓
Entity
    ↓
Service
    ↓
ResponseDTO
    ↓
Flutter
```

---

# 15. Frontend Angular

La herramienta CASE será principalmente web.

Estructura propuesta:

```text
src/app/
│
├── core/
│
├── shared/
│
├── features/
│   ├── projects/
│   ├── modeling/
│   ├── validation/
│   └── generation/
│
└── layout/
```

## Core

Servicios generales.

## Shared

Componentes reutilizables.

## Features

Módulos funcionales específicos.

Esta organización permite mantener una aplicación escalable y basada en componentes.

---

# 16. Flutter

Flutter será utilizado para desarrollar la aplicación móvil que consumirá el backend generado.

Estructura reutilizable:

```text
lib/
│
├── core/
│   ├── api/
│   ├── routing/
│   └── storage/
│
├── shared/
│
├── features/
│
└── assistant/
```

La intención es contar con una base preparada antes del examen para no comenzar desde cero.

---

# 17. Inteligencia Artificial local

El asistente debe poder funcionar sin conexión a Internet.

Arquitectura:

```text
Usuario
   ↓
Flutter
   ↓
Assistant Service
   ↓
Modelo IA local
   ↓
Respuesta
```

Condiciones:

```text
Internet disponible    → funciona
Internet no disponible → funciona
```

El asistente estará orientado principalmente a explicar al usuario cómo utilizar la aplicación.

Ejemplo:

```text
Usuario:
¿Cómo registro un paciente?

Asistente:
Ingresa a la sección Pacientes,
selecciona Nuevo Paciente,
completa los datos requeridos
y presiona Guardar.
```

---

# 18. Contexto de la IA local

La herramienta podrá generar información de contexto junto con el backend.

Ejemplo:

```json
{
  "application": "Clinica",
  "entities": [
    "Paciente",
    "Medico",
    "Consulta"
  ],
  "operations": {
    "Paciente": [
      "registrar",
      "editar",
      "consultar",
      "eliminar"
    ]
  }
}
```

Este contexto puede ser utilizado por el asistente para responder preguntas relacionadas con la aplicación concreta.

---

# 19. Despliegue

Se deben mantener dos versiones.

## Versión local

```text
Angular
   ↓
Spring Boot
   ↓
PostgreSQL
```

Ventajas:

- Respaldo.
- Demostración incluso sin Internet.
- Desarrollo.
- Pruebas.

## Versión AWS

Arquitectura prevista:

```text
Usuario
   ↓
Angular
   ↓
Spring Boot en AWS
   ↓
PostgreSQL
```

A futuro podrá utilizarse:

- AWS S3.
- Amazon RDS.
- balanceadores.
- múltiples instancias.

---

# 20. Escalabilidad

La aplicación debe ser escalable desde su arquitectura.

Principios:

- Backend stateless cuando sea posible.
- Motor generador desacoplado.
- Persistencia independiente.
- Almacenamiento de artefactos desacoplado.
- Módulos independientes.
- APIs bien definidas.

Posible evolución:

```text
                  Load Balancer
                       │
          ┌────────────┼────────────┐
          ▼            ▼            ▼
      Backend 1    Backend 2    Backend N
          │            │            │
          └────────────┼────────────┘
                       ▼
                  PostgreSQL
```

El motor de generación podrá evolucionar en el futuro hacia:

```text
Backend CASE
     ↓
Cola de trabajos
     ↓
Generation Workers
     ↓
Artifact Storage
```

Esta infraestructura no es necesaria para el MVP, pero la arquitectura deberá permitirla.

---

# 21. Metodología

Se utilizará el **Proceso Unificado de Desarrollo de Software – PUDS** con enfoque iterativo e incremental.

Cada ciclo deberá contener:

```text
Requisitos
   ↓
Análisis
   ↓
Diseño
   ↓
Implementación
   ↓
Pruebas
```

---

# 22. Ciclos de desarrollo

## Ciclo 1 – Núcleo de modelado

Objetivo:

Construir el editor conceptual.

Incluye:

- Proyectos.
- Modelo conceptual.
- Entidades.
- Atributos.
- Tipos.
- Claves.
- Relaciones.
- Cardinalidades.
- Guardado.

Resultado:

Debe ser posible representar correctamente un diseño conceptual completo.

---

## Ciclo 2 – Generación automática

Objetivo:

Transformar el modelo en software.

Incluye:

- Validación.
- Modelo intermedio.
- Reglas de transformación.
- Templates.
- Entities.
- DTO.
- Repository.
- Service.
- Controller.
- PostgreSQL.
- Swagger.
- Descarga ZIP.

Resultado:

```text
Modelo
  ↓
Generar
  ↓
Descargar
  ↓
Compilar
  ↓
Ejecutar
  ↓
Swagger
```

sin tener que corregir manualmente el proyecto.

---

## Ciclo 3 – Móvil, IA y despliegue

Incluye:

- Flutter.
- Integración REST.
- Base móvil reutilizable.
- Asistente IA local.
- Contexto local.
- Funcionamiento offline.
- AWS.
- Pruebas finales.
- Corrección de errores.
- Documentación final.

---

# 23. Casos de uso iniciales

| ID | Caso de uso | Actor | Plataforma | Ciclo |
|---|---|---|---|---|
| CU1 | Gestionar proyecto | Desarrollador | Web | C1 |
| CU2 | Crear y editar modelo conceptual | Desarrollador | Web | C1 |
| CU3 | Gestionar entidades | Desarrollador | Web | C1 |
| CU4 | Gestionar atributos y claves | Desarrollador | Web | C1 |
| CU5 | Gestionar relaciones y cardinalidades | Desarrollador | Web | C1 |
| CU6 | Guardar y recuperar modelo | Desarrollador | Web | C1 |
| CU7 | Validar modelo conceptual | Desarrollador | Web | C2 |
| CU8 | Generar backend Spring Boot | Desarrollador | Web | C2 |
| CU9 | Descargar backend generado | Desarrollador | Web | C2 |
| CU10 | Probar API generada | Desarrollador | Backend/Web | C2 |
| CU11 | Utilizar funcionalidades del negocio | Usuario final | Móvil | C3 |
| CU12 | Consultar asistente IA local | Usuario final | Móvil | C3 |

---

# 24. Actores

## Desarrollador / Modelador

Usuario principal de la herramienta CASE.

Responsabilidades:

- Crear proyecto.
- Diseñar el modelo.
- Validarlo.
- Generar código.
- Descargar backend.
- Probar servicios.

## Usuario final móvil

Utiliza la aplicación generada para realizar las operaciones del negocio y consultar el asistente.

---

# 25. Modelo de datos básico de la herramienta

## Project

```text
id
name
description
createdAt
updatedAt
status
```

## ConceptualModel

```text
id
projectId
name
version
status
createdAt
updatedAt
```

## ModelEntity

```text
id
modelId
name
positionX
positionY
```

## EntityAttribute

```text
id
entityId
name
dataType
primaryKey
nullable
unique
```

## Relationship

```text
id
modelId
sourceEntityId
targetEntityId
name
sourceCardinality
targetCardinality
```

## GenerationJob

```text
id
modelId
status
startedAt
finishedAt
errorMessage
```

## GeneratedArtifact

```text
id
generationJobId
fileName
location
createdAt
```

---

# 26. Relaciones generales

```text
Project
   1
   │
   N
ConceptualModel
   │
   ├── 1:N → ModelEntity
   │              │
   │              └── 1:N → EntityAttribute
   │
   └── 1:N → Relationship

ConceptualModel
   │
   └── 1:N → GenerationJob
                     │
                     └── 1:1 → GeneratedArtifact
```

---

# 27. Pruebas

## Pruebas unitarias

- Validación de entidades.
- Validación de atributos.
- Validación de cardinalidades.
- Conversión de tipos.
- Reglas del generador.
- Construcción del modelo intermedio.

## Pruebas de integración

```text
Angular ↔ Spring Boot
Spring Boot ↔ PostgreSQL
Generador ↔ Sistema de archivos
Flutter ↔ Backend generado
```

## Pruebas funcionales

- Crear proyecto.
- Crear modelo.
- Guardar.
- Recuperar.
- Validar.
- Generar.
- Descargar.
- Ejecutar backend.
- Consumir API.

## Pruebas del código generado

Se deben probar diferentes modelos con:

- 1:1.
- 1:N.
- N:M.
- múltiples entidades.
- múltiples relaciones.

Se comprobará:

- Compilación.
- Ejecución.
- PostgreSQL.
- CRUD.
- Swagger.
- Relaciones.
- Serialización.
- Manejo de errores.

## Prueba offline

Desconectar completamente el dispositivo y verificar que el asistente local continúe funcionando.

---

# 28. Criterios de aceptación del backend generado

Un backend se considera válido únicamente cuando:

- Se genera automáticamente.
- No requiere corregir manualmente su estructura.
- Contiene las clases esperadas.
- Compila.
- Spring Boot inicia.
- Se conecta a PostgreSQL.
- Las entidades están correctamente mapeadas.
- Las relaciones funcionan.
- Los CRUD responden.
- Swagger funciona.
- Los datos se almacenan correctamente.

---

# 29. Definition of Done

Una funcionalidad se considera terminada cuando:

- Está implementada.
- Compila.
- Cumple sus requisitos.
- Está validada.
- Tiene manejo básico de errores.
- Ha sido probada.
- No rompe funcionalidades existentes.
- Está integrada.
- Está documentada.
- Está versionada en Git.

---

# 30. Calidad del software

La calidad se abordará mediante:

- Pruebas unitarias.
- Pruebas de integración.
- Pruebas funcionales.
- Validación de datos.
- Pruebas de regresión.
- Manejo de excepciones.
- Arquitectura modular.
- Documentación.
- Evidencia de pruebas.

El proyecto no debe considerarse terminado solo porque "funciona en una computadora".

---

# 31. Fundamentación teórica requerida

La documentación deberá contener como mínimo:

## Ingeniería de Software Asistida por Computadora

- CASE.
- AGS.
- Automatización.
- Modelado.
- Generación de código.

## Desarrollo Basado en Componentes

- Componentes.
- Reutilización.
- Acoplamiento.
- Modularidad.
- Escalabilidad.

## Arquitectura de Software

- Cliente-servidor.
- Arquitectura en capas.
- Arquitectura Hexagonal.
- APIs REST.
- Comunicación entre componentes.
- Conceptos como sockets y RMI según las indicaciones académicas.

## Desarrollo Dirigido por Modelos

- Modelo conceptual.
- Modelo intermedio.
- Transformación modelo-código.
- Reglas de generación.

## UML

- UML 2.5 o superior.
- Casos de uso.
- Clases.
- Secuencia.
- Componentes.
- Actividades cuando correspondan.

## Spring Boot

- Arquitectura.
- REST.
- Spring Data JPA.
- DTO.
- Dependencias.
- Swagger.

## Inteligencia Artificial

- IA en desarrollo de software.
- IA como asistente.
- IA local.
- Funcionamiento offline.

## PUDS

- Requisitos.
- Análisis.
- Diseño.
- Implementación.
- Pruebas.
- Ciclos evolutivos.

## Calidad, productividad, innovación y facilidad de uso

Relacionados directamente con el producto desarrollado.

---

# 32. Estructura general de la documentación

## 1. PERFIL

### 1.1. Introducción

### 1.2. Planteamiento del problema

### 1.3. Objetivo general

### 1.4. Objetivos específicos

### 1.5. Alcance

### 1.6. Justificación

---

## 2. FUNDAMENTACIÓN TEÓRICA

### 2.1. Herramientas asistidas por computadora / CASE / AGS

### 2.2. Desarrollo de Software Basado en Componentes

### 2.3. Arquitectura de Software

### 2.4. Desarrollo Dirigido por Modelos y Generación Automática

### 2.5. Inteligencia Artificial en el Desarrollo del Software

### 2.6. Proceso Unificado de Desarrollo de Software – PUDS

### 2.7. UML Aplicado al Proyecto

### 2.8. Spring Boot y DTO

### 2.9. Calidad, Productividad, Innovación y Facilidad de Uso

---

## 3. PROCESO DE DESARROLLO

### 3.1. Captura de requisitos

- Actores.
- Casos de uso.
- Priorización.
- Descripción detallada de casos de uso.
- Prototipos.

### 3.2. Análisis

#### 3.2.1. Modelo de contexto

#### 3.2.2. Modelo de arquitectura

- Paquetes.
- Módulos.
- Relación módulos–casos de uso.
- Vista por paquetes.

#### 3.2.3. Modelo de datos

- Clases principales.
- Relaciones.
- Modelo de persistencia.

#### 3.2.4. Modelo de lógica

- Diagramas de secuencia.
- Flujo del generador.
- Validación.
- Descarga.

### 3.3. Diseño

#### 3.3.1. Arquitectura física

#### 3.3.2. Arquitectura lógica

#### 3.3.3. Motor de generación

#### 3.3.4. Diseño del backend generado

#### 3.3.5. Diseño Angular

#### 3.3.6. Diseño Flutter e IA local

### 3.4. Implementación

- Lenguajes.
- Frameworks.
- Base de datos.
- Librerías.
- Herramientas.
- Git.
- Docker.
- AWS.

### 3.5. Pruebas

- Plan de pruebas.
- Casos de prueba.
- Pruebas unitarias.
- Integración.
- Funcionales.
- UI.
- Generador.
- Offline.
- Registro de defectos.
- Re-pruebas.

---

## 4. CONCLUSIONES

Resultados obtenidos y cumplimiento de los objetivos.

---

## 5. RECOMENDACIONES

Mejoras y evolución futura.

---

## 6. BIBLIOGRAFÍA

Bibliografía utilizada en:

- Ingeniería de Software.
- CASE.
- PUDS.
- UML.
- Spring Boot.
- Arquitectura.
- IA.

---

## 7. ANEXOS

- Repositorio GitHub.
- Enlaces de despliegue.
- Capturas.
- Resultados de pruebas.
- Evidencias.
- Manuales.
- Documentación API.

---

# 33. Evaluación del parcial

La evaluación contempla cuatro grandes fases.

## Fase 1 – Documentación

La documentación deberá presentarse en PDF y contener la fundamentación y todo el proceso de Ingeniería de Software.

Debe existir evidencia de:

- Requisitos.
- Análisis.
- Diseño.
- Implementación.
- Pruebas.
- Uso de IA.
- Arquitectura.
- Modelado.
- Proceso seguido.

## Fase 2 – Producto

Se deberá demostrar que el software:

- Hace lo acordado.
- Está terminado.
- Está probado.
- No presenta errores críticos.
- Puede considerarse un producto funcional.

## Fase 3 – Aseguramiento

Debe existir evidencia de calidad:

- Pruebas.
- Validaciones.
- Registro de errores.
- Correcciones.
- Re-pruebas.
- Garantía de funcionamiento.

## Fase 4 – Autoría

Los integrantes deberán demostrar que son autores del software.

Todos deberán comprender:

```text
Modelo
 ↓
Validación
 ↓
Modelo intermedio
 ↓
Generador
 ↓
Spring Boot
 ↓
PostgreSQL
 ↓
REST
 ↓
Flutter
 ↓
IA local
```

No debe existir una dependencia total del conocimiento de una sola persona.

---

# 34. Evidencias del proceso de desarrollo

Durante el desarrollo deberán conservarse:

- Capturas de avances.
- Versiones de diagramas.
- Commits.
- Pull Requests si se utilizan.
- Pruebas.
- Bugs encontrados.
- Soluciones aplicadas.
- Decisiones arquitectónicas.
- Versiones desplegadas.
- Evidencias de funcionamiento.
- Evidencias de IA utilizada durante el desarrollo.

La documentación debe evolucionar junto con el software y no escribirse únicamente al final.

---

# 35. Principios del proyecto

## Primero funcional, después avanzado

La prioridad será:

```text
MVP estable
   ↓
Calidad
   ↓
Mejoras
   ↓
Características avanzadas
```

## El generador debe ser genérico

La herramienta no debe estar preparada solamente para un ejemplo específico.

Debe ser capaz de recibir distintos contextos:

- Clínica.
- Hotel.
- Biblioteca.
- Gimnasio.
- Veterinaria.
- Comercio.
- Universidad.
- Restaurante.
- Otros.

sin modificar el código de la herramienta para cada negocio.

## Generación determinista

La generación del backend debe depender de reglas y plantillas controladas.

La IA puede ayudar, pero no debe ser el único mecanismo responsable de producir código crítico.

## Reutilización

Angular, Spring Boot y Flutter deberán organizarse de forma que los componentes principales puedan reutilizarse.

---

# 36. Simulacros previos al examen

Antes del examen se deberán realizar pruebas completas utilizando negocios diferentes.

Ejemplo de simulacro:

```text
Sistema de biblioteca
        ↓
Diseñar modelo
        ↓
Generar backend
        ↓
Ejecutarlo
        ↓
Probar Swagger
        ↓
Construir Flutter
        ↓
Probar API
        ↓
Probar IA offline
```

Posteriormente repetir con:

- Clínica.
- Hotel.
- Gimnasio.
- Veterinaria.
- Ventas.

El objetivo es demostrar que el sistema es verdaderamente genérico.

---

# 37. Ruta crítica del proyecto

La ruta de mayor prioridad es:

```text
Definir estructura del modelo
          ↓
Crear editor conceptual
          ↓
Guardar modelo
          ↓
Validar modelo
          ↓
Modelo intermedio
          ↓
Motor de generación
          ↓
Spring Boot generado
          ↓
PostgreSQL
          ↓
Swagger
          ↓
Flutter
          ↓
IA local
          ↓
AWS
          ↓
Prueba completa
```

Cualquier funcionalidad que no contribuya directamente a esta ruta tendrá una prioridad inferior mientras el MVP no esté terminado.

---

# 38. Resultado esperado

Al finalizar el proyecto se deberá contar con una herramienta capaz de transformar un diseño conceptual de base de datos en una base funcional para el desarrollo de una aplicación.

El resultado esperado puede resumirse de la siguiente manera:

```text
DISEÑAR
   ↓
VALIDAR
   ↓
GENERAR
   ↓
EJECUTAR
   ↓
CONSUMIR
   ↓
ASISTIR
```

Donde:

- **Diseñar** corresponde al modelo conceptual.
- **Validar** garantiza que el modelo sea consistente.
- **Generar** produce el backend Spring Boot.
- **Ejecutar** permite levantar la API y PostgreSQL.
- **Consumir** corresponde al frontend Flutter.
- **Asistir** corresponde al agente de IA local.

---

# 39. Resumen ejecutivo

El proyecto desarrollará una herramienta CASE web enfocada exclusivamente en el modelado conceptual de bases de datos. El usuario podrá representar entidades, atributos, claves, relaciones y cardinalidades mediante una interfaz desarrollada en Angular.

El modelo será almacenado y transformado en una representación intermedia que será validada antes de iniciar la generación. Un motor de generación basado en reglas y plantillas producirá automáticamente un backend Spring Boot organizado mediante entidades, DTO, repositorios, servicios y controladores REST, configurado para utilizar PostgreSQL y documentado con Swagger/OpenAPI.

El backend producido será utilizado para desarrollar una aplicación móvil en Flutter. La aplicación incorporará un asistente de inteligencia artificial local capaz de proporcionar ayuda incluso cuando el dispositivo no disponga de conexión a Internet.

La herramienta CASE utilizará una arquitectura cliente-servidor con un backend organizado como monolito modular siguiendo principios de Arquitectura Hexagonal, permitiendo mantener un MVP sencillo de desplegar pero preparado para una futura evolución y escalabilidad.

El desarrollo seguirá PUDS mediante ciclos iterativos que contemplarán captura de requisitos, análisis, diseño, implementación y pruebas. El proyecto deberá mantener documentación técnica, evidencias del proceso de desarrollo, pruebas del software generado, una versión local y una versión desplegada en AWS.

El criterio principal de éxito será poder recibir un modelo conceptual correspondiente a cualquier negocio, representarlo en la herramienta, generar automáticamente un backend funcional, consumirlo desde Flutter y demostrar el funcionamiento del asistente de IA local.
