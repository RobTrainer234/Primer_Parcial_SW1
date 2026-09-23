# Especificación de diseño visual para el frontend web CASE

## Propósito

Este documento describe **todo lo que actualmente puede hacer y mostrar la web** para que otra IA pueda rediseñar su apariencia, colores, componentes y jerarquía visual sin inventar funcionalidades ni romper los contratos existentes.

La aplicación es un frontend Angular para una herramienta CASE académica que permite:

1. Crear proyectos.
2. Crear modelos conceptuales.
3. Crear entidades y atributos.
4. Validar modelos.
5. Sincronizar y consultar snapshots.
6. Generar un backend Spring Boot.
7. Gestionar colaboración demo.
8. Crear y revisar propuestas textuales, Qwen, voz y fotografía.
9. Intercambiar XMI parcial.
10. Simular deployments.
11. Consultar el estado de almacenamiento.

El rediseño debe mejorar la comprensión del flujo y la jerarquía visual, pero no debe cambiar el comportamiento funcional actual.

---

## 1. Restricciones funcionales que el diseño debe respetar

- No cambiar rutas HTTP.
- No cambiar nombres de campos ni bindings Angular.
- No eliminar botones o capacidades existentes.
- No presentar funcionalidades demo como productivas.
- No presentar autenticación demo como autenticación real.
- No presentar XMI como intercambio lossless.
- No presentar deployments demo como infraestructura ejecutada.
- No presentar Qwen como activo si está deshabilitado.
- No ocultar errores, advertencias ni estados de disponibilidad.
- Mantener accesibilidad para teclado, foco visible y lectores de pantalla.
- Mantener funcionamiento responsive en desktop, tablet y móvil.

---

## 2. Usuario y objetivo de la pantalla

### Usuario principal

Modelador, estudiante o evaluador que necesita demostrar el ciclo:

```text
Proyecto → Modelo → Entidades → Atributos → Validación → Generación
```

### Usuario secundario

Administrador o colaborador que consulta:

- miembros;
- permisos;
- diagramas;
- presencia;
- conflictos;
- propuestas;
- importación/exportación;
- deployments y almacenamiento.

### Objetivo visual

La interfaz debe parecer una herramienta profesional de modelado, no un formulario administrativo largo. Debe diferenciar claramente:

- flujo principal;
- módulos avanzados;
- estados del sistema;
- acciones destructivas o irreversibles;
- capacidades demo, opt-in o parcialmente implementadas.

---

## 3. Estructura visual recomendada

### 3.1 Shell general

La pantalla debe tener:

- barra superior con identidad del producto;
- indicador de conexión/API;
- estado de sesión;
- navegación interna o índice de secciones;
- contenido central con ancho legible;
- sistema consistente de mensajes, badges y estados.

La implementación actual es una sola página Angular. Se puede rediseñar como dashboard con navegación por anclas, tabs o sidebar, sin convertir obligatoriamente el sistema en múltiples rutas Angular.

### 3.2 Jerarquía de secciones

Orden recomendado:

1. Encabezado y estado del entorno.
2. Sesión demo.
3. Flujo principal del modelo.
4. Validación y generación.
5. Colaboración y administración.
6. Propuestas e integraciones de IA.
7. XMI, deployments y almacenamiento.
8. Resumen técnico del modelo.

### 3.3 Diferenciación visual

Usar tres niveles:

- **Core:** proyecto, modelo, entidad, atributo, validación y generación.
- **Colaborativo:** permisos, diagramas, presencia, sincronización y conflictos.
- **Integraciones:** Qwen, ASR, fotografía, XMI, deployments y storage.

Las funciones demo/opt-in deben usar badges o etiquetas como:

```text
DEMO
OPT-IN
PARCIAL
NO DISPONIBLE
EN MEMORIA
```

---

## 4. Encabezado principal

### Contenido actual

- Etiqueta: `Herramienta CASE`.
- Título: `Generador de backend Spring Boot`.
- Descripción del flujo mínimo.
- API actual: `http://localhost:8080`.
- Usuario demo si existe.

### Tratamiento visual esperado

