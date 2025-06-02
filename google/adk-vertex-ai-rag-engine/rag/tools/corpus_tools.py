"""
RAG Corpus Management Tools for Vertex AI using ADK function tools pattern.

RAG Corpus Management:
1. Create a new RAG corpus
2. Update an existing RAG corpus
3. List all RAG corpora
4. Get details of a specific RAG corpus
5. Delete a RAG corpus

RAG File Management (within a corpus):
6. Upload RAG files
7. List RAG files
8. Get RAG file details
9. Delete RAG files
10. Query RAG files
"""

import vertexai
from vertexai.preview import rag
from google.adk.tools import FunctionTool
from typing import Dict, Optional, Any
from rag.config import (
    PROJECT_ID,
    LOCATION,
    RAG_DEFAULT_EMBEDDING_MODEL,
    RAG_DEFAULT_TOP_K,
    RAG_DEFAULT_SEARCH_TOP_K,
    RAG_DEFAULT_VECTOR_DISTANCE_THRESHOLD,
    RAG_DEFAULT_PAGE_SIZE
)

# Initialize Vertex AI API
vertexai.init(project=PROJECT_ID, location=LOCATION)


def create_rag_corpus(
    display_name: str,
    description: Optional[str] = None,
    embedding_model: Optional[str] = None
) -> Dict[str, Any]:
    """
    Creates a new RAG corpus in Vertex AI.
    
    Args:
        display_name: A human-readable name for the corpus
        description: Optional description for the corpus
        embedding_model: The embedding model to use (default: text-embedding-004)
    
    Returns:
        A dictionary containing the created corpus details including:
        - status: "success" or "error"
        - corpus_name: The full resource name of the created corpus
        - corpus_id: The ID portion of the corpus name
        - display_name: The human-readable name provided
        - error_message: Present only if an error occurred
    """
    if embedding_model is None:
        embedding_model = RAG_DEFAULT_EMBEDDING_MODEL
    try:
        # Configure embedding model
        embedding_model_config = rag.EmbeddingModelConfig(
            publisher_model=f"publishers/google/models/{embedding_model}"
        )
        
        # Create the corpus
        corpus = rag.create_corpus(
            display_name=display_name,
            description=description or f"RAG corpus: {display_name}",
            embedding_model_config=embedding_model_config,
        )
        
        # Extract corpus ID from the full name
        corpus_id = corpus.name.split('/')[-1]
        
        return {
            "status": "success",
            "corpus_name": corpus.name,
            "corpus_id": corpus_id,
            "display_name": corpus.display_name,
            "message": f"Successfully created RAG corpus '{display_name}'"
        }
    except Exception as e:
        return {
            "status": "error",
            "error_message": str(e),
            "message": f"Failed to create RAG corpus: {str(e)}"
        }


def update_rag_corpus(
    corpus_id: str,
    display_name: Optional[str] = None,
    description: Optional[str] = None
) -> Dict[str, Any]:
    """
    Updates an existing RAG corpus with new display name and/or description.
    
    Args:
        corpus_id: The ID of the corpus to update
        display_name: New display name for the corpus (optional)
        description: New description for the corpus (optional)
    
    Returns:
        A dictionary containing the update result:
        - status: "success" or "error"
        - corpus_name: The full resource name of the updated corpus
        - corpus_id: The ID of the corpus
        - display_name: The updated display name
        - description: The updated description
        - error_message: Present only if an error occurred
    """
    try:
        # Construct full corpus name
        corpus_name = f"projects/{PROJECT_ID}/locations/{LOCATION}/ragCorpora/{corpus_id}"
        
        # Get the existing corpus
        corpus = rag.get_corpus(name=corpus_name)
        
        # Update fields if provided
        if display_name:
            corpus.display_name = display_name
        if description:
            corpus.description = description
        
        # Apply updates
        updated_corpus = rag.update_corpus(
            corpus=corpus,
            update_mask=["display_name", "description"]
        )
        
        return {
            "status": "success",
            "corpus_name": updated_corpus.name,
            "corpus_id": corpus_id,
            "display_name": updated_corpus.display_name,
            "description": updated_corpus.description,
            "message": f"Successfully updated RAG corpus '{corpus_id}'"
        }
    except Exception as e:
        return {
            "status": "error",
            "corpus_id": corpus_id,
            "error_message": str(e),
            "message": f"Failed to update RAG corpus: {str(e)}"
        }


