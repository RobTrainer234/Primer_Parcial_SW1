# Flutter consumidor mínimo

Fuente principal: `GUIA DEL PROYECTO.txt`.

## Objetivo

Implementar una primera aplicación Flutter reutilizable que consuma un backend Spring Boot generado, permita configurar la URL base, ejecute operaciones CRUD genéricas y demuestre un asistente local offline básico.

## Tareas

- [x] Completar estructura Flutter mínima ejecutable.
- [x] Implementar configuración de backend y cliente HTTP genérico.
- [x] Implementar pantallas CRUD reutilizables para una entidad de ejemplo/configurable.
- [x] Implementar asistente local offline usando assets JSON.
- [x] Verificar compilación o estructura disponible y documentar uso.

## Alcance incluido

- App Flutter mínima con `main.dart`.
- Cliente HTTP configurable.
- CRUD genérico básico contra endpoints REST del backend generado.
- Lectura de conocimiento local desde assets.
- Documentación de ejecución.

## Fuera de alcance por ahora

- Generación automática del Flutter desde el modelo conceptual.
- Autenticación.
- Diseño visual definitivo.
- Publicación en tiendas.
- Integración avanzada con IA real.

## Criterios de aceptación

- [x] La app permite configurar la URL base del backend generado.
- [x] La app puede listar, crear, editar y eliminar registros simples.
- [x] El asistente responde preguntas básicas sin conexión.
- [x] El proyecto tiene comandos claros de ejecución/verificación.

## Estado

Completado.

## Evidencia de verificación

- `flutter --version`: Flutter 3.44.1 estable disponible con Dart 3.12.1.
- `cd movil && flutter pub get`: dependencias resueltas correctamente.
- `cd movil && flutter analyze`: sin issues.
- `find movil/lib movil/assets -type f | sort`: estructura esperada presente.
- `grep -R "assets/conocimiento_asistente.json\|http.Client\|GenericEntityPage\|BackendConfig" movil/lib movil/pubspec.yaml`: referencias estructurales presentes.
