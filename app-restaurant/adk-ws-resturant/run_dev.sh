#!/bin/bash
# Development server startup script

echo "Starting Restaurant API Development Server..."
echo "=========================================="

# Activate virtual environment if not already active
if [[ "$VIRTUAL_ENV" == "" ]]; then
    source .venv/bin/activate
fi

# Run the application
python main.py