def list_rag_corpora() -> Dict[str, Any]:
    """
    Lists all RAG corpora in the current project and location.
    
    Returns:
        A dictionary containing the list of corpora:
        - status: "success" or "error"
        - corpora: List of corpus objects with id, name, and display_name
        - count: Number of corpora found
        - error_message: Present only if an error occurred
    """
    try:
        corpora = rag.list_corpora()
        
        corpus_list = []
        for corpus in corpora:
            corpus_id = corpus.name.split('/')[-1]
            
            # Get corpus status
            status = None
            if hasattr(corpus, "corpus_status") and hasattr(corpus.corpus_status, "state"):
                status = corpus.corpus_status.state
            elif hasattr(corpus, "corpusStatus") and hasattr(corpus.corpusStatus, "state"):
                status = corpus.corpusStatus.state
            
            # Make an explicit API call to count files
            files_count = 0
            try:
                # List all files to get the count
                files_response = rag.list_files(corpus_name=corpus.name)
                
                if hasattr(files_response, "rag_files"):
                    files_count = len(files_response.rag_files)
            except Exception:
                # If counting files fails, continue with zero count
                pass
            
            corpus_list.append({
                "id": corpus_id,
                "name": corpus.name,
                "display_name": corpus.display_name,
                "description": corpus.description if hasattr(corpus, "description") else None,
                "create_time": str(corpus.create_time) if hasattr(corpus, "create_time") else None,
                "files_count": files_count,
                "status": status
            })
        
        return {
            "status": "success",
            "corpora": corpus_list,
            "count": len(corpus_list),
            "message": f"Found {len(corpus_list)} RAG corpora"
        }
    except Exception as e:
        return {
            "status": "error",
            "error_message": str(e),
            "message": f"Failed to list RAG corpora: {str(e)}"
        }


def get_rag_corpus(corpus_id: str) -> Dict[str, Any]:
    """
    Retrieves details of a specific RAG corpus.
    
    Args:
        corpus_id: The ID of the corpus to retrieve
    
    Returns:
        A dictionary containing the corpus details:
        - status: "success" or "error"
        - corpus: Detailed information about the corpus
        - files_count: Number of files in the corpus
        - error_message: Present only if an error occurred
    """
    try:
        # Construct full corpus name
        corpus_name = f"projects/{PROJECT_ID}/locations/{LOCATION}/ragCorpora/{corpus_id}"
        
        # Get the corpus
        corpus = rag.get_corpus(name=corpus_name)
        
        # Get corpus status
        status = None
        if hasattr(corpus, "corpus_status") and hasattr(corpus.corpus_status, "state"):
            status = corpus.corpus_status.state
        elif hasattr(corpus, "corpusStatus") and hasattr(corpus.corpusStatus, "state"):
            status = corpus.corpusStatus.state
        
        # Make an explicit API call to count files
        files_count = 0
        try:
            # List all files to get the count
            files_response = rag.list_files(corpus_name=corpus_name)
            
            if hasattr(files_response, "rag_files"):
                files_count = len(files_response.rag_files)
        except Exception as file_error:
            # If counting files fails, log but continue with zero count
            print(f"Warning: Could not count files: {str(file_error)}")
        
        # Extract basic information
        corpus_details = {
            "id": corpus_id,
            "name": corpus.name,
            "display_name": corpus.display_name,
            "description": corpus.description if hasattr(corpus, "description") else None,
            "create_time": str(corpus.create_time) if hasattr(corpus, "create_time") else None,
            "update_time": str(corpus.update_time) if hasattr(corpus, "update_time") else None,
            "files_count": files_count,
            "state": status
        }
        
        # Include raw API response data for transparency
        raw_data = {}
        if hasattr(corpus, "to_dict"):
            raw_data = corpus.to_dict()
        elif hasattr(corpus, "__dict__"):
            raw_data = {k: v for k, v in corpus.__dict__.items() if not k.startswith('_')}
        
        if raw_data:
            corpus_details["raw_api_data"] = raw_data
        
        return {
            "status": "success",
            "corpus": corpus_details,
            "files_count": files_count,
            "message": f"Successfully retrieved RAG corpus '{corpus_id}' with {files_count} files"
        }
    except Exception as e:
        return {
            "status": "error",
            "corpus_id": corpus_id,
            "error_message": str(e),
            "message": f"Failed to retrieve RAG corpus: {str(e)}"
        }


