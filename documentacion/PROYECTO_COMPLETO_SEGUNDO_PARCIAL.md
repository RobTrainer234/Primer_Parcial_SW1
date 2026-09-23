# Proyecto completo segundo parcial

**Proyecto:** Herramienta CASE para modelado conceptual y generación de backend Spring Boot
**Autor:** Víctor Adolfo Lozada Arias
**Materia:** Software I
**Formato:** Documento Markdown preparado para transferencia posterior a Word
**Fecha de referencia técnica:** 2026-09-21

> **Nota para edición final en Word:** este documento conserva la estructura académica solicitada por el documento base `2do_parcial_SW1_LOZADA_ARIAS_VICTOR_ADOLFO.docx`: fundamentación teórica, PUDS, manual de usuario, bibliografía y anexos. El contenido está en Markdown para facilitar revisión, trazabilidad y posterior copiado/formateo en Word. Las tablas y diagramas textuales pueden convertirse luego en tablas nativas, imágenes o diagramas UML.

## Control de alcance y honestidad técnica

Este documento usa como fuentes técnicas principales:

- `2do_parcial_SW1_LOZADA_ARIAS_VICTOR_ADOLFO.docx` como esquema académico.
- `GUIA DEL PROYECTO.txt` como guía funcional y técnica del proyecto.
- `Resumen_Completo_Proyecto_Software_I.md` como resumen de alcance esperado.
- Evidencia del repositorio: código fuente, README de módulos, documentación de requisitos, evidencias de fases, Docker Compose, configuración Spring Boot, `pom.xml`, `package.json`, `pubspec.yaml` y migración SQL.

El documento no presenta como terminado aquello que la evidencia marca como parcial, demo, opt-in o pendiente. En particular:

- El backend actual es **Spring Boot 3.3.5, Java 21, PostgreSQL y Flyway**.
- El frontend actual de este repositorio es **Angular 18**, no React.
- La aplicación móvil actual es **Flutter/Dart** con CRUD genérico y asistente local offline separado de Qwen.
- La ejecución local se apoya en **Docker Compose** para PostgreSQL, backend y frontend.
- Qwen web es **opt-in**: no se habilita por defecto.
- El asistente móvil local es **offline y separado**: usa assets locales y no invoca Qwen.
- La colaboración evidenciada usa **HTTP + SSE**, no WebSocket.
- AWS real, S3 real, despliegue productivo, XMI lossless, autenticación productiva y generación avanzada completa se documentan como demo/parcial/planeado según corresponda.

---

# Capítulo 1. Fundamentación teórica

## 1.1 Ingeniería de software y herramientas CASE

Una herramienta CASE (*Computer-Aided Software Engineering*) automatiza o asiste actividades del proceso de desarrollo de software. Su propósito no es reemplazar el análisis, sino reducir errores repetitivos, mantener trazabilidad y acelerar la transición entre modelos y artefactos ejecutables.

En este proyecto, la herramienta CASE se orienta a un flujo académico concreto:

```text
Diseño conceptual
→ representación en herramienta CASE
→ validación del modelo
→ modelo intermedio
→ generación determinista
→ backend Spring Boot
→ PostgreSQL
→ API REST / Swagger
→ consumo desde Flutter
→ asistencia local offline
```

La idea central es que un diseño conceptual de base de datos pueda transformarse en una base funcional de software, especialmente en la capa backend, disminuyendo la escritura manual de entidades, repositorios, servicios, controladores y configuración.

## 1.2 Modelado conceptual y UML

El modelado conceptual describe los elementos principales de un dominio antes de implementar la solución. En el alcance del proyecto se trabaja con conceptos cercanos a diagramas de clases o modelos entidad-relación:

- entidades o clases;
- atributos;
- claves primarias;
- tipos de datos;
- obligatoriedad;
- unicidad;
- relaciones;
- cardinalidades 1:1, 1:N y N:M.

La documentación de requisitos del repositorio amplía el alcance hacia UML 2.5 con separación entre **estado semántico** y **layout visual**. Esa separación es importante:

- El estado semántico representa el significado del modelo: entidades, atributos, relaciones y propiedades.
- El layout visual representa posiciones, geometría o presentación en pantalla.

Mover una entidad en el lienzo no debería cambiar el significado del modelo. Por eso el repositorio documenta CU5 para semántica y CU6 para layout.

## 1.3 Arquitectura cliente-servidor

La solución adopta una arquitectura cliente-servidor:

- El frontend web Angular actúa como cliente principal para modelar y generar.
- El backend Spring Boot expone servicios HTTP/JSON y persiste el modelo en PostgreSQL.
- La aplicación móvil Flutter consume un backend generado o servicios REST compatibles.
- PostgreSQL almacena los datos principales.
- Docker Compose permite levantar el entorno local completo.

Esta arquitectura es adecuada para el proyecto porque permite separar responsabilidades: interfaz, reglas de negocio, persistencia, generación de código y consumo móvil.

## 1.4 Monolito modular y arquitectura hexagonal liviana

El repositorio documenta una decisión arquitectónica de **monolito modular**. Esto evita la complejidad innecesaria de microservicios para un MVP académico y permite organizar el sistema por módulos:

- proyectos;
- modelado;
- validación;
- generación;
- artefactos;
- autenticación;
- sincronización;
- propuestas;
- despliegues;
- almacenamiento;
- configuración compartida.

También se recomienda una arquitectura hexagonal liviana por módulo, separando:

- dominio;
- aplicación;
- infraestructura;
- presentación.

En el código actual se observa esta convención en paquetes como `aplicacion`, `dominio`, `infraestructura` y `presentacion`.

## 1.5 API REST, JSON y SSE

El backend expone endpoints REST sobre HTTP. Las operaciones usan JSON para crear, consultar y modificar recursos. Para colaboración y eventos, la evidencia del repositorio muestra **Server-Sent Events (SSE)** mediante `text/event-stream`.

El contrato vigente debe describirse como:

```text
Comandos HTTP + snapshots HTTP + eventos SSE
```

No se debe afirmar WebSocket como transporte actual, porque la evidencia del código y la documentación de requisitos señalan HTTP/SSE.

## 1.6 Generación determinista de código

El motor de generación debe ser determinista: ante el mismo modelo válido, debería producir artefactos equivalentes. La guía y la arquitectura recomiendan no depender de IA generativa para producir código crítico. La generación se basa en:

1. modelo conceptual;
2. modelo intermedio;
3. reglas de transformación;
4. plantillas;
5. archivos generados;
6. empaquetado ZIP.

La implementación actual genera un backend Spring Boot CRUD básico y descarga un ZIP. La documentación del backend indica que el mapeo JPA avanzado de relaciones todavía está pendiente o no debe considerarse completo.

## 1.7 Spring Boot, PostgreSQL, Angular y Flutter

El stack actual evidenciado es:

