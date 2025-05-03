from pydantic import BaseModel
from typing import List, Optional

class FileInfo(BaseModel):
    file_id: str
    filename: Optional[str]

class FileListResponse(BaseModel):
    files: List[FileInfo]

class FileSearchRequest(BaseModel):
    query: str

class FileSearchMatch(BaseModel):
    filename: Optional[str]
    file_id: Optional[str]
    score: Optional[float]
    content: List[str]

class FileSearchResponse(BaseModel):
    matches: List[FileSearchMatch]

class LLMQueryRequest(BaseModel):
    question: str

class LLMQueryResponse(BaseModel):
    answers_id: str
    answers: List[str]
