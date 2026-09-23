# Validador de subconjunto UML 2.5

## Objetivo
Implementar un subconjunto estructural explícito y verificable de UML 2.5 para diagramas de clases del proyecto.

## Alcance semántico
- Class: nombre válido y estereotipo permitido.
- Property: nombre, tipo de dato y visibilidad representada.
- Identificadores: al menos una propiedad `clavePrimaria` por entidad persistible.
- Restricciones: obligatorio y único.
- Association: origen/destino del mismo modelo, nombre/rol opcional y verbo recomendado.
- Multiplicidades: cardinalidades soportadas por el contrato actual (`1` y `0..*`).
- Generalización/composición: tipo visual reconocido por el frontend; si no está persistido, se marca como limitación.
- Operations: representación visual documentada, sin generación avanzada en esta iteración.
- OCL: errores de consistencia básicos del metamodelo.

## Reglas mínimas
- UML25-CLASS-001: clase/entidad con nombre no vacío y máximo 120 caracteres.
- UML25-CLASS-002: nombre compatible con identificador de clase Java.
- UML25-PROP-001: atributo con nombre y tipo soportado.
- UML25-PROP-002: no duplicar nombres de atributos en una clase.
- UML25-PROP-003: una entidad persistible debe tener una clave primaria.
- UML25-ASSOC-001: origen y destino pertenecen al modelo.
- UML25-ASSOC-002: multiplicidad declarada en ambos extremos.
- UML25-ASSOC-003: nombre o verbo recomendado para asociación de dominio.
- UML25-ASSOC-004: no permitir relación de una entidad consigo misma en el perfil actual.
- UML25-MODEL-001: no duplicar nombres de clases en un modelo.

## Declaración académica
El producto implementa un subconjunto estructural controlado de UML 2.5; no afirma implementar todos los diagramas ni toda la semántica del estándar.

## Criterios de aceptación
- Backend devuelve errores con códigos UML25.
- Modelo válido puede continuar a generación.
- Modelo inválido bloquea generación con explicación.
- Frontend muestra que la validación corresponde al subconjunto UML 2.5 controlado.
- Tests backend cubren al menos reglas positivas y negativas.
