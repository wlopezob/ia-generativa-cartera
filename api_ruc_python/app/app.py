from fastapi import FastAPI, Query, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from typing import Optional
import string
from dotenv import load_dotenv
from langchain_openai import ChatOpenAI
from langchain_core.messages import SystemMessage, HumanMessage
import random
import requests
import json

# Load environment variables
load_dotenv()

app = FastAPI(
    title="API RUC",
    description="API para consultar información de RUC usando IA",
    version="0.1.0"
)

# Configure CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize the OpenAI ChatBot
llm = ChatOpenAI(model="gpt-4o-mini", temperature=0.5, max_tokens=10000)

# Prompt template
PROMPT = """Extract relevant business or personal data from the provided website text and return it in a structured JSON format.

Extract the following information from the provided text: Número de RUC, NombrePersonaOrEmpresa, Nombre Comercial, Estado del Contribuyente, Condición del Contribuyente, and Domicilio Fiscal. Return this information in JSON format with a "status" key that is true if at least one piece of information is found, or false if none are found. If "status" is false, all other values should be empty strings.

# Output Format

The output should be a JSON object with the following keys:
- "status": A boolean value, true if at least one data point is extracted, and false otherwise.
- "ruc": Always a string, include the extracted data or an empty string if not found.
- "nombrePersonaEmpresa": Always a string, include the extracted data or an empty string if not found.
- "nombreComercial": Always a string, include the extracted data or an empty string if not found.
- "estadoContribuyente": Always a string, include the extracted data or an empty string if not found.
- "condicionContribuyente": Always a string, include the extracted data or an empty string if not found.
- "domicilioFiscal": Always a string, include the extracted data or an empty string if not found.

# Example

**Input:**

"Texto de una página web que incluye el Número de RUC 123456789, una empresa llamada EjemploCorp, Estado del Contribuyente: Activo, Condición del Contribuyente: Habido, y Domicilio Fiscal: Calle Falsa 123."

**Output:**

{
  "status": true,
  "ruc": "123456789",
  "nombrePersonaEmpresa": "EjemploCorp",
  "nombreComercial": "",
  "estadoContribuyente": "Activo",
  "condicionContribuyente": "Habido",
  "domicilioFiscal": "Calle Falsa 123"
}

# Notes

- Ensure "status" is correctly set based on the presence or absence of data.
- Each extracted element should be checked thoroughly to ensure accuracy.
- If only partial data is found, the "status" should still be true, with empty strings for missing elements.
- Return only the json with the extracted data, do not return any additional text, do not return format markdown.                             
"""


@app.get("/")
async def root():
    return {"message": "API RUC operational"}

@app.get("/consulta-ruc")
async def consulta_ruc(ruc: str = Query(..., description="Número de RUC a consultar")):
    try:
        
        # Fetch HTML data from SUNAT
        html_content = consultar_ruc(ruc, generate_key(52))
        # Process with LLM
        system_message = SystemMessage(content=PROMPT)
        human_message = HumanMessage(content=html_content.text)
        messages = [system_message, human_message]
        
        # Get response from LLM
        assistant_message = llm.invoke(messages)
        
        # Parse the response
        try:
            result = json.loads(assistant_message.content)
            return result
        except json.JSONDecodeError:
            # If the response is not valid JSON, return it as text
            return {
                "status": False,
                "error": "Failed to parse LLM response as JSON",
                "raw_response": assistant_message.content
            }
            
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error processing request: {str(e)}")

def generate_key(token_length):
    """
    Genera una cadena aleatoria para simular un token de reCAPTCHA
    
    Args:
        token_length (int): La longitud deseada del token
    
    Returns:
        str: Una cadena aleatoria que simula un token
    """
    random_token = ""
    
    # Genera caracteres aleatorios hasta alcanzar la longitud deseada
    while len(random_token) < token_length:
        # En Python no hay equivalente directo a toString(36), así que usamos
        # caracteres alfanuméricos para simular el mismo comportamiento
        random_chars = ''.join(random.choice(string.ascii_lowercase + string.digits) 
                              for _ in range(8))
        random_token += random_chars
    
    # Recorta la cadena a la longitud exacta solicitada
    return random_token[:token_length]

