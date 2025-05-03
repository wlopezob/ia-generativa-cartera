from ..core.openai_client import get_openai_client, VECTOR_STORE_ID, MODEL, PROMPT
from ..schemas.file_search import (
    FileSearchRequest,
    FileSearchResponse,
    LLMQueryRequest,
    LLMQueryResponse,
    FileListResponse
)

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

def query_llm(request: LLMQueryRequest) -> LLMQueryResponse:
    client = get_openai_client()
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
        previous_response_id="resp_68160e206cb48191a038b1d9812d86c9024c618f06a5dada"
    )
    answers = []
    #answers.append(response.output[1].content[0].text)
    for output in response.output:
        if hasattr(output, "content"):
            for content in getattr(output, "content", []):
                if hasattr(content, "text"):
                    answers.append(content.text)
        elif hasattr(output, "text"):
            answers.append(output.text)
    return LLMQueryResponse(answers=answers, answers_id=response.id)
