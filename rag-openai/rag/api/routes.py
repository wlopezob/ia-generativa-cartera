from fastapi import APIRouter, Query
from ..services.file_search_service import (
    list_files,
    search_files,
    query_llm
)
from ..schemas.file_search import (
    FileListResponse,
    FileSearchRequest,
    FileSearchResponse,
    LLMQueryRequest,
    LLMQueryResponse
)

router = APIRouter(prefix="/legalaco")

# @router.get("/files", response_model=FileListResponse)
# def get_files():
#     return list_files()

# @router.post("/search", response_model=FileSearchResponse)
# def post_search_files(request: FileSearchRequest):
#     return search_files(request)

@router.post("/chat", response_model=LLMQueryResponse)
def post_llm_query(request: LLMQueryRequest):
    return query_llm(request)