def consultar_ruc(nro_ruc: str, recaptcha_token: str, timeout: int = 30) -> Optional[requests.Response]:
    """
    Consulta información de un RUC en la página de SUNAT.
    
    Args:
        nro_ruc: Número de RUC a consultar
        recaptcha_token: Token de recaptcha requerido por SUNAT
        timeout: Tiempo máximo de espera para la respuesta (segundos)
    
    Returns:
        Objeto Response o None en caso de error
    """
    url = 'https://e-consultaruc.sunat.gob.pe/cl-ti-itmrconsruc/jcrS00Alias'
    
    headers = {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
        'Accept-Language': 'en-PE,en;q=0.9,es-PE;q=0.8,es;q=0.7,en-GB;q=0.6,en-US;q=0.5',
        'Cache-Control': 'max-age=0',
        'Connection': 'keep-alive',
        'Content-Type': 'application/x-www-form-urlencoded',
        'Origin': 'https://e-consultaruc.sunat.gob.pe',
        'Referer': 'https://e-consultaruc.sunat.gob.pe/cl-ti-itmrconsruc/FrameCriterioBusquedaWeb.jsp',
        'Sec-Fetch-Dest': 'document',
        'Sec-Fetch-Mode': 'navigate',
        'Sec-Fetch-Site': 'same-origin',
        'Sec-Fetch-User': '?1',
        'Upgrade-Insecure-Requests': '1',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36',
        'sec-ch-ua': '"Not(A:Brand";v="99", "Google Chrome";v="133", "Chromium";v="133"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"Windows"'
    }
    
    # Puedes agregar cookies si son necesarias, aunque en general es mejor dejar
    # que la sesión las maneje automáticamente
    cookies = {
        'ITMRCONSRUCSESSION': 'vqZnnHGCby6MtgTHV3WlnSSwqHb84FWrXx5TpJNl2MNfZDdPX198DkyTJ2TzGMVGpgS37dVzh1g00nsJCGpc1y03StT8DHKChrpMh58QGwnST9bTFdPNn4gMKsfW1FnDLYhyHHpWtJVBkbm2Zvl17FskhNPT9dn9kytYk1cyF9XwVQDLk0G6y9WtWvFTDph0kSQnqf8n8v7Tm2F6DvGWCn8rJTD9Dlc7G9XjBX5pqHLvHq0Fx5sTcWgGY0pTHmm7!1196227037!1492188526',
        'TS01fda901': '019edc9eb8bfddb4ddbceffe39a38b30b53583584048024f02a49adf47ba9668170728010030c5f52de7bb03b78c2c4a0180652d672a2f6a05001da905d8feec758d577ef5',
        'TS61ff7286027': '08d0cd49b8ab20008eeaf602b87b742ea951cd7cd04f6879a4eda4d5034a7420ce832938db2ac5ca081bdbe9fc11300076c7211f55e3f54a6bb258f458d8cfe8313a4070136b398fdd8486fd03cb6ff2f25212c99f3340f2e28b9cf03ab4c2c4',
        'TSf3c1dbbd027': '08fe7428c8ab200095bc3f6c5f11c08bde84cfa4076ae0834abf2c939e0e190faf541b4e30c60e9e08bee23fd1113000ee0eac939de6223d53176584761b218532fa79f0a1e4290274873b8a373dc4129b8abf1158cc0c59ae6a4e53a2821406'
    }
    
    data = {
        'accion': 'consPorRuc',
        'razSoc': '',
        'nroRuc': nro_ruc,
        'nrodoc': '',
        'token': recaptcha_token,
        'contexto': 'ti-it',
        'modo': '1',
        'rbtnTipo': '1',
        'search1': nro_ruc,
        'tipdoc': '1',
        'search2': '',
        'search3': '',
        'codigo': ''
    }
    
    try:
        # Crear una sesión para manejar cookies automáticamente
        with requests.Session() as session:
            #session.cookies.update(cookies)
            response = session.post(
                url,
                headers=headers,
                data=data,
                timeout=timeout
            )
            
            # Verificar si la petición fue exitosa
            response.raise_for_status()
            return response
            
    except requests.exceptions.HTTPError as errh:
        print(f"Error HTTP: {errh}")
    except requests.exceptions.ConnectionError as errc:
        print(f"Error de conexión: {errc}")
    except requests.exceptions.Timeout as errt:
        print(f"Timeout error: {errt}")
    except requests.exceptions.RequestException as err:
        print(f"Error en la petición: {err}")
    
    return None

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
