# CRUD de Personas - MCP Server

Este proyecto implementa un CRUD completo para gestionar personas usando Spring WebFlux, R2DBC, H2 Database y MCP Server Tools.

## Características

- **Spring WebFlux**: Programación reactiva
- **R2DBC**: Base de datos reactiva
- **H2 Database**: Base de datos en memoria
- **Lombok**: Reducir código boilerplate
- **MCP Server Tools**: Herramientas para integración con sistemas de IA

## Modelo de Datos

### Entidad Persona
- **id**: Long (Auto-incrementado)
- **nombre**: String (requerido)
- **apellido**: String (requerido)
- **edad**: Integer (requerido)
- **fecha**: LocalDate (requerido)
- **tipoPersona**: Enum (PADRE, MADRE, HIJO)

## Endpoints REST

### API Principal (`/api/personas`)

- `GET /api/personas` - Listar todas las personas
- `GET /api/personas/{id}` - Obtener persona por ID
- `POST /api/personas` - Crear nueva persona
- `PUT /api/personas/{id}` - Actualizar persona
- `DELETE /api/personas/{id}` - Eliminar persona
- `GET /api/personas/tipo/{tipoPersona}` - Listar por tipo
- `GET /api/personas/search/nombre?nombre={nombre}` - Buscar por nombre
- `GET /api/personas/search/apellido?apellido={apellido}` - Buscar por apellido
- `GET /api/personas/edad?edadMin={min}&edadMax={max}` - Buscar por rango de edad

### API MCP Tools (`/mcp/personas`)

- `POST /mcp/personas/crear` - Crear persona (MCP Tool)
- `GET /mcp/personas/listar` - Listar todas (MCP Tool)
- `GET /mcp/personas/{id}` - Obtener por ID (MCP Tool)
- `GET /mcp/personas/tipo/{tipoPersona}` - Listar por tipo (MCP Tool)
- `GET /mcp/personas/buscar/nombre/{nombre}` - Buscar por nombre (MCP Tool)
- `GET /mcp/personas/buscar/apellido/{apellido}` - Buscar por apellido (MCP Tool)
- `GET /mcp/personas/edad?edadMin={min}&edadMax={max}` - Buscar por edad (MCP Tool)
- `DELETE /mcp/personas/{id}` - Eliminar (MCP Tool)
- `PUT /mcp/personas/{id}` - Actualizar (MCP Tool)

## Ejemplo de Uso

### Crear una persona
```bash
curl -X POST http://localhost:8085/api/personas \
-H "Content-Type: application/json" \
-d '{
  "nombre": "Juan",
  "apellido": "Pérez",
  "edad": 30,
  "fecha": "1993-05-15",
  "tipoPersona": "PADRE"
}'
```

### Listar todas las personas
```bash
curl -X GET http://localhost:8085/api/personas
```

### MCP Tool - Crear persona
```bash
curl -X POST "http://localhost:8085/mcp/personas/crear?nombre=Maria&apellido=García&edad=35&fecha=1988-03-20&tipoPersona=MADRE"
```

## Configuración

El proyecto está configurado para usar:
- Puerto: 8085
- Base de datos H2 en memoria
- Console H2 habilitada
- CORS habilitado para todas las rutas

## Ejecución

```bash
mvn clean install
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8085`

## Base de Datos

Se incluyen datos de ejemplo:
- Juan Pérez (PADRE, 45 años)
- María García (MADRE, 42 años)
- Carlos Pérez (HIJO, 18 años)
- Ana López (MADRE, 38 años)
- Pedro Martínez (PADRE, 50 años)
- Sofía Rodríguez (HIJO, 16 años)

## MCP Server Integration

Este proyecto incluye herramientas MCP que pueden ser utilizadas por sistemas de IA para:
- Crear personas de forma programática
- Consultar información de personas
- Realizar búsquedas avanzadas
- Gestionar el CRUD completo

Las herramientas MCP están disponibles a través de endpoints REST específicos que pueden ser invocados por agentes de IA o sistemas externos.
