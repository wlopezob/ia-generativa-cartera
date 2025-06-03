"""
Health check service
"""
import time
from datetime import datetime, timezone
from typing import Dict
from app.schemas.health import HealthCheck
from app.core.config import settings


class HealthService:
    """Service for handling health checks"""
    
    def __init__(self):
        self.start_time = time.time()
    
    def get_simple_health(self) -> HealthCheck:
        """Get simple health check"""
        return HealthCheck(
            status="ok",
            message="Service is running"
        )
    
    def _format_uptime(self, seconds: float) -> str:
        """Format uptime in human readable format"""
        hours, remainder = divmod(int(seconds), 3600)
        minutes, seconds = divmod(remainder, 60)
        
        if hours > 0:
            return f"{hours}h {minutes}m {seconds}s"
        elif minutes > 0:
            return f"{minutes}m {seconds}s"
        else:
            return f"{seconds}s"
    
    def _check_external_services(self) -> Dict[str, str]:
        """Check the health of external services"""
        # Placeholder for actual health checks
        # In a real application, you would check database, cache, external APIs, etc.
        return {
            "database": "healthy",
            "cache": "healthy",
            "external_api": "healthy"
        }


# Global health service instance
health_service = HealthService()