- Hero sobrio, tecnológico y académico.
- Nombre del producto visible.
- Estado API como badge:
  - conectado;
  - verificando;
  - no disponible.
- Sesión demo separada del estado de infraestructura.
- No usar el color verde para indicar autenticación productiva.

### Acciones

No hay una acción directa en el encabezado, pero debe mostrar claramente el contexto actual y el estado del sistema.

---

## 5. CU1 — Sesión demo

### Acciones

| Acción | Resultado esperado |
|---|---|
| `Demo login` | Inicia sesión académica y muestra usuario/token demo y advertencia. |
| `Logout` | Cierra la sesión demo y limpia el usuario visible. |

### Información que debe mostrarse

- Usuario actual.
- Estado: sesión demo activa/inactiva.
- Advertencia: `X-User-Id` y `tokenDemo` no son autenticación productiva.
- Estado de los botones:
  - Logout deshabilitado sin sesión.
  - Login deshabilitado durante carga.

### Diseño recomendado

- Tarjeta compacta de sesión.
- Icono de usuario.
- Badge `DEMO`.
- Advertencia visible pero secundaria.
- No ocupar el mismo peso visual que el flujo de modelado.

---

## 6. Mensajes globales

La interfaz actualmente tiene dos mensajes globales:

### Mensaje informativo/éxito

Variable: `mensaje`.

Ejemplos:

- `Proyectos cargados.`
- `Proyecto creado: Clinica`.
- `Modelo valido para generar.`
- `Backend generado correctamente.`
- `Snapshot v1 obtenido.`

### Mensaje de error

Variable: `error`.

Debe mostrar:

- qué operación falló;
- mensaje devuelto por el backend;
- acción sugerida cuando sea posible.

### Recomendación visual

Convertir los mensajes en:

- toast para éxitos breves;
- alert persistente para errores;
- banner de advertencia para limitaciones;
- indicador de carga con spinner o progreso real.

No ocultar un error inmediatamente ni reemplazarlo silenciosamente por un mensaje genérico.

---

## 7. Flujo principal de modelado

El flujo principal debe verse como un wizard visual o progress rail, aunque continúe funcionando como una sola pantalla.

```text
1 Proyecto → 2 Modelo → 3 Entidad → 4 Atributo → 5 Validar/Generar
```

Cada paso debe mostrar:

- número o icono;
- título;
- propósito;
- prerequisito;
- campos;
- acción principal;
- resultado asociado;
- estado completado/bloqueado/activo.

---

## 8. Paso 1 — Proyecto

### Campos

- Nombre.
- Descripción.

Valores iniciales actuales:

- Nombre: `Clinica`.
- Descripción: `Modelo de prueba para la herramienta CASE`.

### Acciones

| Acción | Endpoint | Estado visual |
|---|---|---|
| Crear proyecto | `POST /proyectos` | Éxito, error o carga. |
| Recargar proyectos | `GET /proyectos` | Lista actualizada. |
| Seleccionar proyecto | Acción local | Proyecto activo resaltado. |

### Lista de proyectos

Cada proyecto debe mostrar:

- nombre;
- descripción resumida;
- estado;
- fecha de actualización si se decide mostrarla;
- indicador de seleccionado.

### Estados

- Lista cargando.
- Lista vacía: `Todavía no hay proyectos.`
- Lista con proyectos.
- Proyecto seleccionado.
- Error de consulta.

### Diseño recomendado

- Selector de proyecto en panel lateral o combobox visual.
- Proyecto activo con borde/color de selección.
- Acción primaria `Crear proyecto`.
- Recarga como acción secundaria.
- No mostrar una lista larga como una secuencia de botones sin contexto.

---

## 9. Paso 2 — Modelo conceptual

### Dependencia

Requiere un proyecto creado o seleccionado.

Si no existe, mostrar claramente:

```text
Primero crea o selecciona un proyecto.
```

### Campos

- Nombre del modelo.
- Proyecto actual como contexto.

Valor inicial actual: `Clinica`.

### Acción

- `Crear modelo`.
- Endpoint: `POST /proyectos/{proyectoId}/modelos`.

### Resultado

Mostrar:

