# Gestor_comercio_electronico

Proyecto demo pequeno de administracion para e-commerce (usuarios, productos y ordenes), pensado para practicar Spring Boot.
No esta orientado a produccion: la idea es aprender y probar rapido.

## Tecnologias usadas

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server (para modo Keycloak)
- H2 Database (en memoria)
- Springdoc OpenAPI (Swagger UI)
- Maven
- Docker Compose

## Ejecucion rapida

### Opcion 1: local con Maven

```zsh
./mvnw spring-boot:run
```

La app levanta por defecto en `http://localhost:8080` con perfil `manual`.

### Opcion 2: con Docker Compose (solo app)

```zsh
docker compose up -d
```

Para ver logs:

```zsh
docker compose logs -f app
```

Para detener:

```zsh
docker compose down
```

## Credenciales de prueba

Con perfil `manual`, se crea (o actualiza) un admin por defecto al iniciar:

- Usuario: `admin`
- Password: `admin123`
- Rol: `ADMIN`

## Decisiones tecnicas (resumen)

- Se trabaja por perfiles de Spring (`manual` / `keycloak`) para cambiar estrategia de autenticacion sin tocar codigo de negocio.
- El perfil por defecto es `manual`, ideal para desarrollo/demo sin depender de Keycloak.
- Base de datos H2 en memoria para arranque rapido y entorno descartable.
- Usuario admin bootstrap para tener acceso inmediato despues de levantar el proyecto.
- Arquitectura en capas (`controller`, `service`, `repository`, `model`) para mantener el codigo simple y ordenado.
