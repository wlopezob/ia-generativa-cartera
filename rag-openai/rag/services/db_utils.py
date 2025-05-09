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

def get_secrets_collection():
    """Devuelve la colección de secrets de MongoDB."""
    mongo_client = MongoClient(MONGODB_URI)
    db = mongo_client["chat"]
    return db["secrets"]

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

def validate_secret_key(secret_key):
    """Valida el secretKey en la colección de secrets y devuelve el secretInterno si es válido."""
    collection = get_secrets_collection()
    secret_doc = collection.find_one({"secretInterno": secret_key, "activo": 1})
    
    if secret_doc:
        return {"valid": True, "secretInterno": secret_doc.get("secretInterno")}
    return {"valid": False}
