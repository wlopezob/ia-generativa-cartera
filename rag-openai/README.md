# Quickstart

> **Prerequisite:**  
> You must have your vector store and document already registered in OpenAI's storage platform:  
> https://platform.openai.com/storage

## 1. Clone the repository
```bash
git https://github.com/wlopezob/ia-generativa-cartera.git
cd ia-generativa-cartera/rag-openai
```

## 2. Create and activate the environment with uv
```bash
uv venv
source .venv/bin/activate
```

## 3. Install dependencies with uv
```bash
uv sync
```

## 4. Copy the environment file and set your values
```bash
cp .env.example .env
# Edit .env and enter your API keys and configuration values
```

## 5. Start the project
```bash
uvicorn rag.main:app --reload
```

## API documentation
```
http://localhost:8000/docs   (Swagger UI)
http://localhost:8000/redoc  (ReDoc)
http://localhost:8000/openapi.json  (JSON OpenAPI)
```