| Componente | Tecnología actual | Evidencia |
|---|---|---|
| Backend CASE | Spring Boot 3.3.5, Java 21 | `backend-case/pom.xml` |
| Persistencia | PostgreSQL, JPA/Hibernate, Flyway | `pom.xml`, `application.yml`, migración `V1__estructura_inicial.sql` |
| API | REST/JSON, Swagger/OpenAPI | `springdoc-openapi-starter-webmvc-ui`, `/swagger-ui.html` |
| Frontend web | Angular 18 + TypeScript | `frontend-case/package.json` |
| Móvil | Flutter/Dart | `movil/pubspec.yaml`, `movil/README.md` |
| Infraestructura local | Docker Compose | `infraestructura/local/docker-compose.yml` |

## 1.8 Inteligencia artificial en el proyecto

El proyecto distingue dos superficies diferentes:

1. **Qwen web opt-in:** el backend puede intentar propuestas textuales o fotográficas mediante Qwen si se habilitan variables explícitas. Por defecto la IA textual está desactivada (`AI_TEXT_PROPOSALS_ENABLED=false`) y fotografía/Qwen también queda desactivada (`CU12_PHOTO_QWEN_ENABLED=false`).
2. **Asistente móvil local:** la app Flutter incluye un asistente offline que carga conocimiento desde assets locales y responde por coincidencia de palabras clave. No usa red ni Qwen.

Esta separación evita confundir IA remota/local configurable con el asistente offline demostrable en móvil.

## 1.9 Calidad de software e ISO/IEC 25010

ISO/IEC 25010 organiza la calidad del producto de software en características como adecuación funcional, eficiencia, compatibilidad, usabilidad, fiabilidad, seguridad, mantenibilidad y portabilidad. En este proyecto se usa como referencia para evaluar:

- si las funciones principales cubren el flujo académico;
- si los módulos son mantenibles;
- si las interfaces son usables para evaluación;
- si el sistema local puede ejecutarse de forma reproducible;
- si las limitaciones de seguridad están claramente identificadas.

---

# Capítulo 2. PUDS: Proceso Unificado de Desarrollo de Software

## 2.1 Introducción al PUDS aplicado

El PUDS organiza el desarrollo en disciplinas: requisitos, análisis, diseño, implementación y pruebas. Para este proyecto se adapta a un contexto académico incremental, donde cada fase del repositorio documenta avances, evidencia y límites.

## 2.2 Requisitos

### 2.2.1 Objetivo general

Desarrollar una herramienta CASE que permita modelar conceptualmente una base de datos, validar el modelo, generar un backend Spring Boot ejecutable con PostgreSQL y consumirlo desde una aplicación Flutter con asistencia local offline.

### 2.2.2 Objetivos específicos

- Gestionar proyectos de modelado.
- Crear modelos conceptuales con entidades, atributos y relaciones.
- Validar reglas mínimas antes de generar.
- Generar un backend Spring Boot CRUD básico.
- Descargar el backend generado como ZIP.
- Ejecutar el entorno local con PostgreSQL.
- Exponer Swagger/OpenAPI.
- Consumir APIs REST desde Flutter.
- Incluir un asistente móvil offline.
- Documentar funcionalidades parciales, demo y planeadas sin sobredimensionar el estado actual.

### 2.2.3 Requisitos funcionales principales

| Código | Requisito | Estado honesto actual |
|---|---|---|
| RF1 | Crear, consultar, editar y eliminar proyectos | Implementado en backend; visible desde frontend para flujo principal. |
| RF2 | Crear modelos conceptuales por proyecto | Implementado. |
| RF3 | Crear entidades con nombre y posición | Implementado. |
| RF4 | Crear atributos con tipo, clave primaria, obligatoriedad y unicidad | Implementado. |
| RF5 | Crear relaciones con cardinalidades y tipo UML binario | Implementado: asociación, agregación, composición, generalización, dependencia y realización. |
| RF6 | Validar modelo antes de generar | Implementado con reglas básicas. |
| RF7 | Generar backend Spring Boot | Implementado como generación CRUD básica. |
| RF8 | Descargar ZIP generado | Implementado. |
| RF9 | Exponer Swagger/OpenAPI | Configurado por Springdoc en backend CASE y esperado en backend generado. |
| RF10 | Consumir backend desde Flutter | Implementado como cliente genérico/demo para endpoints REST simples. |
| RF11 | Asistente local offline móvil | Implementado de forma simple por assets y palabras clave. |
| RF12 | Despliegue local | Implementado con Docker Compose. |
| RF13 | Despliegue AWS | Planeado/demo; no hay evidencia de AWS real productivo. |
| RF14 | Qwen para propuestas | Opt-in; desactivado por defecto. |
| RF15 | XMI | Parcial, no lossless. |
| RF16 | Colaboración HTTP/SSE | Demo/parcial con comandos, snapshot, presencia y conflictos. |

### 2.2.4 Requisitos no funcionales

| Requisito | Descripción | Estado |
|---|---|---|
| Mantenibilidad | Código organizado por módulos y capas. | Parcialmente implementado con paquetes por dominio. |
| Portabilidad local | Levantar en equipos con Docker. | Compose disponible. |
| Usabilidad académica | Permitir demostrar flujo en examen. | Frontend mínimo funcional; editor visual completo pendiente. |
| Trazabilidad | Relacionar guía, CU, módulos y evidencia. | Documentación CU1-CU18 disponible. |
| Seguridad | No presentar mecanismos demo como seguridad productiva. | Limitación documentada. |
| Determinismo | Generar código por reglas/plantillas. | Motor determinista inicial; evidencia acotada. |
| Interoperabilidad | Swagger/OpenAPI y REST. | Configurado. |

## 2.3 Casos de uso CU1-CU18 y trazabilidad

La documentación del repositorio define dieciocho casos de uso. Esta tabla consolida trazabilidad, módulo, estado y comentario técnico.