def delete_rag_corpus(corpus_id: str) -> Dict[str, Any]:
    """
    Deletes a RAG corpus.
    
    Args:
        corpus_id: The ID of the corpus to delete
    
    Returns:
        A dictionary containing the deletion result:
        - status: "success" or "error"
        - corpus_id: The ID of the deleted corpus
        - error_message: Present only if an error occurred
    """
    try:
        # Construct full corpus name
        corpus_name = f"projects/{PROJECT_ID}/locations/{LOCATION}/ragCorpora/{corpus_id}"
        
        # Delete the corpus
        rag.delete_corpus(name=corpus_name)
        
        return {
            "status": "success",
            "corpus_id": corpus_id,
            "message": f"Successfully deleted RAG corpus '{corpus_id}'"
        }
    except Exception as e:
        return {
            "status": "error",
            "corpus_id": corpus_id,
            "error_message": str(e),
            "message": f"Failed to delete RAG corpus: {str(e)}"
        }


# Function for importing documents into a RAG corpus
def import_document_to_corpus(
    corpus_id: str,
    gcs_uri: str
) -> Dict[str, Any]:
    """
    Imports a document from Google Cloud Storage into a RAG corpus.
    Uses the minimal required parameters to avoid any compatibility issues.
    
    Args:
        corpus_id: The ID of the corpus to import the document into
        gcs_uri: GCS path of the document to import (gs://bucket-name/file-name)
    
    Returns:
        A dictionary containing:
        - status: "success" or "error"
        - corpus_id: The ID of the corpus
        - message: Status message
    """
    try:
        # Construct full corpus name
        corpus_name = f"projects/{PROJECT_ID}/locations/{LOCATION}/ragCorpora/{corpus_id}"
        
        # Import document with minimal configuration
        # Use the most basic form of the API call to avoid parameter issues
        result = rag.import_files(
            corpus_name,
            [gcs_uri]  # Single path in a list
        )
        
        # Return success result
        return {
            "status": "success",
            "corpus_id": corpus_id,
            "message": f"Successfully imported document {gcs_uri} to corpus '{corpus_id}'"
        }
    except Exception as e:
        return {
            "status": "error",
            "corpus_id": corpus_id,
            "error_message": str(e),
            "message": f"Failed to import document: {str(e)}"
        }

# RAG File Management Functions

