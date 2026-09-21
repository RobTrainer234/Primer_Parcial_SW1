# Ejecución local

## Base de datos

Desde la raíz del repositorio:

```bash
docker compose -f infraestructura/local/docker-compose.yml up -d
```

La base queda disponible en:

```text
jdbc:postgresql://localhost:5432/proyecto_case
usuario: case_user
clave: case_password
```

## Backend CASE

```bash
cd backend-case
mvn spring-boot:run
```

## Swagger

```text
http://localhost:8080/swagger-ui.html
```
