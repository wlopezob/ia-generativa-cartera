"""
Main entry point for the Restaurant API application
"""
from app.main import app
from app.core.config import settings
from dotenv import load_dotenv

#
# ADK Streaming
#

# Load Gemini API Key
load_dotenv()

# No usar uvicorn.run() directamente en el código
# En su lugar, usar el comando: uvicorn main:app --reload

if __name__ == "__main__":
    print(f"\n{settings.app_name} v{settings.app_version}")
    print("\nPara ejecutar el servidor correctamente (especialmente con WebSockets):")
    print(f"    uvicorn app.main:app --host {settings.host} --port {settings.port} --reload\n")
