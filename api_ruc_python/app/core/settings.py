from pydantic_settings import BaseSettings
from dotenv import load_dotenv

load_dotenv()

class Settings(BaseSettings):
    app_name: str = "API RUC"
    app_description: str = "API para consultar información de RUC usando IA"
    app_version: str = "0.1.0"
    
    # LLM settings
    openai_model: str = "gpt-4o-mini"
    openai_temperature: float = 0.5
    openai_max_tokens: int = 10000
    
    # CORS settings
    allow_origins: list[str] = ["*"]
    allow_credentials: bool = True
    allow_methods: list[str] = ["*"]
    allow_headers: list[str] = ["*"]
    
    # SUNAT API settings
    sunat_url: str = "https://e-consultaruc.sunat.gob.pe/cl-ti-itmrconsruc/jcrS00Alias"
    sunat_timeout: int = 30

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"

settings = Settings() 