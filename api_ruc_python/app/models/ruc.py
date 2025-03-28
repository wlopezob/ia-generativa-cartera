from pydantic import BaseModel, Field

class RucResponse(BaseModel):
    status: bool = Field(..., description="True if at least one data point is extracted")
    ruc: str = Field("", description="RUC number")
    nombrePersonaEmpresa: str = Field("", description="Company or person name")
    nombreComercial: str = Field("", description="Commercial name")
    estadoContribuyente: str = Field("", description="Taxpayer status")
    condicionContribuyente: str = Field("", description="Taxpayer condition")
    domicilioFiscal: str = Field("", description="Fiscal address")

class ErrorResponse(BaseModel):
    status: bool = Field(False)
    error: str = Field(..., description="Error message")
    raw_response: str = Field(None, description="Raw LLM response if applicable") 