- nombre;
- versión;
- semántica o etiqueta `UML mínimo`;
- estado del modelo activo.

### Diseño recomendado

- Mostrar breadcrumb o contexto:

```text
Proyecto actual / Modelo actual
```

- Si está bloqueado, explicar la dependencia en vez de sólo desactivar el botón.
- El modelo activo debe ser persistente visualmente mientras el usuario baja por la página.

---

## 10. Paso 3 — Entidad

### Dependencia

Requiere modelo conceptual activo.

### Campos

- Nombre de entidad.
- Posición X.
- Posición Y.

Valores actuales:

- Nombre: `Paciente`.
- X: `100`.
- Y: `100`.

### Acción

- `Crear entidad`.
- Endpoint: `POST /modelos/{modeloId}/entidades`.

### Lista de entidades

Cada elemento debe mostrar:

- nombre;
- cantidad de atributos;
- estado seleccionado;
- posición si es relevante.

### Estados

- Sin modelo.
- Sin entidades.
- Entidad creada.
- Entidad seleccionada.
- Error de creación.

### Diseño recomendado

- Reemplazar una lista de botones por cards compactas o filas seleccionables.
- Mostrar un contador total.
- Mostrar selección actual con color y borde.
- Preparar visualmente el espacio para un futuro canvas UML, aunque el canvas todavía no exista.

---

## 11. Paso 4 — Atributo

### Dependencia

Requiere una entidad seleccionada.

### Campos

- Nombre.
- Tipo de dato:
  - `TEXTO`;
  - `ENTERO`;
  - `ENTERO_LARGO`;
  - `DECIMAL`;
  - `BOOLEANO`;
  - `FECHA`;
  - `FECHA_HORA`.
- Clave primaria.
- Obligatorio.
- Valor único.

### Acción

- `Crear atributo`.
- Endpoint: `POST /entidades/{entidadId}/atributos`.

### Lista de atributos

Cada atributo debe mostrar como mínimo:

- nombre;
- tipo;
- badge `PK` si es clave primaria;
- badge `Required` si es obligatorio;
- badge `Unique` si es único.

### Diseño recomendado

- Usar una tabla compacta o lista de propiedades.
- Usar badges para restricciones.
- Diferenciar atributos clave con color/icono, no sólo texto.
- Mostrar claramente la entidad actual.

---

## 12. Paso 5 — Validar y generar

Esta sección debe tener mayor jerarquía que los módulos secundarios.

### Acciones

| Acción | Endpoint | Propósito |
|---|---|---|
| Validar modelo | `POST /modelos/{modeloId}/validacion` | Detectar errores antes de generar. |
| Snapshot | `GET /modelos/{modeloId}/sync/snapshot` | Consultar estado sincronizado. |
| Comando sync | `POST /modelos/{modeloId}/sync/commands` | Registrar comando demo. |
| Generar backend | `POST /modelos/{modeloId}/generaciones` | Crear backend Spring Boot. |
| Descargar ZIP | `/generaciones/{id}/artefacto` | Descargar artefacto completado. |

### Resultado de validación

Mostrar:

- `Modelo válido` con estado positivo;
- `Modelo con errores` con estado negativo;
- código del error;
- mensaje;
- elemento afectado.

Ejemplo:

```text
ENTIDAD_SIN_CLAVE_PRIMARIA
La entidad no tiene clave primaria: Cliente
```

### Estado de generación

Mostrar:

- ID del trabajo;
- estado:
  - `EN_PROCESO`;
  - `COMPLETADO`;
  - `FALLIDO`;
- target;
- perfil;
- SHA-256 del artefacto cuando exista;
- mensaje de error.

### Diseño recomendado

- Usar un stepper de validación → generación → descarga.
- El botón Descargar debe ser claramente posterior a una generación completada.
- Diferenciar error de validación de error de generación.
- Mostrar el hash como dato técnico expandible, no como contenido principal.

---

## 13. CU3/CU4/CU11/CU12 — Colaboración y administración

Esta sección debe verse como módulo avanzado, separado del camino principal.

### Acciones generales