| CU | Caso de uso | Módulo principal | Estado actual | Observación |
|---|---|---|---|---|
| CU1 | Autenticarse y gestionar sesión | Autenticación | Parcial/demo | `POST /auth/login`, `/demo-login`, `/logout`. No es autenticación productiva completa. |
| CU2 | Crear, listar y recuperar proyectos/versiones | Proyectos | Implementado/demo | Rutas `/proyectos` y compatibilidad `/projects`. Versiones expuestas de forma académica. |
| CU3 | Gestionar permisos, invitaciones, miembros y capacidades | Administración de proyecto | Demo/parcial | Endpoints compatibles; varios estados en memoria. |
| CU4 | Crear diagramas/vistas y administrar permisos de diagrama | Diagramas/vistas | Demo/parcial | Maneja colaboración, acceso e historial de vista en capa compatible. |
| CU5 | Editar estado semántico UML 2.5 | Modelado | Perfil binario soportado | Entidades, atributos, operaciones y seis relaciones UML binarias persistidas. No incluye clases de asociación ni asociaciones n-arias. |
| CU6 | Editar layout, geometría y atajos web | Modelado/frontend | Parcial | Posiciones `posicionX/posicionY`; canvas/atajos avanzados pendientes. |
| CU7 | Crear propuestas desde texto y revisar decisión | Propuestas | Demo/opt-in | Manual y Qwen opt-in; aceptación no muta automáticamente el modelo. |
| CU8 | Transcribir voz local y convertir en propuesta | ASR local | Pendiente opt-in | Endpoint previsto; requiere sidecar `faster-whisper` externo. |
| CU9 | Crear propuesta desde foto/Qwen y revisarla | Qwen/foto | Placeholder opt-in | Puede responder `QWEN_UNCONFIGURED`; no prueba extracción real por defecto. |
| CU10 | Sincronizar comandos, snapshots y eventos SSE | Sincronización | Demo/parcial | HTTP + SSE; no WebSocket. |
| CU11 | Publicar y visualizar presencia | Sincronización | Demo/parcial | Presencia local/no distribuida. |
| CU12 | Listar, consultar y resolver conflictos | Sincronización | Demo/parcial | Conflictos por revisión obsoleta; no merge semántico avanzado. |
| CU13 | Consumir desde móvil descriptor-driven con cola offline | Flutter | Parcial | Descriptor y cola en memoria; no persiste cola al cerrar app. |
| CU14 | Importar/exportar XMI con preview/confirmación | XMI | Parcial | No lossless. |
| CU15 | Gestionar generación, runs y artefactos | Generación | Implementado demo | Backend CRUD básico y ZIP; relaciones JPA avanzadas limitadas. |
| CU16 | Gestionar deployments | Despliegues | Demo | No ejecuta infraestructura real. |
| CU17 | Consultar salud, ejecutar comandos y conservar evidencia | Operación | Implementado en parte | `/actuator/health` y comandos documentados; depende del entorno. |
| CU18 | Configurar almacenamiento local/S3/Floci | Almacenamiento | Local implementado; S3/Floci opt-in | Estado local disponible; S3 real no evidenciado. |

## 2.4 Análisis

### 2.4.0 Análisis de Arquitectura - Identificar Paquetes (2.2.1.1)

Los paquetes arquitectónicos estructuran el sistema agrupando los casos de uso (CU-01 a CU-18) y los módulos del backend Spring Boot, frontend web Angular y app móvil Flutter según cohesión funcional:

| Diagrama del Paquete | Descripción Funcional y de Dominio | Casos de Uso Cubiertos |
| :--- | :--- | :--- |
| **Gestión de Acceso y Proyectos** | Agrupa las funcionalidades relacionadas con la autenticación de usuarios, gestión de sesiones y administración integral del espacio de trabajo. Incluye el inicio y cierre de sesión, soporte de credenciales de demostración (`SEED_DEMO_USERS`), creación y consulta de proyectos de modelado, control de miembros, invitaciones colaborativas y asignación de capacidades administrativas sobre el proyecto. | CU-01, CU-02, CU-03 |
| **Modelado Semántico y Visual UML** | Constituye el núcleo del modelado conceptual del sistema. Agrupa la creación, edición y persistencia del estado semántico UML 2.5 (entidades, atributos con tipos y restricciones, relaciones binarias con cardinalidad y navegabilidad), manteniendo una estricta separación con el layout geométrico (coordenadas espaciales, canvas interactivo y atajos de teclado web), así como la administración de vistas y diagramas con sus permisos de acceso. | CU-04, CU-05, CU-06 |
| **Propuestas y Asistencia Inteligente** | Agrupa las capacidades de asistencia aumentada para el modelado mediante procesamiento de lenguaje natural y visión computacional. Incluye la formulación de propuestas de cambio a partir de descripciones textuales, transcripción de voz local mediante modelos ASR (`faster-whisper`), y extracción de diagramas conceptuales desde fotografías usando visión (`Qwen`), garantizando siempre un flujo de revisión, decisión y aceptación humana previa a la alteración del modelo. | CU-07, CU-08, CU-09 |
| **Colaboración y Sincronización en Tiempo Real** | Agrupa los mecanismos que posibilitan la concurrencia multiusuario sobre los diagramas. Gestiona el intercambio determinista de comandos atómicos y snapshots vía HTTP, la difusión de eventos unidireccionales en tiempo real mediante Server-Sent Events (SSE), el seguimiento de presencia de participantes activos, la detección y resolución explícita de conflictos de sincronización, y el soporte a clientes móviles Flutter con capacidades de operación offline. | CU-10, CU-11, CU-12, CU-13 |
| **Generación de Código y Artefactos** | Agrupa las capacidades de transformación de modelos a código fuente ejecutable e intercambio de especificaciones. Incluye la validación de consistencia y restricciones semánticas previas, el motor de generación determinista de backend Spring Boot con persistencia JPA/PostgreSQL y documentación Swagger/OpenAPI empaquetado en archivos ZIP descargables, así como los mecanismos de importación y exportación parcial de modelos bajo el estándar XMI. | CU-14, CU-15 |
| **Operación, Despliegue y Almacenamiento** | Agrupa la infraestructura técnica, la verificación de salud del sistema y la persistencia física de entregables. Gestiona la consulta de telemetría y métricas operativas (`Spring Boot Actuator`), ejecución de suites de pruebas automáticas, administración de estados de despliegue (deployments), y configuración del repositorio de almacenamiento de artefactos (PostgreSQL BLOB por defecto, con soporte extensible opt-in para Amazon S3 y almacenamiento compatible Floci). | CU-16, CU-17, CU-18 |

### 2.4.1 Actores

| Actor | Responsabilidad |
|---|---|
| Usuario autenticado/demo | Ingresar al sistema y operar proyectos. |
| Modelador | Crear entidades, atributos, relaciones y generar backend. |
| Administrador de proyecto | Gestionar permisos, miembros e invitaciones. |
| Administrador de diagrama | Gestionar colaboración y acceso a vistas. |
| Cliente web Angular | Interfaz para operar el flujo CASE. |
| Cliente móvil Flutter | Consumir APIs REST y ofrecer asistente offline. |
| Generador | Transformar modelo conceptual en proyecto Spring Boot. |
| Operador/evaluador | Levantar servicios, ejecutar pruebas y revisar evidencia. |
| Servicio Qwen | Integración opt-in para propuestas. |
| ASR local | Integración opt-in de voz local. |

### 2.4.2 Modelo conceptual del dominio

El núcleo de datos persistente observado en la migración inicial contiene:

- `proyectos`;
- `modelos_conceptuales`;
- `entidades_modelo`;
- `atributos_entidad`;
- `relaciones_modelo`;
- `trabajos_generacion`;
- `artefactos_generados`.

Relación conceptual principal:

