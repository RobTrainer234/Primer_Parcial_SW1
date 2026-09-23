# Plan de implementación fases 0–5 — Identidad académica persistente

## Decisión adoptada
Se implementará identidad académica persistente. Se conservarán usuarios demo sembrados, pero usuarios, sesiones, membresías, roles, capacidades e historial serán persistentes y autorizados dentro del alcance académico. No se implementará OAuth/JWT productivo en estas fases.

## Alcance
- Fase 0: congelar contratos, roles, estados, capacidades, errores y límites.
- Fase 1: persistencia de usuarios/sesiones, actor actual, autorización central y auditoría.
- Fase 2: CU1 y CU2 con visibilidad, sesión, proyectos, modelos y revisiones recuperables.
- Fase 3: CU3 con miembros, invitaciones, roles, capacidades e historial.
- Fase 4: CU4 con diagramas/vistas, colaboradores, administrador, acceso efectivo e historial.
- Fase 5: CU5 con semántica UML persistente: clases, atributos, operaciones y relaciones binarias soportadas.

## Principios para evitar retrabajo
1. Un servicio canónico de identidad/autorización; las rutas españolas/inglesas sólo adaptan contratos.
2. Migraciones aditivas y backfill explícito; no borrar datos existentes.
3. Toda mutación autorizada registra auditoría append-only.
4. El frontend no decide permisos: sólo refleja las capacidades que devuelve backend.
5. Estado semántico y layout permanecen separados.
6. Operaciones UML no soportadas se documentan; no se exponen controles falsos.
7. Cada fase debe compilar y tener evidencia antes de iniciar la siguiente.

## Gates
- Fase 0: contratos y matriz de aceptación documentados.
- Fase 1: migraciones, actor actual y autorización base compilados/probados.
- Fase 2: sesión/proyectos/modelos/revisiones recuperables.
- Fase 3: CU3 persistente y protegido con pruebas positivas/negativas.
- Fase 4: CU4 persistente, acceso efectivo y auditoría.
- Fase 5: CRUD UML soportado, operaciones persistentes y relaciones tipadas.
- Cierre: build Docker backend/frontend, salud, pruebas automatizadas disponibles y matriz manual.

## Limitaciones explícitas
- La identidad es académica/demo, no autenticación productiva.
- Usuarios demo se siembran por migración.
- No se agrega OAuth, recuperación de contraseña ni proveedor externo.
- Asociación n-aria, clase asociación y metamodelo UML completo requieren una fase posterior.
