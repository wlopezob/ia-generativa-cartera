# Add MongoDB to a project
- create a new MongoDB database named chat
- create a new MongoDB collection named messages
- create a new MongoDB document with the following fields:
  - _id_: string
  - sesionId: string
  - question: string
  - answer: string
  - totalTokens: number
  - lastResponseId: string
  - createdAt: date
  - status: number
- the connection string is stored in a .env file with the key MONGODB_URI
- consider the good practices for MongoDB with fastapi

# Objectives
- In the endpoint post_chat_query the user will send session_id.
- Search in the MongoDB collection messages for the session_id and status = 1 and order by createdAt asc.
- If the session_id is found, get the lastResponseId of the last document.
- If not found, set lastResponseId to None.
- In client.responses.create set previous_response_id with the lastResponseId.
- after executing client.responses.create, create a new document with the values of the response.
- then save the document in the MongoDB collection messages.