```text
Proyecto 1 ── N ModeloConceptual
ModeloConceptual 1 ── N Entidad
Entidad 1 ── N Atributo
ModeloConceptual 1 ── N Relación
TrabajoGeneracion N ── 1 ModeloConceptual
TrabajoGeneracion 1 ── N ArtefactoGenerado
```

### 2.4.3 Reglas de validación esperadas

La guía exige detectar como mínimo:

- entidades sin nombre;
- entidades duplicadas;
- atributos duplicados;
- entidades sin clave primaria;
- relaciones incompletas;
- tipos de datos no soportados;
- nombres incompatibles con generación Java;
- referencias a entidades inexistentes.

La implementación actual declara validación básica antes de generación. Para defensa académica se recomienda demostrar al menos una validación exitosa y una validación fallida.

## 2.5 Diseño

### 2.5.1 Vista de alto nivel

```text
┌──────────────────────────┐
│ Frontend Angular CASE     │
│ Proyecto, modelo, generar │
└─────────────┬────────────┘
              │ REST/JSON + SSE
              ▼
┌──────────────────────────┐
│ Backend CASE Spring Boot  │
│ proyectos/modelado/etc.   │
└─────────────┬────────────┘
              │ JPA/Flyway
              ▼
┌──────────────────────────┐
│ PostgreSQL                │
└─────────────┬────────────┘
              │ modelo válido
              ▼
┌──────────────────────────┐
│ Motor de generación       │
│ plantillas/reglas/ZIP     │
└─────────────┬────────────┘
              │ descarga/ejecución
              ▼
┌──────────────────────────┐
│ Backend generado          │
│ Spring Boot + PostgreSQL  │
└─────────────┬────────────┘
              │ REST
              ▼
┌──────────────────────────┐
│ App Flutter reutilizable  │
│ CRUD + asistente offline  │
└──────────────────────────┘
```

### 2.5.2 Diseño del backend CASE

Paquete base evidenciado:

```text
backend-case/src/main/java/bo/edu/proyecto/caseapp/
```

Módulos relevantes:

```text
almacenamiento/
autenticacion/
compartido/
configuracion/
despliegues/
generacion/
modelado/
proyectos/
sincronizacion/
validacion/
```

Responsabilidades:

- `proyectos`: gestión de proyectos y endpoints en español/compatibilidad inglés.
- `modelado`: modelos, entidades, atributos, relaciones y XMI parcial.
- `validacion`: validación del modelo.
- `generacion`: generación de backend y artefactos.
- `sincronizacion`: snapshots, comandos, eventos SSE, presencia y conflictos.
- `autenticacion`: login/demo/logout académico.
- `despliegues`: CRUD demo de deployments.
- `almacenamiento`: estado local/S3/Floci.

### 2.5.3 Diseño del frontend Angular

El frontend actual está concentrado en una pantalla principal funcional:

```text
frontend-case/src/app/componente-raiz.ts
frontend-case/src/app/componente-raiz.html
frontend-case/src/app/nucleo/modelos-case.ts
frontend-case/src/app/nucleo/servicio-api-case.ts
```

Responsabilidades implementadas según README:

- crear proyecto;
- crear modelo conceptual;
- crear entidad;
- crear atributos;
- validar modelo;
- generar backend;
- descargar ZIP.

Pendiente documentado:

- editor visual con canvas completo;
- relaciones desde interfaz avanzada;
- edición/eliminación completa desde UI;
- mejoras visuales finales.

### 2.5.4 Diseño móvil Flutter

Estructura actual:

```text
movil/lib/main.dart
movil/lib/nucleo/
movil/lib/funcionalidades/crud/
movil/lib/funcionalidades/asistente/
movil/assets/contexto_aplicacion.json
movil/assets/conocimiento_asistente.json
```

La app móvil permite:

- configurar URL base del backend;
- usar cliente HTTP genérico;
- operar una pantalla CRUD reutilizable con entidad ejemplo `clientes`;
- usar un asistente offline por palabras clave.

Limitaciones documentadas:

- no incluye autenticación;
- no persiste URL base entre sesiones;
- la entidad demo es `clientes`;
- no genera Flutter automáticamente desde el modelo;
- el asistente no usa red ni IA remota.

### 2.5.5 Diseño de despliegue local

El Compose local declara:

| Servicio | Puerto | Descripción |
|---|---:|---|
| `base-datos-case` | 5432 | PostgreSQL 16, base `proyecto_case`. |
| `backend-case` | 8080 | Spring Boot conectado a PostgreSQL. |
| `frontend-case` | 4200 | Angular servido por nginx. |

URLs esperadas:

```text
Frontend: http://localhost:4200
Backend:  http://localhost:8080
Swagger:  http://localhost:8080/swagger-ui.html
Postgres: jdbc:postgresql://localhost:5432/proyecto_case
```

Qwen no se levanta en Compose. Si se usa Docker Model Runner local, el backend apunta al host por:

```text
http://host.docker.internal:12434/engines/v1/chat/completions
```

## 2.6 Implementación

### 2.6.1 Backend principal

Tecnologías evidenciadas:

- Java 21.
- Spring Boot 3.3.5.
- Spring Web.
- Spring Data JPA.
- Spring Validation.
- Spring Actuator.
- Flyway.
- PostgreSQL driver.
- Springdoc OpenAPI UI.

Configuración relevante:

```yaml
server.port: ${PUERTO_BACKEND:8080}
spring.datasource.url: ${BASE_DATOS_URL:jdbc:postgresql://localhost:5432/proyecto_case}
spring.datasource.username: ${BASE_DATOS_USUARIO:case_user}
spring.datasource.password: ${BASE_DATOS_CLAVE:case_password}
spring.jpa.hibernate.ddl-auto: validate
spring.flyway.enabled: true
springdoc.swagger-ui.path: /swagger-ui.html
caseapp.ai.text-proposals.enabled: ${AI_TEXT_PROPOSALS_ENABLED:false}
```

### 2.6.2 API general actual

#### Proyectos y modelado básico

```http
POST   /proyectos
GET    /proyectos
GET    /proyectos/{id}
PUT    /proyectos/{id}
DELETE /proyectos/{id}

POST   /proyectos/{proyectoId}/modelos
GET    /proyectos/{proyectoId}/modelos
GET    /proyectos/{proyectoId}/modelo
GET    /modelos/{modeloId}

POST   /modelos/{modeloId}/entidades
PUT    /entidades/{entidadId}
DELETE /entidades/{entidadId}

POST   /entidades/{entidadId}/atributos
PUT    /atributos/{atributoId}
DELETE /atributos/{atributoId}

POST   /modelos/{modeloId}/relaciones
PUT    /relaciones/{relacionId}
DELETE /relaciones/{relacionId}
```

#### Validación y generación