- Cargar permisos.
- Crear diagrama.
- Agregar colaborador demo.
- Enviar presencia.
- Escuchar SSE.
- Crear conflicto demo.
- Listar conflictos.

### CU3 — Permisos de proyecto

Mostrar:

- miembros;
- rol;
- capacidad de crear diagrama;
- estado activo;
- invitaciones pendientes;
- sugerencias de cuentas;
- cantidad de eventos del historial.

Acción por miembro:

- `Alternar` capacidad de crear diagrama.

Badges sugeridos:

- `OWNER`;
- `EDITOR`;
- `VIEWER`;
- `ACTIVE`;
- `PENDING`.

### CU4 — Diagrama/vista

Mostrar:

- ID de vista;
- nombre;
- administrador;
- lista de colaboradores;
- rol;
- puede editar;
- puede comentar.

### CU11 — Presencia

Mostrar:

- estado de conexión SSE;
- usuario visible;
- estado online/offline;
- última actividad;
- cursor si existe.

La presencia debe verse como actividad, no como cambio del modelo.

### CU12 — Conflictos

Mostrar:

- operationId;
- estado;
- revisión base;
- revisión actual;
- detalle;
- resolución;
- fecha de resolución.

Acciones:

- listar conflictos;
- resolver conflicto.

La resolución debe pedir confirmación visual porque representa una decisión colaborativa.

### Limitaciones que deben conservarse visualmente

- Estado local/en memoria.
- No autorización productiva.
- SSE local/no distribuido.
- Conflicto demo basado en revisión.
- No presentar presencia como sincronización semántica completa.

---

## 14. CU7/CU8/CU9 — Propuestas e integraciones de IA

Esta sección debe tener identidad visual propia, pero no competir con el flujo central.

### Texto de propuesta

Campo multilinea actual:

```text
Agregar entidad Turno con fecha, hora y paciente asociado.
```

### Acciones

| Acción | Estado esperado |
|---|---|
| Crear propuesta textual | Propuesta manual pendiente de revisión. |
| Solicitar IA Qwen texto | Propuesta generada por Qwen si está habilitado. |
| Listar propuestas | Historial del proyecto. |
| Foto/Qwen opt-in | Placeholder o resultado real según configuración. |

### Etiquetas obligatorias

- `TEXT`;
- `AI_QWEN_TEXT`;
- `PHOTO_QWEN`;
- `READY_FOR_REVIEW`;
- `QWEN_UNAVAILABLE`;
- `QWEN_UNCONFIGURED`;
- `PENDING_REVIEW`;
- `ACCEPTED`;
- `REJECTED`.

### Lista de propuestas

Cada fila/card debe mostrar:

- ID;
- título;
- fuente;
- estado de fuente;
- estado de revisión;
- fecha;
- botón `Revisar`.

### Revisión

Mostrar:

- texto generado/original;
- fuente;
- metadata técnica expandible;
- checklist;
- nota de revisión;
- botones `Aceptar` y `Rechazar`.

### Mensaje crítico

Debe ser visible:

```text
Aceptar sólo registra la decisión; no modifica automáticamente el modelo en esta fase.
```

### Qwen

Mostrar claramente:

- IA Qwen `OPT-IN`;
- habilitado/deshabilitado;
- proveedor configurado/no configurado;
- error de disponibilidad;
- respuesta lista para revisión.

No mostrar `Qwen` como activo sólo porque existe el botón.

### ASR

La interfaz actual sólo muestra el contrato informativo. Para un futuro diseño de voz debe reservarse espacio para:

- seleccionar/subir audio;
- idioma `es-ES`;
- límite 25 MiB;
- estado de transcripción;
- texto transcrito;
- convertir transcripción en propuesta.

No simular un control funcional mientras la carga de audio no esté conectada.

### Foto

La interfaz actual crea un `FormData` vacío. El diseño puede preparar:

- carga de imagen;
- preview;
- validación de formato/tamaño;
- estado de extracción;
- propuesta resultante;
- advertencia de integración opt-in.

No presentar extracción real si el backend devuelve placeholder.

---

## 15. CU14 — XMI parcial

### Acciones

- `Exportar XMI`.
- `Preview import`.
- `Confirmar parcial`.