def list_rag_files(
    corpus_id: str,
    page_size: Optional[int] = None,
    page_token: Optional[str] = None
) -> Dict[str, Any]:
    """
    Lists all RAG files in a corpus.
    
    Args:
        corpus_id: The ID of the corpus to list files from
        page_size: Maximum number of files to return (default: 50)
        page_token: Token for pagination
    
    Returns:
        A dictionary containing the list of files:
        - status: "success" or "error"
        - corpus_id: The ID of the corpus
        - files: List of file objects
        - count: Number of files found
        - next_page_token: Token for the next page (if any)
        - error_message: Present only if an error occurred
    """
    if page_size is None:
        page_size = RAG_DEFAULT_PAGE_SIZE
    try:
        # Construct full corpus name
        corpus_name = f"projects/{PROJECT_ID}/locations/{LOCATION}/ragCorpora/{corpus_id}"
        
        # List files
        response = rag.list_files(
            corpus_name=corpus_name,
            page_size=page_size, 
            page_token=page_token
        )
        
        # Process files
        files = []
        for file in response.rag_files:
            file_id = file.name.split("/")[-1]
            files.append({
                "id": file_id,
                "name": file.name,
                "display_name": file.display_name if hasattr(file, "display_name") else None,
                "description": file.description if hasattr(file, "description") else None,
                "source_uri": file.source_uri if hasattr(file, "source_uri") else None,
                "create_time": str(file.create_time) if hasattr(file, "create_time") else None,
                "update_time": str(file.update_time) if hasattr(file, "update_time") else None
            })
        
        return {
            "status": "success",
            "corpus_id": corpus_id,
            "files": files,
            "count": len(files),
            "next_page_token": response.next_page_token if hasattr(response, "next_page_token") else None,
            "message": f"Found {len(files)} file(s) in corpus '{corpus_id}'"
        }
    except Exception as e:
        return {
            "status": "error",
            "corpus_id": corpus_id,
            "error_message": str(e),
            "message": f"Failed to list files: {str(e)}"
        }

def get_rag_file(
    corpus_id: str,
    file_id: str
) -> Dict[str, Any]:
    """
    Gets details of a specific RAG file in a corpus.
    
    Args:
        corpus_id: The ID of the corpus
        file_id: The ID of the file to get
    
    Returns:
        A dictionary containing the file details:
        - status: "success" or "error"
        - corpus_id: The ID of the corpus
        - file: Detailed information about the file
        - error_message: Present only if an error occurred
    """
    try:
        # Construct full file name
        file_name = f"projects/{PROJECT_ID}/locations/{LOCATION}/ragCorpora/{corpus_id}/ragFiles/{file_id}"
        
        # Get the file
        file = rag.get_file(name=file_name)
        
        # Extract file details
        file_details = {
            "id": file_id,
            "name": file.name,
            "display_name": file.display_name if hasattr(file, "display_name") else None,
            "description": file.description if hasattr(file, "description") else None,
            "source_uri": file.source_uri if hasattr(file, "source_uri") else None,
            "create_time": str(file.create_time) if hasattr(file, "create_time") else None,
            "update_time": str(file.update_time) if hasattr(file, "update_time") else None
        }
        
        # Include raw API response data for transparency
        raw_data = {}
        if hasattr(file, "to_dict"):
            raw_data = file.to_dict()
        elif hasattr(file, "__dict__"):
            raw_data = {k: v for k, v in file.__dict__.items() if not k.startswith('_')}
        
        if raw_data:
            file_details["raw_api_data"] = raw_data
        
        return {
            "status": "success",
            "corpus_id": corpus_id,
            "file": file_details,
            "message": f"Successfully retrieved file '{file_id}' from corpus '{corpus_id}'"
        }
    except Exception as e:
        return {
            "status": "error",
            "corpus_id": corpus_id,
            "file_id": file_id,
            "error_message": str(e),
            "message": f"Failed to retrieve file: {str(e)}"
        }

