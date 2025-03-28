from fastapi import APIRouter
from app.api.endpoints import ruc

api_router = APIRouter()

api_router.include_router(ruc.router, prefix="/ruc", tags=["RUC Queries"]) 