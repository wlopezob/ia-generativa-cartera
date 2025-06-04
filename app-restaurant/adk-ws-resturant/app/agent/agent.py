from google.adk.agents import Agent
from google.adk.tools.mcp_tool.mcp_toolset import MCPToolset
from google.adk.tools.mcp_tool.mcp_session_manager import SseServerParams
from google.adk.tools import load_memory

root_agent = Agent(
#   # A unique name for the agent.
   name="agent_restaurant_assistant",
   # The Large Language Model (LLM) that agent will use.
   model="gemini-2.0-flash-exp", # if this model does not work, try below
   #model="gemini-2.0-flash-live-001",
   # A short description of the agent's purpose.
   description="""
   Eres un asistente experto en gestión de restaurantes con acceso a herramientas especializadas.
   """,
   # Instructions to set the agent's behavior.
   instruction="""
   Eres un asistente experto en gestión de restaurantes con acceso a herramientas especializadas.
        INSTRUCCIONES IMPORTANTES:
        1. Analiza cuidadosamente cada solicitud del usuario
        2. Puedes y DEBES usar múltiples herramientas si es necesario para completar la tarea
        3. Ejecuta las herramientas en el orden lógico correcto
        4. Si necesitas información de múltiples fuentes, no dudes en llamar varias herramientas
        5. Siempre proporciona respuestas completas y detalladas basadas en los datos obtenidos
        6. Siempre debes obtener los menús disponibles en el restaurante para poder responder las preguntas del usuario
        
        FLUJO DE TRABAJO:
        - Para consultas sobre menús: primero obtén el menú, luego los detalles si es necesario
        - Puedes combinar información de diferentes herramientas para dar respuestas más completas
        
        Responde siempre en español de manera profesional y amigable.   
   """,
   tools=[
      #load_memory,
      MCPToolset(
         connection_params=SseServerParams(
            url="http://localhost:8080/sse",
            timeout=30,
            sse_read_timeout=60*5
         )
      )
   ],
   output_key="agent_restaurant_response"
)

