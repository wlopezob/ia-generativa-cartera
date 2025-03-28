from fastapi import APIRouter, HTTPException, Query, Depends
from app.models.ruc import RucResponse, ErrorResponse
from app.services.sunat import consultar_ruc
from app.services.llm import process_html_with_llm
import logging

router = APIRouter()

@router.get(
    "/",
    response_model=dict,
    summary="Check if API is operational",
    description="Returns a simple message to check if the API is running."
)
async def root():
    return {"message": "API RUC operational"}

@router.get(
    "/consulta-ruc",
    response_model=RucResponse,
    responses={
        200: {"model": RucResponse, "description": "RUC information successfully retrieved"},
        404: {"model": ErrorResponse, "description": "RUC not found"},
        500: {"model": ErrorResponse, "description": "Server error"}
    },
    summary="Query RUC information",
    description="Consults information for a given RUC number from SUNAT and processes it with AI."
)
async def consulta_ruc(ruc: str = Query(..., description="RUC number to query")):
    try:
        # Validate RUC number (basic validation)
        if not ruc.isdigit() or len(ruc) != 11:
            raise HTTPException(status_code=400, detail="Invalid RUC number. Must be 11 digits.")
            
        # Fetch HTML data from SUNAT
        response = consultar_ruc(ruc)
        if not response:
            raise HTTPException(status_code=404, detail=f"RUC {ruc} not found or SUNAT service unavailable")
        
        # Process HTML with LLM
        result = process_html_with_llm(response.text)
        
        # If it's an error response, raise an exception
        if isinstance(result, ErrorResponse):
            raise HTTPException(status_code=500, detail=result.error)
            
        return result
            
    except HTTPException:
        raise
    except Exception as e:
        logging.exception(f"Error processing RUC {ruc}")
        raise HTTPException(status_code=500, detail=f"Error processing request: {str(e)}") 