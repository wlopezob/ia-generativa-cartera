import json
from langchain_openai import ChatOpenAI
from langchain_core.messages import SystemMessage, HumanMessage
from app.core.settings import settings
from app.models.ruc import RucResponse, ErrorResponse

# LLM prompt template
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

# Initialize the LLM client
def get_llm_client():
    return ChatOpenAI(
        model=settings.openai_model, 
        temperature=settings.openai_temperature, 
        max_tokens=settings.openai_max_tokens
    )

def process_html_with_llm(html_content: str) -> RucResponse | ErrorResponse:
    """
    Process HTML content with LLM to extract RUC information
    
    Args:
        html_content: HTML content from SUNAT website
        
    Returns:
        RucResponse or ErrorResponse object
    """
    llm = get_llm_client()
    
    system_message = SystemMessage(content=PROMPT)
    human_message = HumanMessage(content=html_content)
    messages = [system_message, human_message]
    
    # Get response from LLM
    assistant_message = llm.invoke(messages)
    
    # Parse the response
    try:
        result_dict = json.loads(assistant_message.content)
        return RucResponse(**result_dict)
    except json.JSONDecodeError:
        # If the response is not valid JSON, return an error
        return ErrorResponse(
            status=False,
            error="Failed to parse LLM response as JSON",
            raw_response=assistant_message.content
        ) 