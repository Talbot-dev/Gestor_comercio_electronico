# Gestor_comercio_electronico

Backend para gestión de comercio electrónico construido con Spring Boot. El proyecto cubre el ciclo básico de usuarios, productos y órdenes, con seguridad basada en roles y soporte para dos modos de autenticación.

## Características principales

- Gestión de usuarios, productos y órdenes.
- Seguridad con Spring Security y control de acceso por roles.
- Modo de autenticación `manual` para desarrollo local y `keycloak` para integración con un servidor OAuth2.
- Base de datos H2 en memoria para pruebas rápidas.
- Documentación interactiva con Swagger UI.
- Integración con RabbitMQ mediante Docker Compose.

## Stack tecnológico

- Java 21
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Boot OAuth2 Resource Server
- H2 Database
- Springdoc OpenAPI
- Maven
- Docker Compose

## Requisitos

- Java 21
- Maven Wrapper (`./mvnw`) o Maven instalado localmente
- Docker y Docker Compose, si quieres levantar RabbitMQ junto con la aplicación

## Configuración por defecto

La aplicación arranca con el perfil `manual` por defecto:

- `spring.profiles.default=manual`
- `spring.profiles.active=manual`

En este modo se crea o actualiza automáticamente un usuario administrador inicial:

- Usuario: `admin`
- Password: `admin123`
- Rol: `ADMIN`

## Ejecución local

```zsh
./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

## Ejecución con Docker Compose

El archivo `compose.yaml` levanta:

- `rabbitmq` en `5672` y `15672`
- `app` en `8080`

Arranque:

```zsh
docker compose up -d
```

Ver logs de la aplicación:

```zsh
docker compose logs -f app
```

Detener todo:

```zsh
docker compose down
```

## Perfiles de ejecución

### `manual`

- Autenticación local para desarrollo.
- Usa el usuario administrador bootstrap.
- Es el perfil activo por defecto.

### `keycloak`

Activa la validación JWT contra un issuer de Keycloak:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8081/realms/ecommerce-admin
```

Para usarlo, inicia la app con:

```zsh
SPRING_PROFILES_ACTIVE=keycloak ./mvnw spring-boot:run
```

## Endpoints principales

### Usuarios

- `POST /api/public/registro/usuario` — registrar usuario
- `GET /api/usuario/{id}` — consultar usuario por ID
- `GET /api/usuario/nombre/{nombre}` — consultar usuario por nombre
- `PATCH /api/usuario/{id}/rol` — cambiar rol de usuario
- `DELETE /api/usuario/{id}` — eliminar usuario

### Productos

- `GET /api/productos/{id}` — consultar producto por ID
- `GET /api/productos/nombre/{nombre}` — consultar producto por nombre
- `POST /api/productos` — crear producto
- `PUT /api/productos/{id}` — modificar producto
- `DELETE /api/productos/{id}` — eliminar producto

### Órdenes

- `POST /api/orden` — crear orden
- `GET /api/orden/usuario/{usuarioId}` — consultar historial de órdenes de un usuario

## Documentación y pruebas

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

Colección de Postman:

- [Set de pruebas](https://www.postman.com/talbot-systems/workspace/taller-ecommerce-acm/collection/41239914-029f05bc-c561-4409-b52f-719ec6b490a7?action=share&creator=41239914)

Ejecutar tests:

```zsh
./mvnw test
```

## Estructura general

- `config/` — configuración general y seguridad
- `usuario/` — dominio de usuarios
- `producto/` — dominio de productos
- `orden/` — dominio de órdenes
- `shared/` — DTOs, eventos y mapeos comunes

## Notas

- La base de datos H2 se ejecuta en memoria y se recrea al iniciar la aplicación.
- La persistencia usa `create-drop`, por lo que los datos se eliminan al detener la app.
- La integración con Docker Compose está habilitada por defecto en `application.properties`.
