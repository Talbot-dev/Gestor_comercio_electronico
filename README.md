# Gestor_comercio_electronico

Este proyecto es una implementación ligera de un backend para e-commerce, enfocado en la gestión de usuarios, productos y órdenes.
Su propósito principal es servir como entorno de experimentación y práctica sobre el ecosistema de Spring.

---

## Tecnologias usadas

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server (autenticación con keycloak)
- H2 Database
- Springdoc OpenAPI (Swagger UI)
- Maven
- Docker Compose

## Ejecución 

### EJecución local

```zsh
./mvnw spring-boot:run
```

La app se ejecuta por defecto en `http://localhost:8080` con perfil `manual`.

---

### Ejecución con Docker

```zsh
docker compose up -d
```

Docker levanta la aplicacion en un contenedor Maven. Spring Boot no intenta arrancar `compose.yaml` automaticamente al iniciar la app en local.

Ver logs:

```zsh
docker compose logs -f app
```

Para detener:

```zsh
docker compose down
```
---

## Acceso inicial

En el perfil `manual`, se genera automáticamente un usuario administrador al iniciar la aplicación:

- Usuario: `admin`
- Password: `admin123`
- Rol: `ADMIN`
  
Esto permite interactuar con la API desde el primer momento sin configuración adicional.

---

## Pruebas de la API

Se incluye una colección de pruebas en Postman para facilitar la exploración de los endpoints y validar el comportamiento de la API:

- Colección Postman: [`Set de pruebas.`](https://www.postman.com/talbot-systems/workspace/taller-ecommerce-acm/collection/41239914-029f05bc-c561-4409-b52f-719ec6b490a7?action=share&creator=41239914)

Adicionalmente, la API puede inspeccionarse y probarse directamente desde Swagger UI:

http://localhost:8080/swagger-ui/index.html

Swagger permite visualizar los endpoints disponibles, sus contratos y ejecutar requests de forma interactiva sin necesidad de herramientas externas.

---
## Enfoque y decisiones
Este proyecto prioriza simplicidad y claridad sobre robustez:

- Uso de perfiles (`manual` / `keycloak`) para desacoplar la autenticación del dominio.
- Configuración por defecto orientada a desarrollo rápido, sin dependencias externas.
- Base de datos en memoria para facilitar reinicios limpios y pruebas rápidas.
- Bootstrap de usuario administrador para reducir fricción al probar endpoints.
- Estructura en capas (`controller`, `service`, `repository`, `model`) para mantener separación de responsabilidades sin sobreingeniería.
