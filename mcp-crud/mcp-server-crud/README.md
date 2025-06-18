# Person Management System with MCP Server

This project implements a complete CRUD system for managing people using Spring WebFlux, R2DBC, H2 Database, and MCP Server Tools.

## Features

- **Spring WebFlux**: Reactive programming
- **R2DBC**: Reactive database access
- **H2 Database**: In-memory database
- **Lombok**: Reduce boilerplate code
- **MCP Server Tools**: Tools for AI system integration
- **Maven**
- **Java 21**

## Data Model

### Person Entity
- **id**: Long (Auto-incremented)
- **nombre** (name): String (required)
- **apellido** (last name): String (required)
- **edad** (age): Integer (required)
- **fecha** (date): LocalDate (required)
- **tipoPersona** (person type): Enum (PADRE, MADRE, HIJO) - (FATHER, MOTHER, CHILD)
- **dni** (national ID): String (required, unique)
- **saldo** (balance): Double (optional)

### Transaction Entity
- **id**: Long (Auto-incremented)
- **senderPersonaId**: Long (required) - ID of the person sending money
- **receiverPersonaId**: Long (required) - ID of the person receiving money
- **fecha** (date): LocalDateTime (required) - Transaction date and time
- **monto** (amount): Double (required) - Transaction amount


### Available MCP Tools

#### Person Tools

1. **listarPersonas** - List all people
   - Returns a list of all people in the system
   - No parameters required

2. **crearPersona** - Create a new person
   - Parameters:
     - `nombre` (required): Person's first name
     - `apellido` (required): Person's last name
     - `edad` (required): Person's age
     - `fecha` (required): Birth date in dd/MM/yyyy format
     - `tipoPersona` (required): Person type (PADRE, MADRE, HIJO)
     - `dni` (required): National ID (must be unique)
     - `saldo` (optional): Person's balance

3. **actualizarPersona** - Update an existing person
   - Parameters:
     - `id` (required): Person's ID
     - `nombre` (required): Person's first name
     - `apellido` (required): Person's last name
     - `edad` (required): Person's age
     - `fecha` (required): Birth date in dd/MM/yyyy format
     - `tipoPersona` (required): Person type (PADRE, MADRE, HIJO)
     - `dni` (required): National ID (must be unique)
     - `saldo` (optional): Person's balance

4. **eliminarPersona** - Delete a person
   - Parameters:
     - `id` (required): Person's ID

5. **buscarPersonaPorId** - Find a person by ID
   - Parameters:
     - `id` (required): Person's ID

#### Transaction Tools
1. **crearTransaccion** - Create a new transaction (money transfer)
   - Parameters:
     - `senderPersonaId` (required): ID of the person sending money
     - `receiverPersonaId` (required): ID of the person receiving money
     - `monto` (required): Transaction amount
   - Validates that the sender has sufficient balance

2. **buscarTransaccionPorId** - Find a transaction by ID
   - Parameters:
     - `id` (required): Transaction ID

3. **listarTransaccionesPorPersona** - List all transactions for a person
   - Parameters:
     - `personaId` (required): Person's ID
   - Returns both sent and received transactions

### Execute MCP SERVER

### Inspector MCP
1. execute inspector
```bash
npx @modelcontextprotocol/inspector
```
2. connect with
![setting](docs/img/setting.png)
3. tool list
![setting](docs/img/tools.png)