### Área de edición

Textarea grande para `XMI entrada/salida`.

Debe tener:

- tipografía monoespaciada;
- botón copiar;
- scroll horizontal;
- contador o advertencia de contenido grande;
- mensajes de XML inválido.

### Preview

Mostrar:

- token;
- cantidad de entidades;
- cantidad de relaciones;
- advertencias;
- limitación `XMI parcial/no lossless`.

### Diseño recomendado

Usar layout de dos paneles:

```text
Editor XMI | Preview y advertencias
```

La confirmación debe ser una acción destacada pero secundaria a la revisión.

---

## 16. CU16 — Deployments demo

### Campos

- Nombre del deployment.

Valor inicial: `Demo local`.

### Acciones

- Crear deployment.
- Listar deployments.
- Habilitar/deshabilitar.
- Eliminar.

### Lista

Mostrar:

- ID;
- nombre;
- ambiente;
- target;
- estado;
- habilitado/deshabilitado;
- limitación.

### Tratamiento de eliminación

- Acción destructiva separada.
- Confirmación antes de eliminar.
- No usar el mismo estilo que una acción primaria.

### Advertencia permanente

```text
Implementación académica en memoria; no ejecuta infraestructura real.
```

---

## 17. CU18 — Almacenamiento

### Acción

- `Ver estado de storage`.

### Información

- proveedor activo;
- estado;
- limitación;
- lista de proveedores;
- seleccionado;
- configurado;
- detalle.

### Proveedores esperados

- `local`;
- `s3`;
- `floci`.

### Estados visuales

- disponible;
- configurado pero no verificado;
- no configurado;
- opt-in.

No mostrar S3 o Floci como activos si únicamente están declarados en configuración.

---

## 18. Resumen técnico del modelo

La sección actual muestra el objeto completo en JSON.

Debe conservarse como sección secundaria para debugging/evaluación, no como contenido principal.

### Diseño recomendado

- Accordion `Ver JSON técnico`.
- Botón copiar.
- Syntax highlighting.
- Scroll independiente.
- Mostrar resumen legible antes del JSON:
  - número de entidades;
  - número de atributos;
  - número de relaciones;
  - revisión;
  - estado de validación.

---

## 19. Estados globales que el diseño debe contemplar

### Estado inicial

- No hay proyecto seleccionado.
- El flujo está bloqueado por dependencias.
- Debe explicarse qué acción habilita el siguiente paso.

### Cargando

- Spinner o skeleton.
- Botones bloqueados sólo durante la operación real.
- No usar únicamente un cambio de texto.

### Éxito

- Toast o banner breve.
- El resultado debe quedar visible en la sección correspondiente.

### Error

- Alert visible.
- Mensaje legible.
- Acción de recuperación cuando sea posible.

### Lista vacía

Cada módulo debe tener empty state dedicado:

- proyectos;
- entidades;
- atributos;
- propuestas;
- miembros;
- conflictos;
- deployments;
- presencias.

### Deshabilitado por dependencia

No basta con un botón gris. Mostrar:

```text
Necesitás seleccionar un proyecto para continuar.
```

### Estado parcial/demo

Usar una combinación de:

- badge;
- color neutro/ámbar;
- texto explicativo;
- enlace o tooltip de limitación.

---

## 20. Sistema visual sugerido para la IA de diseño

La otra IA puede proponer libremente colores y estilos, pero debe conservar esta semántica:

| Semántica | Tratamiento sugerido |
|---|---|
| Acción primaria | Color de marca fuerte. |
| Acción secundaria | Superficie neutra o contorno. |
| Éxito | Verde accesible. |
| Error | Rojo accesible. |
| Advertencia/opt-in | Ámbar. |
| Demo | Violeta o azul neutro. |
| Parcial/no lossless | Ámbar o gris informativo. |
| Estado activo | Color de marca + badge. |
| Estado pendiente | Ámbar o gris. |
| Acción destructiva | Rojo sólo para eliminar/rechazar. |

### Requisitos de accesibilidad

