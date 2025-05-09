from datetime import datetime, timezone
from bson import ObjectId
import json

from ..core.openai_client import get_openai_client, VECTOR_STORE_ID, MODEL, PROMPT
import uuid
from ..schemas.file_search import (
    FileSearchRequest,
    FileSearchResponse,
    LLMQueryRequest,
    LLMQueryResponse,
    FileListResponse,
    SecretKeyRequest,
    SecretKeyResponse
)
from .db_utils import get_last_response_id, save_message_to_mongo, validate_secret_key  # <-- actualizado para incluir validate_secret_key

def list_files() -> FileListResponse:
    client = get_openai_client()
    result = client.vector_stores.files.list(vector_store_id=VECTOR_STORE_ID)
    files = [{"file_id": f.id, "filename": getattr(f, "filename", None)} for f in result.data]
    return FileListResponse(files=files)

def search_files(request: FileSearchRequest) -> FileSearchResponse:
    client = get_openai_client()
    results = client.vector_stores.search(
        vector_store_id=VECTOR_STORE_ID,
        query=request.query,
    )
    matches = []
    for result in results.data:
        match = {
            "filename": getattr(result, "filename", None),
            "file_id": getattr(result, "file_id", None),
            "score": getattr(result, "score", None),
            "content": [getattr(content, "text", "") for content in getattr(result, "content", [])]
        }
        matches.append(match)
    return FileSearchResponse(matches=matches)

def chat(request: LLMQueryRequest) -> LLMQueryResponse:
    # Validar secretInterno antes de continuar
    validation_result = validate_secret_key(request.secretInterno)
    if not validation_result["valid"]:
        # No lanzamos la excepción aquí, la manejamos en la ruta
        return None

    client = get_openai_client()
    sessionId = request.sessionId
    last_response_id = None

    if sessionId:
        last_response_id = get_last_response_id(sessionId)
    else:
        sessionId = str(uuid.uuid4())
    print("sessionId", sessionId)
    print("last_response_id", last_response_id)
    response = client.responses.create(
        model=MODEL,
        instructions=PROMPT,
        temperature=0.5,
        input=request.question,
        max_output_tokens=2000,
        tools=[{
            "type": "file_search",
            "vector_store_ids": [VECTOR_STORE_ID],
            "max_num_results": 10
        }],
        include=["file_search_call.results"],
        previous_response_id=last_response_id
    )
    answers = []  # Initialize answers list
    for output in response.output:  # Iterate through response output
        if hasattr(output, "content"):
            for content in getattr(output, "content", []):
                if hasattr(content, "text"):
                    answers.append(content.text)
        elif hasattr(output, "text"):
            answers.append(output.text)
    #print(json.dumps(response.to_dict(), ensure_ascii=False, separators=(',', ':')))
    #answers.append(response.output[1].content[0].text)
    # Preparar documento MongoDB
    doc = {
        "_id": str(ObjectId()),
        "sesionId": sessionId,
        "question": request.question,
        "answer": answers[0] if answers else "",
        "totalTokens": getattr(response.usage, "total_tokens", 0) if hasattr(response, "usage") else 0,
        "lastResponseId": getattr(response, "id", None),
        "createdAt": datetime.now(timezone.utc),
        "status": 1
    }
    # print("doc", doc)
    save_message_to_mongo(doc)

    return LLMQueryResponse(answers=answers, sessionId=sessionId)

def validate_secret(request: SecretKeyRequest):
    """
    Valida el secretKey en la base de datos MongoDB y retorna el secretInterno si es válido.
    """
    result = validate_secret_key(request.secretKey)
    
    if result["valid"]:
        return SecretKeyResponse(secretInterno=result["secretInterno"])
    return None
