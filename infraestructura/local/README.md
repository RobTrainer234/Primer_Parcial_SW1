# Ejecución local

## Ejecutar frontend, backend y base de datos con Docker Compose

Desde la raíz del repositorio:

```bash
docker compose -f infraestructura/local/docker-compose.yml up --build -d
```

El archivo Compose declara el proyecto `Parcial_SW1`; si querés fijarlo desde la CLI podés usar:

```bash
docker compose -p parcial_sw1 -f infraestructura/local/docker-compose.yml up --build -d
```

Servicios publicados en el host:

```text
frontend: http://localhost:4200
backend:  http://localhost:8080
swagger:  http://localhost:8080/swagger-ui.html
postgres: jdbc:postgresql://localhost:5432/proyecto_case
usuario:  case_user
clave:    case_password
```

El frontend Angular se compila en una imagen multi-stage de Node y se sirve con nginx. La aplicación mantiene la URL de API `http://localhost:8080` para que el navegador local llegue al backend publicado por Compose sin cambiar contratos REST. El Compose reutiliza los volúmenes existentes `local_datos_case` y `local_artefactos_case` para conservar datos previos.

Qwen no se levanta como servicio de Compose. El backend queda configurado para contactar Docker Model Runner en el host mediante:

```text
http://host.docker.internal:12434/engines/v1/chat/completions
```

La IA queda deshabilitada por defecto. Para habilitar propuestas de texto con Qwen, levantá Compose con la variable explícita:

```bash
AI_TEXT_PROPOSALS_ENABLED=true docker compose -f infraestructura/local/docker-compose.yml up --build -d
```

## Ejecutar backend con Maven local

Si Maven está instalado:

```bash
cd backend-case
mvn spring-boot:run
```

## Swagger

```text
http://localhost:8080/swagger-ui.html
```
