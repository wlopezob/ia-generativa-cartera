"""
Chat schemas for restaurant chat endpoint
"""
from pydantic import BaseModel, Field
from uuid import UUID


class ChatRequest(BaseModel):
    """Chat request model"""
    message: str = Field(..., description="User message")
    sessionId: UUID = Field(..., description="Session identifier")


class ChatResponse(BaseModel):
    """Chat response model"""
    response: str = Field(..., description="Agent response")
    sessionId: str = Field(..., description="Session identifier") 