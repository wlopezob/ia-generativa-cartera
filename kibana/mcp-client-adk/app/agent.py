from google.adk.agents import Agent
from google.adk.tools.mcp_tool.mcp_toolset import MCPToolset
from google.adk.tools.mcp_tool.mcp_session_manager import SseServerParams
from google.adk.tools import load_memory

root_agent = Agent(
#   # A unique name for the agent.
   name="agent_kibana_assistant",
   model="gemini-2.5-pro-preview-06-05", # if this model does not work, try below
   description="""
   Eres un asistente para consulta de indices y logs de kibana y perfiles de usuario.
   """,
   # Instructions to set the agent's behavior.
   instruction="""
   Eres un asistente experto en consulta de indices y logs de kibana y perfiles de usuario.
        INSTRUCCIONES IMPORTANTES:
        1. Cuando el usuario te pida un indice, debes obtenerlos de los tools del MCP.
        2. Cuando el usuario solicita un log, debes de solicitar los datos obligatorios, luego analizar el log y responderle al usuario con el resultado del analisis.
       
        Responde siempre en español de manera profesional y amigable.   
   """,
   tools=[
      #load_memory,
      MCPToolset(
         connection_params=SseServerParams(
            url="http://localhost:8084/sse",
            timeout=30,
            sse_read_timeout=60*5
         )
      )
   ],
   output_key="agent_kibana_response"
)