```http
POST /modelos/{modeloId}/validacion
GET  /modelos/{modeloId}/validacion

GET  /generation/targets
GET  /generation/profiles
POST /modelos/{modeloId}/generaciones
POST /generation/runs
GET  /generaciones/{generacionId}
GET  /generation/runs/{generacionId}
GET  /generation/runs/{generacionId}/artifacts
GET  /generaciones/{generacionId}/artefacto
```

#### Autenticación demo

```http
POST /auth/login
POST /auth/demo-login
POST /auth/logout
```

#### Compatibilidad `/projects`

```http
POST /projects
GET  /projects
GET  /projects/{id}
GET  /projects/{id}/model
GET  /projects/{id}/versions
GET  /projects/{id}/versions/{revision}
GET  /projects/{id}/mobile/descriptor
```

#### Administración, colaboración y permisos demo

```http
POST   /projects/{projectId}/commands
GET    /projects/{projectId}/invitations
GET    /projects/{projectId}/members
GET    /projects/{projectId}/account-suggestions
PATCH  /projects/{projectId}/members/{userId}
PUT    /projects/{projectId}/members/{userId}/capabilities/create-diagram
GET    /projects/{projectId}/permission-history
GET    /projects/{projectId}/permissions/history

POST   /projects/{projectId}/diagrams
POST   /projects/{projectId}/diagrams/{viewId}/collaboration
PUT    /projects/{projectId}/diagrams/{viewId}/collaborators/{userId}
DELETE /projects/{projectId}/diagrams/{viewId}/collaborators/{userId}
PUT    /projects/{projectId}/diagrams/{viewId}/administrator
GET    /projects/{projectId}/diagrams/{viewId}/access
GET    /projects/{projectId}/diagrams/{viewId}/permission-history
GET    /projects/{projectId}/diagrams/{viewId}/permissions/history
```

#### Sincronización HTTP/SSE, presencia y conflictos

```http
GET  /modelos/{modeloId}/sync/snapshot
POST /modelos/{modeloId}/sync/commands
GET  /modelos/{modeloId}/sync/events        # text/event-stream

POST /projects/{projectId}/sync/presence?modelId={modelId}
GET  /projects/{projectId}/sync/presence?modelId={modelId}
GET  /sync/events?modelId={modelId}         # text/event-stream
GET  /projects/{projectId}/sync/events?modelId={modelId}

GET  /projects/{projectId}/models/{modelId}/sync/conflicts
GET  /projects/{projectId}/models/{modelId}/sync/conflicts/{operationId}
POST /projects/{projectId}/models/{modelId}/sync/conflicts/{operationId}/resolve
```

#### Propuestas, Qwen opt-in, ASR y foto

```http
POST /projects/{projectId}/proposals
GET  /projects/{projectId}/proposals
GET  /projects/{projectId}/proposals/{proposalId}
GET  /projects/{projectId}/proposals/{proposalId}/review
POST /projects/{projectId}/proposals/{proposalId}/review-decision
POST /projects/{projectId}/proposals/ai-text/qwen
POST /projects/{projectId}/proposals/asr-local
POST /projects/{projectId}/proposals/photo
```

Las propuestas son revisables. La aceptación actual puede marcar estado, pero no debe documentarse como aplicación automática completa al modelo.

#### XMI parcial

```http
POST /projects/{projectId}/models/{modelId}/xmi/export
POST /projects/{projectId}/models/{modelId}/xmi/import/preview
POST /projects/{projectId}/models/{modelId}/xmi/import/confirm
```

Limitación: XMI parcial, no lossless.

#### Deployments y almacenamiento

```http
POST   /projects/{projectId}/deployments
GET    /projects/{projectId}/deployments
GET    /projects/{projectId}/deployments/{deploymentId}
PUT    /projects/{projectId}/deployments/{deploymentId}
POST   /projects/{projectId}/deployments/{deploymentId}/disable
POST   /projects/{projectId}/deployments/{deploymentId}/enable
DELETE /projects/{projectId}/deployments/{deploymentId}

GET /storage/status
GET /projects/{projectId}/storage/status
```

Deployments son demo; no ejecutan infraestructura real.

### 2.6.3 Frontend Angular

Scripts relevantes:

```bash
npm run iniciar    # ng serve --port 4200
npm run compilar   # ng build
npm run probar     # ng test
```

El README del frontend indica que apunta por ahora a:

```text
http://localhost:8080
```

### 2.6.4 Aplicación Flutter

Comandos documentados:

```bash
cd movil
flutter pub get
flutter run
flutter analyze
```

Uso general:

1. Ejecutar backend generado o compatible.
2. Abrir app Flutter.
3. Configurar URL base, por ejemplo `http://localhost:8080`.
4. Usar CRUD genérico de `clientes`.
5. Usar asistente offline con palabras como `crear`, `listar`, `editar`, `eliminar`.

### 2.6.5 Backend generado

Según `backend-case/README.md`, la primera versión genera un backend Spring Boot CRUD básico. Las relaciones quedan registradas en el modelo intermedio, pero el mapeo JPA avanzado se completa en fase posterior. Por tanto, en la defensa debe mostrarse como:

- funcional para CRUD básico;
- apto para demostrar generación;
- no equivalente todavía a un generador empresarial completo.

## 2.7 Pruebas y evidencia

### 2.7.1 Plan de pruebas académico

El plan de pruebas del repositorio propone simulacros:

- Clínica: Paciente, Médico, Consulta, Especialidad.
- Biblioteca: Libro, Autor, Usuario, Préstamo.
- Ventas: Cliente, Producto, Venta, DetalleVenta.

Pruebas mínimas por simulacro:

1. crear proyecto;
2. crear modelo;
3. crear entidades;
4. crear atributos y claves primarias;
5. crear relaciones;
6. guardar modelo;
7. recuperar modelo;
8. validar modelo;
9. generar backend;
10. descargar ZIP;
11. compilar backend generado;
12. ejecutar backend generado;
13. abrir Swagger;
14. crear/listar/editar/eliminar registros;
15. consumir desde Flutter;
16. probar asistente local sin Internet.

### 2.7.2 Evidencia local CU1-CU18 registrada

El archivo `documentacion/requisitos/RESULTADO_PRUEBAS_LOCALES_CU.md` registra una ejecución local del 2026-09-21 con:

- PostgreSQL 16 en Docker healthy;
- backend Spring Boot en puerto 8080 con health `UP`;
- frontend Angular en `http://localhost:4200`;
- Docker Model Runner en `http://127.0.0.1:12434`;
- modelo Qwen disponible para esa corrida.

Resumen de resultados:

