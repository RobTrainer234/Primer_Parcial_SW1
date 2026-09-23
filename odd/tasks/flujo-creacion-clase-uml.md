# Flujo explícito de creación de clase UML

## Problema
La herramienta `entity` queda activa después de crear una clase y cada clic posterior crea otra clase automática `NuevaClaseN`. La paleta `Nodos Estructurales` no explica el flujo ni distingue capacidades implementadas.

## Diseño decidido
1. El usuario pulsa `+ Clase` o `Clase UML`.
2. Se abre diálogo `Nueva clase UML`.
3. El usuario ingresa nombre obligatorio y confirma.
4. La herramienta queda en modo `Colocar clase`.
5. Un único clic en canvas crea la clase en esa posición.
6. La herramienta vuelve automáticamente a `Selección`.
7. Se muestra toast de confirmación.

## Reglas
- No crear clases con nombre automático por clic accidental.
- No permitir nombre vacío.
- Cancelar no cambia el modelo.
- La paleta usa español claro: `Elementos UML`, `Clase UML persistible`, `Relaciones UML`, `Restricción OCL`.
- Interfaz no se ofrece como acción funcional si no existe persistencia/metamodelo correspondiente; se puede documentar como capacidad futura.
- Preservar UML 2.5 y contratos existentes.

## Aceptación
- Un click de herramienta no crea una clase inmediatamente.
- Confirmar nombre y hacer un clic crea exactamente una clase.
- El segundo clic no crea otra clase porque la herramienta vuelve a selección.
- Clase creada se persiste si hay modelo activo.
- Angular compila.
