from fastapi import APIRouter, Query, HTTPException, status
from ..services.file_search_service import (
    list_files,
    search_files,
    chat,
    validate_secret
)
from ..schemas.file_search import (
    FileListResponse,
    FileSearchRequest,
    FileSearchResponse,
    LLMQueryRequest,
    LLMQueryResponse,
    SecretKeyRequest,
    SecretKeyResponse
)

router = APIRouter(prefix="/legalaco")

# @router.get("/files", response_model=FileListResponse)
# def get_files():
#     return list_files()

# @router.post("/search", response_model=FileSearchResponse)
# def post_search_files(request: FileSearchRequest):
#     return search_files(request)

@router.post("/chat", response_model=LLMQueryResponse)
def post_chat_query(request: LLMQueryRequest):
    result = chat(request)
    if result is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid secretInterno or secret not active"
        )
    return result

@router.post("/validate-secret", response_model=SecretKeyResponse, status_code=status.HTTP_200_OK)
def post_validate_secret(request: SecretKeyRequest):
    result = validate_secret(request)
    if result:
        return result
    raise HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Invalid secret key or secret not active"
    )