- Contraste WCAG AA como mínimo.
- Foco visible.
- Estados no comunicados sólo por color.
- Botones con texto claro.
- Labels asociados a inputs.
- Navegación por teclado.
- Mensajes de error asociados al campo o sección.
- Responsive sin scroll horizontal accidental.

---

## 21. Responsive

### Desktop

- Sidebar o índice opcional.
- Flujo principal en columnas.
- Paneles de resultados laterales cuando haya espacio.
- XMI en dos columnas.

### Tablet

- Dos columnas para core.
- Módulos avanzados apilables.
- Botoneras con wrapping.

### Mobile web

- Una columna.
- Acciones agrupadas por contexto.
- Inputs de ancho completo.
- Tablas convertidas en cards.
- JSON/XMI con scroll interno.
- No perder el contexto de proyecto/modelo seleccionado.

---

## 22. Componentes que la IA de diseño debería producir

Solicitar como mínimo:

1. Shell/dashboard principal.
2. Header con estado API y sesión.
3. Stepper del flujo de modelado.
4. Card de proyecto.
5. Selector/lista de proyectos.
6. Card de modelo.
7. Editor de entidad.
8. Editor de atributos.
9. Tabla/lista de propiedades.
10. Panel de validación.
11. Panel de generación y descarga.
12. Panel de colaboración.
13. Tabla de miembros/permisos.
14. Panel de presencia.
15. Panel de conflictos.
16. Panel de propuestas.
17. Vista de revisión de propuesta.
18. Editor/preview XMI.
19. Lista de deployments.
20. Panel de almacenamiento.
21. Inspector JSON técnico.
22. Toast, alert, empty state, loading y confirmation dialog.

---

## 23. Texto para entregar a otra IA de diseño

> Rediseñá visualmente el frontend Angular de una herramienta CASE académica. La aplicación permite crear proyectos, modelos conceptuales, entidades y atributos; validar modelos; generar y descargar backends Spring Boot; consultar snapshots; sincronizar comandos; administrar permisos demo; crear diagramas; mostrar presencia y conflictos; crear/revisar propuestas textuales y Qwen; preparar voz ASR y fotografía opt-in; importar/exportar XMI parcial; gestionar deployments demo y consultar storage. No cambies ninguna ruta, acción, binding ni contrato funcional. Diseñá una experiencia dashboard/wizard clara, moderna y profesional, con jerarquía fuerte para el flujo Proyecto → Modelo → Entidad → Atributo → Validar → Generar. Separá visualmente funcionalidades core, colaboración e integraciones. Mostrá estados de carga, éxito, error, vacío, bloqueado, demo, opt-in, parcial y no disponible. Conservá advertencias honestas: autenticación demo, Qwen opt-in, ASR externo, XMI no lossless, deployments en memoria y S3/Floci no verificados. Entregá propuesta de layout, sistema de colores accesible, tipografía, componentes, responsive desktop/tablet/mobile, estados interactivos y especificaciones de botones, tablas, badges, alerts, modales y paneles.

---

## 24. Criterios de aceptación del rediseño

- [ ] El flujo principal se entiende sin leer toda la página.
- [ ] El usuario sabe qué paso está activo.
- [ ] Los prerequisitos se entienden antes de presionar botones bloqueados.
- [ ] Los resultados de cada acción aparecen cerca de su acción.
- [ ] Los estados demo/opt-in/parcial son visibles.
- [ ] Las acciones destructivas tienen tratamiento diferenciado.
- [ ] Propuestas y revisiones no se confunden con cambios aplicados al modelo.
- [ ] XMI muestra claramente sus limitaciones.
- [ ] La generación muestra estado y descarga sólo cuando corresponde.
- [ ] El JSON técnico no domina la pantalla.
- [ ] La interfaz es usable en desktop, tablet y móvil.
- [ ] El diseño no exige cambiar rutas ni lógica Angular.

## Referencias del código actual

- `frontend-case/src/app/componente-raiz.html`
- `frontend-case/src/app/componente-raiz.ts`
- `frontend-case/src/app/componente-raiz.css`
- `frontend-case/src/app/nucleo/servicio-api-case.ts`
- `frontend-case/src/app/nucleo/modelos-case.ts`
- `frontend-case/README.md`
