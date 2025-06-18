from google.adk.agents import LlmAgent
from google.adk.tools.mcp_tool.mcp_toolset import MCPToolset
from google.adk.tools.mcp_tool.mcp_session_manager import SseServerParams
from google.adk.tools import load_memory
from datetime import datetime

def get_current_date():
    """
    Gets the current date and time in a format that the LLM can easily understand.
    
    Returns:
        dict: A dictionary with the current date and time in different formats.
    """
    now = datetime.now()
    
    # Spanish month names
    months = [
        "enero", "febrero", "marzo", "abril", "mayo", "junio",
        "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
    ]
    
    # Spanish weekday names
    weekdays = [
        "lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"
    ]
    
    # The weekday is a number from 0 to 6, where 0 is Monday
    weekday = weekdays[now.weekday()]
    
    # Natural format in Spanish
    natural_date = f"{weekday}, {now.day} de {months[now.month-1]} de {now.year}"
    
    return {
        "natural_date": natural_date,
        "full_date": now.strftime("%d/%m/%Y %H:%M:%S"),
        "date_only": now.strftime("%d/%m/%Y"),
        "time_only": now.strftime("%H:%M:%S"),
        "day": now.day,
        "month": now.month,
        "year": now.year,
        "month_name": months[now.month-1],
        "weekday": weekday,
        "hour": now.hour,
        "minute": now.minute,
        "second": now.second,
        "timestamp": now.timestamp()
    }

root_agent = LlmAgent(
#   # A unique name for the agent.
   name="agent_managment_person",
   model= "gemini-2.5-pro", #"gemini-2.5-pro-preview-06-05",
   description="""
   Agent to managment of people and transaction money.
   """,
   # Instructions to set the agent's behavior.
   instruction="""
   You are assistant that help with the managment of people and transaction money.
        MAIN INSTRUCTIONS:
        - Always answer in spanish.
        - Use emoticons to make the conversation more engaging.
        - Use the tools to get the information you need.
        - You are friendly and professional.
        - Always use the **get_current_date** tool to get the current date and time.
           This will provide you with the date in natural format (e.g., "lunes, 15 de mayo de 2023") and 
           also structured information like day, month, year, etc, So you can use this information to perform calculations.

   """,
   tools=[
      get_current_date,
      MCPToolset(
         connection_params=SseServerParams(
            url="http://localhost:8085/sse",
            timeout=30,
            sse_read_timeout=60*5
         )
      )
   ],
   output_key="agent_response"
)

