# MCP CRUD Server - Personas

Este proyecto implementa un servidor MCP (Model Context Protocol) con un CRUD completo de Personas usando Spring Boot, WebFlux, R2DBC y H2.

## Configuración Simplificada

### ¿Por qué no necesitamos DatabaseConfig?

Spring Boot proporciona **auto-configuración** para R2DBC, lo que significa que toda la configuración se puede hacer a través de properties en `application.yml`:

```yaml
spring:
  r2dbc:
    url: r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: ""
    pool:
      enabled: true
      initial-size: 10
      max-size: 20
  sql:
    init:
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
      mode: always
```

### Ventajas de usar Properties vs DatabaseConfig:

1. **Simplicidad**: No necesitas crear clases de configuración adicionales
2. **Mantenibilidad**: Toda la configuración está centralizada en un archivo
3. **Flexibilidad**: Fácil cambio entre entornos (dev, test, prod)
4. **Convención**: Sigue las mejores prácticas de Spring Boot

## Arquitectura del Proyecto

```
src/main/java/com/wlopezob/crud/
├── entity/
│   ├── Persona.java           # Entidad JPA/R2DBC
│   └── TipoPersona.java       # Enum para tipos de persona
├── repository/
│   └── PersonaRepository.java # Repository reactivo
├── service/
│   └── PersonaService.java    # Lógica de negocio
├── controller/
│   ├── PersonaController.java    # REST API
│   └── PersonaMcpController.java # MCP Tools
├── dto/
│   └── PersonaRequest.java    # DTOs para requests
├── config/
│   ├── R2dbcConfig.java       # Configuración mínima R2DBC
│   └── WebFluxConfig.java     # Configuración WebFlux
└── mcp/
    └── PersonaMcpTools.java   # Herramientas MCP
```

## Entidad Persona

- **id**: Identificador único (auto-generado)
- **nombre**: Nombre de la persona
- **apellido**: Apellido de la persona
- **edad**: Edad en años
- **fecha**: Fecha de nacimiento
- **tipoPersona**: Enum (PADRE, MADRE, HIJO)

## API Endpoints

### REST API
- `GET /api/personas` - Listar todas las personas
- `GET /api/personas/{id}` - Obtener persona por ID
- `POST /api/personas` - Crear nueva persona
- `PUT /api/personas/{id}` - Actualizar persona
- `DELETE /api/personas/{id}` - Eliminar persona

### MCP Tools
- `create_persona` - Crear una nueva persona
- `list_personas` - Listar todas las personas

## Tecnologías Utilizadas

- ✅ **Spring Boot 3.5.0** - Framework principal
- ✅ **Spring WebFlux** - Programación reactiva
- ✅ **Spring Data R2DBC** - Acceso reactivo a datos
- ✅ **H2 Database** - Base de datos en memoria
- ✅ **Spring AI MCP Server** - Herramientas MCP
- ✅ **Lombok** - Reducir boilerplate code
- ✅ **Maven** - Gestión de dependencias

## Ejecución

```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run
```

## Acceso

- **Aplicación**: http://localhost:8085
- **Consola H2**: http://localhost:8085/h2-console
- **API REST**: http://localhost:8085/api/personas

## Configuración H2

En la consola H2, usar estos datos de conexión:
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **User Name**: `sa`
- **Password**: (dejar vacío)

## Ejemplo de uso MCP

```json
{
  "tool": "create_persona",
  "parameters": {
    "nombre": "Juan",
    "apellido": "Pérez",
    "edad": 30,
    "fecha": "1993-06-17",
    "tipoPersona": "PADRE"
  }
}
```
