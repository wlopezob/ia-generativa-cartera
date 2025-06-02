"""
Configuration settings for the Vertex AI RAG engine.
"""
import os

# Google Cloud Project Settings
PROJECT_ID = os.environ.get("GOOGLE_CLOUD_PROJECT")  # Replace with your project ID
LOCATION = os.environ.get("GOOGLE_CLOUD_LOCATION", "us-central1")  # Default location for Vertex AI and GCS resources

# GCS Storage Settings
GCS_DEFAULT_STORAGE_CLASS = "STANDARD"
GCS_DEFAULT_LOCATION = "US"
GCS_LIST_BUCKETS_MAX_RESULTS = 50
GCS_LIST_BLOBS_MAX_RESULTS = 100
GCS_DEFAULT_CONTENT_TYPE = "application/pdf"  # Default content type for uploaded files

# RAG Corpus Settings
RAG_DEFAULT_EMBEDDING_MODEL = "text-embedding-004"
RAG_DEFAULT_TOP_K = 10  # Default number of results for single corpus query
RAG_DEFAULT_SEARCH_TOP_K = 5  # Default number of results per corpus for search_all
RAG_DEFAULT_VECTOR_DISTANCE_THRESHOLD = 0.5
RAG_DEFAULT_PAGE_SIZE = 50  # Default page size for listing files

# Agent Settings
AGENT_NAME = "rag_corpus_manager"
AGENT_MODEL = "gemini-2.0-flash-exp"
AGENT_OUTPUT_KEY = "last_response"

# Logging Settings
LOG_LEVEL = "INFO"
LOG_FORMAT = "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
