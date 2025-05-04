import os
from dotenv import load_dotenv
from pymongo import MongoClient, ASCENDING

load_dotenv()
MONGODB_URI = os.getenv("MONGODB_URI")

def get_mongo_collection():
    """Devuelve la colección de mensajes de MongoDB."""
    mongo_client = MongoClient(MONGODB_URI)
    db = mongo_client["chat"]
    return db["messages"]

def save_message_to_mongo(doc):
    """Guarda un documento en la colección de mensajes."""
    collection = get_mongo_collection()
    collection.insert_one(doc)

def get_last_response_id(session_id):
    """Obtiene el lastResponseId más reciente para una sesión dada."""
    collection = get_mongo_collection()
    docs = list(collection.find(
        {"sesionId": session_id, "status": 1}
    ).sort("createdAt", ASCENDING))
    if docs:
        return docs[-1].get("lastResponseId")
    return None
