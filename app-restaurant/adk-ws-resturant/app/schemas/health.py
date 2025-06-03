"""
Health check schemas
"""
from pydantic import BaseModel, Field
from datetime import datetime
from typing import Dict, Any

class HealthCheck(BaseModel):
    """Simple health check response"""
    
    status: str = Field(default="ok", description="Health status")
    message: str = Field(default="Service is running", description="Status message")
