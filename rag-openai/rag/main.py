from fastapi import FastAPI
from fastapi.openapi.utils import get_openapi
from rag.api.routes import router
from rag.core.openapi_config import custom_openapi

app = FastAPI(
    title="File Search API",
    description="API para búsqueda y consulta de documentos legales usando OpenAI y vector store.",
    version="1.0.0"
)

app.include_router(router)

app.openapi = lambda: custom_openapi(app)