| CU | Resultado registrado | Límite |
|---|---|---|
| CU1 | Parcial OK demo | No productivo. |
| CU2 | OK demo | Versión compatible con fecha `1970-01-01` en una capa. |
| CU3 | OK demo | Estado en memoria. |
| CU4 | OK demo | Estado en memoria. |
| CU5 | Parcial | Operaciones UML no implementadas. |
| CU6 | Parcial | Atajos avanzados no garantizados. |
| CU7 | OK demo | Aceptación no muta modelo. |
| CU8 | Pendiente opt-in | Sidecar ASR no levantado. |
| CU9 | OK placeholder | `QWEN_UNCONFIGURED` para foto. |
| CU10 | OK demo | SSE no medido en esa corrida. |
| CU11 | OK demo | Estado local/no distribuido. |
| CU12 | OK demo | Sin merge semántico. |
| CU13 | Parcial | No existe `movil/test`; cola en memoria. |
| CU14 | OK parcial | XMI no lossless. |
| CU15 | OK demo | Generó artefacto con hash registrado. |
| CU16 | OK demo | No despliega infraestructura real. |
| CU17 | OK | `/actuator/health` respondió UP. |
| CU18 | OK local | S3/Floci no probados. |

### 2.7.3 Verificaciones registradas por fase

| Fase | Comando/evidencia | Resultado documentado |
|---|---|---|
| Fase 1 | `cd frontend-case && npm run compilar` | Correcto. |
| Fase 1 | `mvn -f backend-case/pom.xml test` | Bloqueado: Maven no instalado. |
| Fase 1 | Docker build backend | Bloqueado: daemon Docker no disponible en ese entorno. |
| Fase 2 | `cd frontend-case && npm run compilar` | Exitoso. |
| Fase 2 | `cd backend-case && mvn test` | Bloqueado por Maven ausente. |
| Fase 3 | `cd frontend-case && npm run compilar` | Correcto. |
| Fase 3 | `cd movil && flutter analyze` | Correcto, sin issues. |
| Fase 4 | `cd frontend-case && npm run compilar` | Exitoso. |
| Fase 4 | `cd backend-case && mvn test` | Bloqueado por Maven ausente. |
| Resultado local CU | `cd frontend-case && npm run compilar` | Correcto. |
| Resultado local CU | `curl.exe http://localhost:4200/` | Correcto. |
| Resultado local CU | `cd movil && flutter analyze` | Correcto. |
| Resultado local CU | `cd movil && flutter test` | Bloqueado/no aplicable: no existe `movil/test`. |

### 2.7.4 Riesgos técnicos restantes

- Autenticación productiva no completa.
- Estados demo/en memoria en permisos, colaboración, presencia, conflictos y deployments.
- Editor visual Angular todavía no es un canvas CASE completo.
- Generación avanzada de relaciones JPA pendiente.
- XMI no lossless.
- Qwen y ASR requieren configuración externa explícita.
- AWS real no demostrado.
- Flutter consume entidad demo y no se genera automáticamente desde el modelo.

---

# Capítulo 3. Manual de Usuario

## 3.1 Requisitos previos

Para ejecución local completa:

- Docker y Docker Compose disponibles.
- Puertos libres: 4200, 8080 y 5432.
- Opcional: Node/npm para ejecutar Angular sin Docker.
- Opcional: Java 21 y Maven para ejecutar backend sin Docker.
- Opcional: Flutter SDK para ejecutar la app móvil.
- Opcional: Docker Model Runner/Qwen para propuestas IA opt-in.

## 3.2 Puesta en marcha local con Docker Compose

Desde la raíz del repositorio:

```bash
docker compose -f infraestructura/local/docker-compose.yml up --build -d
```

Servicios esperados:

```text
Frontend: http://localhost:4200
Backend:  http://localhost:8080
Swagger:  http://localhost:8080/swagger-ui.html
Postgres: localhost:5432 / proyecto_case
```

Para detener el entorno:

```bash
docker compose -f infraestructura/local/docker-compose.yml down
```

> No usar este Compose como producción. Las credenciales locales (`case_user`, `case_password`) son de desarrollo.

## 3.3 Rol: Modelador en frontend web

### 3.3.1 Crear proyecto

1. Abrir `http://localhost:4200`.
2. Buscar la sección de proyecto.
3. Ingresar nombre y descripción.
4. Crear el proyecto.
5. Verificar que aparezca en la lista o que quede seleccionado.

### 3.3.2 Crear modelo conceptual

1. Seleccionar un proyecto.
2. Crear un modelo conceptual.
3. Confirmar que el modelo quede asociado al proyecto.

### 3.3.3 Crear entidades

1. Ingresar nombre de entidad, por ejemplo `Cliente`.
2. Definir posición inicial si la interfaz la solicita.
3. Crear la entidad.
4. Repetir para otras entidades como `Producto`, `Venta`, `DetalleVenta`.

### 3.3.4 Crear atributos

Para cada entidad:

1. Seleccionar la entidad.
2. Agregar atributo, por ejemplo `id`, `nombre`, `correo`.
3. Definir tipo de dato.
4. Marcar clave primaria cuando corresponda.
5. Marcar obligatorio o único si aplica.

### 3.3.5 Crear operaciones

El contrato académico soportado persiste operaciones semánticas dentro de cada entidad. Cada operación tiene `id`, `entidadId`, `nombre`, `tipoRetorno`, `firma`, `visibilidad` (`PUBLICA`, `PROTEGIDA`, `PRIVADA`, `PAQUETE`) y marcas de tiempo. La representación UML compatible se expone como texto, por ejemplo `+calcularTotal() : Decimal`, y además como DTO en `detalleOperaciones` para no mezclar semántica con layout.

Rutas principales:

- `POST /entidades/{entidadId}/operaciones`
- `GET /entidades/{entidadId}/operaciones`
- `PUT /operaciones/{operacionId}`
- `DELETE /operaciones/{operacionId}`

### 3.3.6 Crear relaciones

El perfil de relaciones UML soportado es binario y persistido de punta a punta. Desde la paleta **Relaciones UML** se puede crear:

- **Asociación**: línea continua; permite verbo/rol y multiplicidades.
- **Agregación**: diamante hueco.
- **Composición**: diamante lleno.
- **Generalización**: triángulo hueco.
- **Dependencia**: línea discontinua con flecha abierta.
- **Realización**: línea discontinua con triángulo hueco.

Pasos básicos:

1. Seleccionar el tipo de relación en la paleta.
2. Seleccionar clase origen y clase destino.
3. Editar nombre, verbo/rol y cardinalidades en el inspector.
4. Cambiar el tipo si corresponde y guardar la relación.

No se representan clases de asociación, asociaciones n-arias, calificadores, templates ni construcciones completas del metamodelo UML: el contrato actual persiste relaciones binarias entre dos entidades.

### 3.3.7 Validar modelo

1. Ejecutar validación.
2. Si hay errores, corregir nombres, claves primarias, atributos duplicados o relaciones incompletas.
3. Repetir hasta obtener resultado válido.

### 3.3.8 Generar backend

1. Con el modelo válido, ejecutar generación.
2. Esperar estado completado.
3. Descargar ZIP generado.
4. Registrar hash o identificador si la interfaz lo muestra.

