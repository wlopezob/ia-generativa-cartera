from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.config import settings
from app.api.v1.router import api_router
from fastapi.staticfiles import StaticFiles
from pathlib import Path
from fastapi.responses import FileResponse
import os

def create_app() -> FastAPI:
    """
    Create and configure FastAPI application
    
    Returns:
        FastAPI: Configured FastAPI application instance
    """
    
    # Create FastAPI instance
    app = FastAPI(
        title=settings.app_name,
        description=settings.app_description,
        version=settings.app_version,
        debug=settings.debug,
        docs_url="/docs",
        redoc_url="/redoc",
        openapi_url="/openapi.json"
    )
    
    # Add CORS middleware
    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.allowed_origins,
        allow_credentials=True,
        allow_methods=settings.allowed_methods,
        allow_headers=settings.allowed_headers,
    )
    
    # Include API routers
    app.include_router(api_router)
    
    STATIC_DIR = Path("static")
    app.mount("/static", StaticFiles(directory=STATIC_DIR), name="static")

    @app.get("/static")
    async def root():
        """Serves the index.html"""
        return FileResponse(os.path.join(STATIC_DIR, "index.html"))

    # Root endpoint
    @app.get("/", tags=["Root"])
    async def root():
        """Root endpoint"""
        return {
            "message": f"Welcome to {settings.app_name}",
            "version": settings.app_version,
            "docs": "/docs",
            "health": "/api/v1/health",
            "websocket": {
                "endpoint": "/api/v1/streaming/ws/{user_id}",
                "description": "WebSocket endpoint for restaurant AI assistant",
                "parameters": {
                    "user_id": "Unique user identifier",
                    "is_audio": "Optional: 'true' or 'false' (default: 'false')"
                }
            }
        }
    
    return app


# Create app instance
app = create_app()
