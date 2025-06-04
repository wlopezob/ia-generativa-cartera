from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field
from google.genai.types import Content, Part
from app.core.config import settings
from app.core.adk_runner import runner
import json

router = APIRouter(prefix="/chat", tags=["Chat"])

class ChatMemoryRequest(BaseModel):
    message: str = Field(..., description="User message")
    sessionId: str = Field(..., description="Session ID")

class ChatMemoryResponse(BaseModel):
    response: str = Field(..., description="Agent response")
    sessionId: str = Field(..., description="Session ID")

@router.post("/memory", response_model=ChatMemoryResponse)
async def chat_memory(request: ChatMemoryRequest):
    """
    Chat with memory using Google ADK agent and InMemoryRunner.
    """
    # Obtén o crea la sesión usando el sessionId
    session = await runner.session_service.get_session(
        app_name=settings.app_name,
        user_id=request.sessionId,
        session_id=request.sessionId
    )
    if not session:
        # Si la sesión no existe, créala
        session = await runner.session_service.create_session(
            app_name=settings.app_name,
            user_id=request.sessionId,
            session_id=request.sessionId
        )
        print(f"New session created: {request.sessionId}")
    
    # Envía el mensaje al agente
    content = Content(role="user", parts=[Part.from_text(text=request.message)])
    final_response_content = ""
    async for event in runner.run_async(
        user_id=request.sessionId,
        session_id=request.sessionId,
        new_message=content
    ):
        if event.is_final_response() and event.content and event.content.parts:
            # Print the final response content
            final_response_content = event.content.parts[0].text
            print(f"Final Response: {final_response_content}")
    if not final_response_content:
        raise HTTPException(status_code=500, detail="No response from agent.")
    
    stored_response = session.state.get("agent_restaurant_response");
    try:
        parsed_output = json.loads(stored_response);
        print(f"Parsed Output: {json.dumps(parsed_output, indent=2)}");
    except (json.JSONDecodeError, TypeError):
         # Otherwise, print as string
        print(f"Stored Response: {stored_response}")

    return ChatMemoryResponse(response=final_response_content, sessionId=request.sessionId) 