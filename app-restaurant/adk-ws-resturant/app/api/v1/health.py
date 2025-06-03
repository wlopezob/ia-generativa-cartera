"""
Health check endpoints
"""
from fastapi import APIRouter, status
from app.schemas.health import HealthCheck
from app.services.health import health_service

router = APIRouter(prefix="/health", tags=["Health"])


@router.get(
    "",
    response_model=HealthCheck,
    status_code=status.HTTP_200_OK,
    summary="Simple Health Check",
    description="Returns a simple health check status"
)
async def health_check() -> HealthCheck:
    """
    Simple health check endpoint.
    
    Returns:
        HealthCheck: Simple health status
    """
    return health_service.get_simple_health()

