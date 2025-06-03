"""
Main entry point for the Restaurant API application
"""
import uvicorn
from app.main import app
from app.core.config import settings
from dotenv import load_dotenv

#
# ADK Streaming
#

# Load Gemini API Key
load_dotenv()

def main():
    """Run the FastAPI application"""
    print(f"Starting {settings.app_name} v{settings.app_version}")
    print(f"Server will be available at: http://{settings.host}:{settings.port}")
    print(f"API Documentation: http://{settings.host}:{settings.port}/docs")
    print(f"Health Check: http://{settings.host}:{settings.port}/api/v1/health")
    
    uvicorn.run(
        "app.main:app",
        host=settings.host,
        port=settings.port,
        reload=settings.debug,
        log_level="info" if not settings.debug else "debug"
    )


if __name__ == "__main__":
    main()
