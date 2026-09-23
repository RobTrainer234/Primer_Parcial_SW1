# Seguimiento ODD: IA web Qwen opt-in

## Objetivo
Implementar primero la integración web de IA textual/fotográfica con Qwen como proveedor opt-in, conservando el flujo determinista existente y la compatibilidad de endpoints.

## Alcance
- Configuración explícita y desactivada por defecto.
- Cliente backend hacia endpoint OpenAI-compatible de Qwen.
- Propuesta trazable sujeta a revisión humana.
- Control explícito en Angular para solicitar IA.
- Fallback controlado cuando Qwen está desactivado o no disponible.
- Evidencia de compilación y pruebas disponibles.

## No alcance
- IA local móvil.
- Aplicación automática de propuestas al modelo UML.
- Persistencia productiva de propuestas.
- Embeddings, MediaPipe o TensorFlow Lite.

## Criterios de aceptación
1. El flujo manual actual continúa funcionando sin Qwen.
2. Qwen permanece desactivado por defecto.
3. La invocación sólo ocurre por configuración y acción explícita.
4. Toda respuesta queda registrada como propuesta revisable.
5. Timeout, respuesta inválida y servicio no disponible producen estado controlado.
6. Frontend compila y backend tiene verificación ejecutada o bloqueo documentado.

## Tareas
- [x] Diseñar configuración y contrato de proveedor.
  - `caseapp.ai.text-proposals` maps the guide variables with disabled/default deterministic behavior.
- [x] Implementar cliente/adaptador Qwen y ruta opt-in.
  - Added `POST /projects/{projectId}/proposals/ai-text/qwen`; it only calls Qwen when enabled and provider is `qwen`.
- [x] Integrar control explícito en Angular.
  - Existing manual text proposal button remains unchanged; Qwen text has its own button and API method.
- [x] Agregar/verificar pruebas y documentación.
  - Added focused Qwen client unit coverage for OpenAI-compatible, non-2xx, and invalid JSON responses.
  - Local Docker E2E reached Qwen and produced `READY_FOR_REVIEW`; full CU matrix is in `documentacion/requisitos/RESULTADO_PRUEBAS_LOCALES_CU.md`.