## 3.4 Rol: Evaluador/docente

El evaluador puede comprobar:

1. que el sistema levanta localmente;
2. que Swagger está disponible;
3. que el modelo puede crearse y recuperarse;
4. que la validación detecta errores básicos;
5. que la generación produce un ZIP;
6. que el backend generado tiene estructura Spring Boot;
7. que Flutter puede configurarse para consumir una API REST;
8. que el asistente móvil responde sin Internet.

## 3.5 Rol: Operador técnico

### 3.5.1 Ver salud del backend

```bash
curl http://localhost:8080/actuator/health
```

Resultado esperado:

```json
{"status":"UP"}
```

### 3.5.2 Revisar Swagger

Abrir:

```text
http://localhost:8080/swagger-ui.html
```

### 3.5.3 Compilar frontend Angular sin Docker

```bash
cd frontend-case
npm install
npm run compilar
```

### 3.5.4 Ejecutar backend sin Docker

Si Java 21 y Maven están disponibles:

```bash
cd backend-case
mvn spring-boot:run
```

### 3.5.5 Ejecutar app Flutter

```bash
cd movil
flutter pub get
flutter run
```

## 3.6 Uso de Qwen web opt-in

Por defecto Qwen no está activo para propuestas textuales. Para habilitarlo en Compose:

```bash
AI_TEXT_PROPOSALS_ENABLED=true docker compose -f infraestructura/local/docker-compose.yml up --build -d
```

Variables relevantes:

```text
AI_TEXT_PROPOSALS_ENABLED=false
AI_TEXT_PROPOSALS_PROVIDER=qwen|deterministic
AI_QWEN_ENDPOINT=http://host.docker.internal:12434/engines/v1/chat/completions
CU12_PHOTO_QWEN_ENABLED=false
```

Debe existir un servicio compatible con el endpoint configurado. Si no existe, la función debe tratarse como no disponible o placeholder.

## 3.7 Uso del asistente móvil local offline

1. Ejecutar Flutter.
2. Abrir panel de asistente offline.
3. Preguntar usando palabras clave como:
   - `crear`;
   - `listar`;
   - `editar`;
   - `eliminar`.
4. Confirmar que responde sin conexión a Internet.

Este asistente no es Qwen. Usa assets locales y coincidencia de palabras clave.

## 3.8 Manual por roles

| Rol | Funciones principales | Restricciones |
|---|---|---|
| Usuario demo | Iniciar sesión demo, crear proyecto, modelar, generar. | No representa seguridad productiva. |
| Modelador | Crear entidades, atributos, relaciones y validar. | Editor visual avanzado incompleto. |
| Administrador proyecto | Gestionar miembros/capacidades demo. | Varios datos pueden ser en memoria. |
| Administrador diagrama | Gestionar colaboración/vista demo. | No distribuido productivo. |
| Operador técnico | Levantar Compose, revisar health, ejecutar pruebas. | Depende de Docker/Maven/Flutter instalados. |
| Usuario móvil | Configurar backend, operar CRUD demo, usar asistente. | CRUD genérico; entidad ejemplo `clientes`. |

## 3.9 Despliegue y demostración

### Demostración local recomendada

1. Levantar Docker Compose.
2. Abrir frontend.
3. Crear proyecto/modelo.
4. Crear entidades y atributos.
5. Validar.
6. Generar backend.
7. Descargar ZIP.
8. Abrir Swagger.
9. Ejecutar Flutter.
10. Configurar URL base.
11. Probar CRUD demo.
12. Probar asistente offline.

### AWS

El proyecto menciona despliegue AWS como objetivo. El repositorio contiene carpeta `infraestructura/aws`, pero la evidencia actual no demuestra despliegue real productivo. Debe presentarse como **planeado** o **pendiente de evidencia**, no como completado.

---

# Capítulo 4. Bibliografía

La bibliografía combina referencias académicas generales y fuentes internas del repositorio.

## 4.1 Referencias externas

1. Sommerville, I. (2016). *Software Engineering* (10th ed.). Pearson.
2. Pressman, R. S., & Maxim, B. R. (2020). *Software Engineering: A Practitioner's Approach* (9th ed.). McGraw-Hill.
3. Booch, G., Rumbaugh, J., & Jacobson, I. (2005). *The Unified Modeling Language User Guide* (2nd ed.). Addison-Wesley.
4. Object Management Group. (2017). *Unified Modeling Language Specification, Version 2.5.1*.
5. ISO/IEC. (2011). *ISO/IEC 25010: Systems and software engineering — Systems and software Quality Requirements and Evaluation (SQuaRE) — System and software quality models*.
6. Fowler, M. (2002). *Patterns of Enterprise Application Architecture*. Addison-Wesley.
7. Richardson, L., & Ruby, S. (2007). *RESTful Web Services*. O'Reilly.
8. Spring. (s. f.). *Spring Boot Reference Documentation*.
9. PostgreSQL Global Development Group. (s. f.). *PostgreSQL Documentation*.
10. Angular. (s. f.). *Angular Documentation*.
11. Flutter. (s. f.). *Flutter Documentation*.
12. Docker. (s. f.). *Docker Compose Documentation*.

## 4.2 Fuentes internas del repositorio

1. `GUIA DEL PROYECTO.txt`.
2. `Resumen_Completo_Proyecto_Software_I.md`.
3. `README.md`.
4. `backend-case/README.md`.
5. `frontend-case/README.md`.
6. `movil/README.md`.
7. `documentacion/ARQUITECTURA.md`.
8. `documentacion/pruebas/PLAN_PRUEBAS.md`.
9. `documentacion/requisitos/CASOS_USO_MVP.md`.
10. `documentacion/requisitos/MODELO_CASOS_USO.md`.
11. `documentacion/requisitos/MATRIZ_ACTORES_CU_MODULOS.md`.
12. `documentacion/requisitos/RESULTADO_PRUEBAS_LOCALES_CU.md`.
13. `documentacion/requisitos/EVIDENCIA_FASE_1_RUTA_CRITICA.md`.
14. `documentacion/requisitos/EVIDENCIA_FASE_2_COLABORACION_ADMIN.md`.
15. `documentacion/requisitos/EVIDENCIA_FASE_3_MOVIL_INTEGRACIONES.md`.
16. `documentacion/requisitos/EVIDENCIA_FASE_4_INTERCAMBIO_DESPLIEGUE_ALMACENAMIENTO.md`.
17. `infraestructura/local/README.md`.
18. `infraestructura/local/docker-compose.yml`.
19. `backend-case/pom.xml`.
20. `frontend-case/package.json`.
21. `movil/pubspec.yaml`.
22. `backend-case/src/main/resources/application.yml`.
23. `backend-case/src/main/resources/db/migration/V1__estructura_inicial.sql`.

---