def delete_rag_file(
    corpus_id: str,
    file_id: str
) -> Dict[str, Any]:
    """
    Deletes a RAG file from a corpus.
    
    Args:
        corpus_id: The ID of the corpus
        file_id: The ID of the file to delete
    
    Returns:
        A dictionary containing the deletion result:
        - status: "success" or "error"
        - corpus_id: The ID of the corpus
        - file_id: The ID of the deleted file
        - error_message: Present only if an error occurred
    """
    try:
        # Construct full file name
        file_name = f"projects/{PROJECT_ID}/locations/{LOCATION}/ragCorpora/{corpus_id}/ragFiles/{file_id}"
        
        # Delete the file
        rag.delete_file(name=file_name)
        
        return {
            "status": "success",
            "corpus_id": corpus_id,
            "file_id": file_id,
            "message": f"Successfully deleted file '{file_id}' from corpus '{corpus_id}'"
        }
    except Exception as e:
        return {
            "status": "error",
            "corpus_id": corpus_id,
            "file_id": file_id,
            "error_message": str(e),
            "message": f"Failed to delete file: {str(e)}"
        }

# Function for simple direct corpus querying
def query_rag_corpus(
    corpus_id: str,
    query_text: str,
    top_k: Optional[int] = None,
    vector_distance_threshold: Optional[float] = None
) -> Dict[str, Any]:
    """
    Directly queries a RAG corpus using the Vertex AI RAG API.
    
    Args:
        corpus_id: The ID of the corpus to query
        query_text: The search query text
        top_k: Maximum number of results to return (default: 10)
        vector_distance_threshold: Threshold for vector similarity (default: 0.5)
        
    Returns:
        A dictionary containing the query results
    """
    if top_k is None:
        top_k = RAG_DEFAULT_TOP_K
    if vector_distance_threshold is None:
        vector_distance_threshold = RAG_DEFAULT_VECTOR_DISTANCE_THRESHOLD
    try:
        # Construct full corpus resource path
        corpus_path = f"projects/{PROJECT_ID}/locations/{LOCATION}/ragCorpora/{corpus_id}"
        
        # Create the resource config
        rag_resource = rag.RagResource(rag_corpus=corpus_path)
        
        # Configure retrieval parameters
        retrieval_config = rag.RagRetrievalConfig(
            top_k=top_k,
            filter=rag.utils.resources.Filter(vector_distance_threshold=vector_distance_threshold)
        )
        
        # Execute the query directly using the API
        response = rag.retrieval_query(
            rag_resources=[rag_resource],
            text=query_text,
            rag_retrieval_config=retrieval_config
        )
        
        # Process the results
        results = []
        if hasattr(response, "contexts"):
            # Handle different response structures
            contexts = response.contexts
            if hasattr(contexts, "contexts"):
                contexts = contexts.contexts
            
            # Extract text and metadata from each context
            for context in contexts:
                result = {
                    "text": context.text if hasattr(context, "text") else "",
                    "source_uri": context.source_uri if hasattr(context, "source_uri") else None,
                    "relevance_score": context.relevance_score if hasattr(context, "relevance_score") else None
                }
                results.append(result)
        
        return {
            "status": "success",
            "corpus_id": corpus_id,
            "results": results,
            "count": len(results),
            "query": query_text,
            "message": f"Found {len(results)} results for query: '{query_text}'"
        }
        
    except Exception as e:
        return {
            "status": "error",
            "corpus_id": corpus_id,
            "error_message": str(e),
            "message": f"Failed to query corpus: {str(e)}"
        }

