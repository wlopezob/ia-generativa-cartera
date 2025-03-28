## DOC
https://github.com/aurelio-labs/langchain-course

## Create Project
``` sh
uv python install 3.12.7
uv venv --python 3.12.7
## init the project
uv init
```

# API RUC Python

API para consultar información de RUC utilizando inteligencia artificial.

## Instalación

1. Clonar el repositorio
2. Instalar las dependencias:
   ```
   pip install -r requirements.txt
   ```
3. Copiar el archivo `.env.example` a `.env` y completar con tus credenciales:
   ```
   cp .env.example .env
   ```
4. Editar el archivo `.env` y agregar tu API key de OpenAI

## Uso

Para iniciar el servidor:

```
uvicorn app:app --reload
```

La API estará disponible en `http://localhost:8000`

### Endpoints

- `GET /`: Verificar que la API está funcionando
- `GET /consulta-ruc?ruc=20123456789`: Consultar información de un RUC

## Ejemplo de respuesta

```json
{
  "status": true,
  "ruc": "20123456789",
  "nombrePersonaEmpresa": "EMPRESA EJEMPLO S.A.C",
  "nombreComercial": "EMPRESA EJEMPLO",
  "estadoContribuyente": "ACTIVO",
  "condicionContribuyente": "HABIDO",
  "domicilioFiscal": "AV. EJEMPLO 123, LIMA - LIMA - LIMA"
}
```

## Notas

- En esta implementación de ejemplo, la API no hace una consulta real a SUNAT, sino que simula una respuesta.
- Para una implementación en producción, se deberá modificar la función `fetch_ruc_info` para hacer una solicitud real a SUNAT.