# Capítulo 5. Anexos

## 5.1 Estándares de codificación

### 5.1.1 Principios generales

- Usar nombres claros, expresivos y consistentes.
- Separar responsabilidades por módulo.
- Evitar lógica de negocio en controladores.
- Validar entradas antes de generar artefactos.
- No mezclar estado semántico con layout visual.
- Documentar limitaciones de funciones demo u opt-in.
- Mantener generación de código determinista.
- No depender de IA generativa para código crítico.

### 5.1.2 Estándares para Java/Spring Boot

- Paquete base: `bo.edu.proyecto.caseapp`.
- Organización recomendada por módulo:

```text
modulo/
  dominio/
  aplicacion/
  infraestructura/
  presentacion/
```

- Controladores en `presentacion`.
- Servicios de caso de uso en `aplicacion`.
- Entidades/reglas en `dominio`.
- Persistencia/adaptadores en `infraestructura`.
- DTO/records para requests y responses cuando corresponda.
- Validaciones con Bean Validation (`@Valid`, restricciones de campos).
- Manejo centralizado de errores cuando aplique.
- Migraciones versionadas con Flyway.
- No usar `ddl-auto=create` en contexto de evidencia; el proyecto usa `validate`.

### 5.1.3 Estándares para API REST

- Usar sustantivos en rutas.
- Usar métodos HTTP según intención:
  - `GET` para consulta;
  - `POST` para creación o comando;
  - `PUT/PATCH` para actualización;
  - `DELETE` para eliminación.
- Responder errores de validación de forma clara.
- No ocultar si una función es demo o parcial.
- Mantener compatibilidad de rutas existentes cuando se agregan aliases.
- Documentar endpoints con Swagger/OpenAPI.

### 5.1.4 Estándares para Angular

- Mantener tipos TypeScript en `nucleo/modelos-case.ts`.
- Encapsular llamadas HTTP en servicios.
- Evitar lógica de negocio pesada en plantillas HTML.
- Separar componentes cuando la pantalla crezca.
- Mostrar al usuario estados de carga, error y éxito.
- No bloquear el flujo principal por integraciones opt-in como Qwen.

### 5.1.5 Estándares para Flutter

- Separar configuración, cliente HTTP y funcionalidades.
- Mantener el asistente offline independiente de servicios remotos.
- Manejar errores de red con mensajes comprensibles.
- No asumir una entidad fija en diseños futuros; la entidad `clientes` es demo.
- Preparar componentes CRUD reutilizables para distintos backends generados.

### 5.1.6 Estándares para documentación y evidencia

- Toda función debe marcarse como implementada, parcial, demo, opt-in o planeada.
- Las pruebas deben guardar resultado y entorno.
- Las afirmaciones de despliegue deben respaldarse con evidencia.
- Las capturas o logs deben guardarse en `evidencias/` cuando se realicen simulacros.
- Este Markdown puede usarse como base para Word, pero el formato final debe revisarse manualmente.

## 5.2 ISO/IEC 25010 aplicado al proyecto

| Característica | Aplicación al proyecto | Estado/riesgo |
|---|---|---|
| Adecuación funcional | Crear modelos, validar, generar backend y consumir desde Flutter. | Ruta principal parcial/funcional para demo; funciones avanzadas pendientes. |
| Eficiencia de desempeño | Backend monolítico y PostgreSQL local suficientes para evaluación. | No hay pruebas de carga. |
| Compatibilidad | API REST/JSON y Swagger favorecen integración. | XMI no lossless; Flutter consume CRUD genérico. |
| Usabilidad | Frontend concentra flujo académico. | Canvas visual avanzado pendiente. |
| Fiabilidad | Validaciones y health check ayudan a control básico. | Estados en memoria y demos no son tolerantes a fallos productivos. |
| Seguridad | Se documenta que login demo y `X-User-Id` no son producción. | Seguridad real pendiente. |
| Mantenibilidad | Monorepo modular, paquetes por responsabilidad. | Algunas capas demo pueden requerir refactor para producción. |
| Portabilidad | Docker Compose facilita ejecución local. | AWS real no evidenciado; depende de ambiente local. |

## 5.3 Matriz de estado por componente

| Componente | Estado | Comentario |
|---|---|---|
| Backend CASE | Implementado parcialmente | Cubre flujo central y endpoints demo. |
| PostgreSQL/Flyway | Implementado | Migración inicial evidencia tablas principales. |
| Frontend Angular | Implementado mínimo | Flujo principal; editor visual completo pendiente. |
| Generador | Implementado inicial | CRUD básico; relaciones avanzadas limitadas. |
| ZIP de artefactos | Implementado | Descarga disponible. |
| Swagger | Configurado | `/swagger-ui.html`. |
| Flutter | Implementado demo | CRUD genérico y asistente offline. |
| Qwen texto | Opt-in | Desactivado por defecto. |
| Qwen foto | Placeholder/opt-in | No extracción real por defecto. |
| ASR local | Opt-in/pendiente de entorno | Requiere servicio externo local. |
| XMI | Parcial | No lossless. |
| Colaboración HTTP/SSE | Demo/parcial | No distribuida productiva. |
| Deployments | Demo | No infraestructura real. |
| AWS | Planeado | Sin evidencia productiva. |

## 5.4 Glosario

| Término | Definición |
|---|---|
| CASE | Herramienta de asistencia al desarrollo de software. |
| Modelo conceptual | Representación de entidades, atributos y relaciones de un dominio. |
| Modelo intermedio | Estructura normalizada usada por el generador antes de crear código. |
| CRUD | Crear, leer, actualizar y eliminar registros. |
| DTO | Objeto de transferencia de datos. |
| JPA | API Java para persistencia relacional. |
| Flyway | Herramienta de migraciones de base de datos. |
| Swagger/OpenAPI | Documentación interactiva de APIs. |
| SSE | Server-Sent Events, canal HTTP para eventos del servidor al cliente. |
| Opt-in | Función que requiere habilitación explícita. |
| Lossless | Sin pérdida de información. En el proyecto, XMI no es lossless. |

## 5.5 Checklist de defensa sugerida

- [ ] Levantar Docker Compose.
- [ ] Mostrar frontend Angular.
- [ ] Crear proyecto.
- [ ] Crear modelo.
- [ ] Crear entidades y atributos.
- [ ] Definir clave primaria.
- [ ] Crear o explicar relación según soporte UI/API.
- [ ] Validar modelo.
- [ ] Generar backend.
- [ ] Descargar ZIP.
- [ ] Mostrar Swagger.
- [ ] Ejecutar o explicar backend generado.
- [ ] Abrir Flutter.
- [ ] Configurar URL base.
- [ ] Probar CRUD demo.
- [ ] Probar asistente offline.
- [ ] Explicar limitaciones: AWS, XMI, Qwen, ASR, seguridad productiva y canvas avanzado.
