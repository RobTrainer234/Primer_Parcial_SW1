# Ejecución local

## Ejecutar backend y base de datos con Docker

Desde la raíz del repositorio:

```bash
docker compose -f infraestructura/local/docker-compose.yml up --build -d
```

La base queda disponible en:

```text
jdbc:postgresql://localhost:5432/proyecto_case
usuario: case_user
clave: case_password
```

El backend queda disponible en:

```text
http://localhost:8080
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
