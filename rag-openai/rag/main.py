from fastapi import FastAPI
from fastapi.openapi.utils import get_openapi
from fastapi.middleware.cors import CORSMiddleware
from rag.api.routes import router
from rag.core.openapi_config import custom_openapi

app = FastAPI(
    title="File Search API",
    description="API para búsqueda y consulta de documentos legales usando OpenAI y vector store.",
    version="1.0.0"
)

# Configure CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:4200"],  # Frontend origin
    allow_credentials=True,
    allow_methods=["*"],  # Allows all methods
    allow_headers=["*"],  # Allows all headers
)

app.include_router(router)

app.openapi = lambda: custom_openapi(app)