# Function to search across all corpora
def search_all_corpora(
    query_text: str,
    top_k_per_corpus: Optional[int] = None,
    vector_distance_threshold: Optional[float] = None
) -> Dict[str, Any]:
    """
    Searches across ALL available corpora for the given query text.
    When a user wants to search for information without specifying a corpus,
    this is the default tool to use.
    
    Args:
        query_text: The search query text
        top_k_per_corpus: Maximum number of results to return per corpus (default: 5)
        vector_distance_threshold: Threshold for vector similarity (default: 0.5)
        
    Returns:
        A dictionary containing the combined search results with citations
    """
    if top_k_per_corpus is None:
        top_k_per_corpus = RAG_DEFAULT_SEARCH_TOP_K
    if vector_distance_threshold is None:
        vector_distance_threshold = RAG_DEFAULT_VECTOR_DISTANCE_THRESHOLD
    try:
        # First, list all available corpora
        corpora_response = list_rag_corpora()
        
        if corpora_response["status"] != "success":
            return {
                "status": "error",
                "error_message": f"Failed to list corpora: {corpora_response.get('error_message', '')}",
                "message": "Failed to search all corpora - could not retrieve corpus list"
            }
        
        all_corpora = corpora_response.get("corpora", [])
        
        if not all_corpora:
            return {
                "status": "warning",
                "message": "No corpora found to search in"
            }
        
        # Search in each corpus
        all_results = []
        corpus_results_map = {}  # Map of corpus name to its results
        searched_corpora = []
        
        for corpus in all_corpora:
            corpus_id = corpus["id"]
            corpus_name = corpus.get("display_name", corpus_id)
            
            # Query this corpus
            corpus_results = query_rag_corpus(
                corpus_id=corpus_id,
                query_text=query_text,
                top_k=top_k_per_corpus,
                vector_distance_threshold=vector_distance_threshold
            )
            
            # Add corpus info to the results
            if corpus_results["status"] == "success":
                results = corpus_results.get("results", [])
                corpus_specific_results = []
                
                for result in results:
                    # Add citation and source information
                    result["corpus_id"] = corpus_id
                    result["corpus_name"] = corpus_name
                    result["citation"] = f"[Source: {corpus_name} ({corpus_id})]"
                    
                    # Add source file information if available
                    if "source_uri" in result and result["source_uri"]:
                        source_path = result["source_uri"]
                        file_name = source_path.split("/")[-1] if "/" in source_path else source_path
                        result["citation"] += f" File: {file_name}"
                    
                    corpus_specific_results.append(result)
                    all_results.append(result)
                
                # Save results for this corpus
                if corpus_specific_results:
                    corpus_results_map[corpus_name] = {
                        "corpus_id": corpus_id,
                        "corpus_name": corpus_name,
                        "results": corpus_specific_results,
                        "count": len(corpus_specific_results)
                    }
                    searched_corpora.append(corpus_name)
        
        # Sort all results by relevance score (if available)
        all_results.sort(
            key=lambda x: x.get("relevance_score", 0) if x.get("relevance_score") is not None else 0,
            reverse=True
        )
        
        # Format citations summary
        citations_summary = []
        for corpus_name in searched_corpora:
            corpus_data = corpus_results_map[corpus_name]
            citations_summary.append(
                f"{corpus_name} ({corpus_data['corpus_id']}): {corpus_data['count']} results"
            )
        
        return {
            "status": "success",
            "results": all_results,
            "corpus_results": corpus_results_map,
            "searched_corpora": searched_corpora,
            "citations_summary": citations_summary,
            "count": len(all_results),
            "query": query_text,
            "message": f"Found {len(all_results)} results for query '{query_text}' across {len(searched_corpora)} corpora",
            "citation_note": "Each result includes a citation indicating its source corpus and file."
        }
        
    except Exception as e:
        return {
            "status": "error",
            "error_message": str(e),
            "message": f"Failed to search all corpora: {str(e)}"
        }

# Create FunctionTools from the functions for the RAG corpus management tools
create_corpus_tool = FunctionTool(create_rag_corpus)
update_corpus_tool = FunctionTool(update_rag_corpus)
list_corpora_tool = FunctionTool(list_rag_corpora)
get_corpus_tool = FunctionTool(get_rag_corpus)
delete_corpus_tool = FunctionTool(delete_rag_corpus)
import_document_tool = FunctionTool(import_document_to_corpus)

# Create FunctionTools from the functions for the RAG file management tools
list_files_tool = FunctionTool(list_rag_files)
get_file_tool = FunctionTool(get_rag_file)
delete_file_tool = FunctionTool(delete_rag_file)

# Create FunctionTools from the functions for the RAG query tools
query_rag_corpus_tool = FunctionTool(query_rag_corpus)
search_all_corpora_tool = FunctionTool(search_all_corpora) 