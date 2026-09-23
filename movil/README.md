# Aplicación móvil

Aplicación Flutter reutilizable para consumir el backend Spring Boot generado y demostrar un asistente local offline.

## Qué incluye

- App Flutter mínima con `lib/main.dart`.
- Configuración editable de la URL base del backend dentro de la app.
- Cliente HTTP genérico para endpoints REST JSON.
- Pantalla CRUD reutilizable con entidad de ejemplo `clientes`.
- Asistente offline que carga `assets/conocimiento_asistente.json` y responde por palabras clave.

## Estructura

```text
lib/main.dart                              Punto de entrada Flutter.
lib/nucleo/                               Configuración y cliente HTTP.
lib/funcionalidades/crud/                 Definición y pantalla CRUD genérica.
lib/funcionalidades/asistente/            Servicio y panel del asistente offline.
assets/                                   Contexto y conocimiento local.
```

## Requisitos

- Flutter SDK compatible con Dart `>=3.4.0 <4.0.0`.
- Backend Spring Boot ejecutándose y exponiendo endpoints JSON simples, por ejemplo:
  - `GET /clientes`
  - `POST /clientes`
  - `PUT /clientes/{id}`
  - `DELETE /clientes/{id}`

## Instalación y ejecución

Desde la raíz del repositorio:

```bash
cd movil
flutter pub get
flutter run
```

Para web, si está habilitado en tu instalación de Flutter:

```bash
cd movil
flutter run -d chrome
```

## Verificación

```bash
cd movil
flutter pub get
flutter analyze
```

Si Flutter no está instalado o no está en `PATH`, al menos se puede inspeccionar la estructura esperada:

```bash
find movil/lib movil/assets -type f
```

## Uso

1. Ejecutá el backend generado.
2. Abrí la app Flutter.
3. En `Backend configuration`, configurá la URL base, por ejemplo `http://localhost:8080`.
4. Usá la tarjeta `Clientes` para listar, crear, editar o eliminar registros.
5. Usá el `Asistente offline` con preguntas que contengan palabras clave como `crear`, `listar`, `editar` o `eliminar`.

## Limitaciones

- No incluye autenticación.
- No persiste la URL base entre sesiones.
- La entidad de demostración es `clientes`; para otra entidad hay que cambiar la definición en `lib/funcionalidades/crud/entity_definition.dart`.
- El cliente asume JSON simple y soporta listas directas, `content` paginado o colecciones Spring Data REST bajo `_embedded`.
- El asistente no usa red ni IA remota; solo responde por coincidencia básica de palabras clave desde el asset local.
- La app no genera código Flutter automáticamente desde el modelo conceptual.
