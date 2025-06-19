#!/bin/bash
# Development server startup script

echo "Starting Restaurant API Development Server..."
echo "=========================================="

# Activate virtual environment if not already active
if [[ "$VIRTUAL_ENV" == "" ]]; then
    source .venv/bin/activate
fi

# Run the application with the correct uvicorn command
echo "Launching uvicorn server..."
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload

# Optional: Uncomment to run with python main.py instead
# python main.py
