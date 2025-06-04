"""
API v1 router configuration
"""
from fastapi import APIRouter
from app.api.v1 import health, streaming, chat

# Create API v1 router
api_router = APIRouter(prefix="/api/restaurant")

# Include routers
api_router.include_router(health.router)
api_router.include_router(streaming.router)
api_router.include_router(chat.router)
