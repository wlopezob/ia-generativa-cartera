# Implementar endpoint

## chat/memory
- usar adk de google
- Implementar el endpoint /api/restaurant/chat/memory
- el request es: {
    "message": "string",
    "sessionId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
  }
- retorna: {
    "response": "string",
    "sessionId": "string"
  }
- dentro del endpoint tiene que usar el **from app.agent.agent import root_agent** con InMemoryRunner donde el user_id de **runner.session_service.create_session** es el sessionId
- la respuesta del LLM tiene que adaptarse al response del endpoint

