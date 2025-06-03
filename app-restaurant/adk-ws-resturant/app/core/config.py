"""
Core configuration settings for the restaurant API
"""
from pydantic_settings import BaseSettings
from pydantic import Field


class Settings(BaseSettings):
    """Application settings"""
    
    # API Settings
    app_name: str = Field(default="Restaurant API", description="Application name")
    app_version: str = Field(default="0.1.0", description="Application version")
    app_description: str = Field(default="Restaurant management API", description="Application description")
    
    # Server Settings
    host: str = Field(default="0.0.0.0", description="Host to bind the server")
    port: int = Field(default=8000, description="Port to bind the server")
    debug: bool = Field(default=False, description="Debug mode")
    
    # CORS Settings
    allowed_origins: list[str] = Field(default=["*"], description="Allowed CORS origins")
    allowed_methods: list[str] = Field(default=["*"], description="Allowed CORS methods")
    allowed_headers: list[str] = Field(default=["*"], description="Allowed CORS headers")
    
    # Google AI Settings
    google_genai_use_vertexai: str = Field(default="FALSE", description="Use Vertex AI for Google Generative AI")
    google_api_key: str = Field(default="", description="Google API Key")
    
    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"


# Global settings instance
settings = Settings()
