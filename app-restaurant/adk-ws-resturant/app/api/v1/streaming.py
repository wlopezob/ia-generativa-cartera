"""
Streaming endpoints for restaurant AI assistant
"""
import os
import json
import asyncio
import base64
import warnings
from typing import Optional

from fastapi import APIRouter, WebSocket, WebSocketDisconnect
from google.genai.types import (
    Part,
    Content,
    Blob,
    SpeechConfig,
    VoiceConfig,
    PrebuiltVoiceConfig,
)
from google.adk.runners import InMemoryRunner
from google.adk.agents import LiveRequestQueue
from google.adk.agents.run_config import RunConfig
from app.core.adk_runner import runner
from app.core.config import settings
from app.agent.agent import root_agent
from dotenv import load_dotenv

warnings.filterwarnings("ignore", category=UserWarning, module="pydantic")

#
# ADK Streaming
#

# Load Gemini API Key
load_dotenv()


router = APIRouter(prefix="/streaming", tags=["Streaming"])

@router.get("/info", 
    summary="WebSocket Information",
    description="Information about the WebSocket endpoint for streaming communication"
)
async def websocket_info():
    """
    Get information about the WebSocket streaming endpoint
    
    Returns:
        dict: WebSocket endpoint details and usage instructions
    """
    return {
        "endpoint": "/api/v1/streaming/ws/{user_id}",
        "protocol": "WebSocket",
        "description": "Real-time bidirectional communication with the restaurant AI assistant",
        "parameters": {
            "user_id": {
                "type": "string",
                "description": "Unique identifier for the user session",
                "required": True
            },
            "is_audio": {
                "type": "string",
                "description": "Enable audio mode ('true' or 'false')",
                "default": "false",
                "required": False
            }
        },
        "message_format": {
            "client_to_server": {
                "text": {
                    "mime_type": "text/plain",
                    "data": "Your message here"
                },
                "audio": {
                    "mime_type": "audio/pcm",
                    "data": "base64_encoded_audio_data"
                }
            },
            "server_to_client": {
                "text": {
                    "mime_type": "text/plain",
                    "data": "Response text"
                },
                "audio": {
                    "mime_type": "audio/pcm",
                    "data": "base64_encoded_audio_data"
                },
                "transcription": {
                    "mime_type": "text/transcription",
                    "data": "Transcripción del audio enviado"
                },
                "control": {
                    "turn_complete": "boolean",
                    "interrupted": "boolean"
                }
            }
        },
        "example_usage": {
            "javascript": """
// Connect to WebSocket
const ws = new WebSocket('ws://localhost:8000/api/v1/streaming/ws/123?is_audio=false');

// Send message
ws.send(JSON.stringify({
    mime_type: 'text/plain',
    data: 'Hello, I need a table for 4 people'
}));

// Receive messages
ws.onmessage = (event) => {
    const message = JSON.parse(event.data);
    console.log('Received:', message);
};
"""
        }
    }

APP_NAME = "ADK Streaming example"
async def start_agent_session(user_id, is_audio=False):
    """Starts an agent session"""

    # Create a Runner
    runner = InMemoryRunner(
        app_name=APP_NAME,
        agent=root_agent,
    )

    # Create a Session
    session = await runner.session_service.create_session(
        app_name=APP_NAME,
        user_id=user_id,  # Replace with actual user ID
    )

    # Set response modality
    modality = "AUDIO" if is_audio else "TEXT"

    voice_name = "Aoede"  # @param ["Aoede", "Puck", "Charon", "Kore", "Fenrir", "Leda", "Orus", "Zephyr"]
    # Create speech config with voice settings
    speech_config = SpeechConfig(
        voice_config=VoiceConfig(
            prebuilt_voice_config=PrebuiltVoiceConfig(voice_name=voice_name)
        ),
        language_code="es-ES"
    )

    run_config = RunConfig(response_modalities=[modality], speech_config=speech_config)

    #if is_audio:
    #    run_config.output_audio_transcription = {}
    
    # Habilitar transcripción de audio en todos los casos
    if is_audio:
        run_config.output_audio_transcription = {}

    # Create a LiveRequestQueue for this session
    live_request_queue = LiveRequestQueue()

    # Start agent session
    live_events = runner.run_live(
        session=session,
        live_request_queue=live_request_queue,
        run_config=run_config,
    )
    return live_events, live_request_queue


async def agent_to_client_messaging(websocket, live_events):
    """Agent to client communication"""
    while True:
        async for event in live_events:

            # If the turn complete or interrupted, send it
            if event.turn_complete or event.interrupted:
                message = {
                    "turn_complete": event.turn_complete,
                    "interrupted": event.interrupted,
                }
                await websocket.send_text(json.dumps(message))
                print(f"[AGENT TO CLIENT]: {message}")
                continue

            # Read the Content and its first Part
            part: Part = (
                event.content and event.content.parts and event.content.parts[0]
            )
            if not part:
                continue

            # If it's audio, send Base64 encoded audio data
            is_audio = part.inline_data and part.inline_data.mime_type.startswith("audio/pcm")
            if is_audio:
                audio_data = part.inline_data and part.inline_data.data
                if audio_data:
                    message = {
                        "mime_type": "audio/pcm",
                        "data": base64.b64encode(audio_data).decode("ascii")
                    }
                    await websocket.send_text(json.dumps(message))
                    print(f"[AGENT TO CLIENT]: audio/pcm: {len(audio_data)} bytes.")
                    continue

            # If it's text and a parial text, send it
            if part.text and event.partial:
                message = {
                    "mime_type": "text/plain",
                    "data": part.text
                }
                await websocket.send_text(json.dumps(message))
                print(f"[AGENT TO CLIENT]: text/plain: {message}")


async def client_to_agent_messaging(websocket, live_request_queue):
    """Client to agent communication"""
    while True:
        # Decode JSON message
        message_json = await websocket.receive_text()
        message = json.loads(message_json)
        mime_type = message["mime_type"]
        data = message["data"]

        # Send the message to the agent
        if mime_type == "text/plain":
            # Send a text message
            content = Content(role="user", parts=[Part.from_text(text=data)])
            live_request_queue.send_content(content=content)
            print(f"[CLIENT TO AGENT]: {data}")
        elif mime_type == "audio/pcm":
            # Send an audio data
            decoded_data = base64.b64decode(data)
            live_request_queue.send_realtime(Blob(data=decoded_data, mime_type=mime_type))
            print(f"[CLIENT TO AGENT]: audio/pcm: {len(decoded_data)} bytes.")
        else:
            raise ValueError(f"Mime type not supported: {mime_type}")

@router.websocket("/ws/{user_id}")
async def websocket_endpoint(
    websocket: WebSocket, 
    user_id: str, 
    is_audio: Optional[str] = "false"
):
    """Client websocket endpoint"""

    # Wait for client connection
    await websocket.accept()
    print(f"Client #{user_id} connected, audio mode: {is_audio}")

    # Start agent session
    user_id_str = str(user_id)
    live_events, live_request_queue = await start_agent_session(user_id_str, is_audio == "true")

    # Start tasks
    agent_to_client_task = asyncio.create_task(
        agent_to_client_messaging(websocket, live_events)
    )
    client_to_agent_task = asyncio.create_task(
        client_to_agent_messaging(websocket, live_request_queue)
    )

    # Wait until the websocket is disconnected or an error occurs
    tasks = [agent_to_client_task, client_to_agent_task]
    await asyncio.wait(tasks, return_when=asyncio.FIRST_EXCEPTION)

    # Close LiveRequestQueue
    live_request_queue.close()

    # Disconnected
    print(f"Client #{user_id} disconnected")