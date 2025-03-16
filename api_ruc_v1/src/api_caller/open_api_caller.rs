use std::env;
use log::{info, error};
use reqwest::{Error, Response};


use crate::{exception::api_error::ApiError, modelo::{openai::{ChatCompletionRequest, ChatMessage}, sunat::RucResponse}, util::constants::PROMPT};

pub async fn obtener_datos_caller(html_ruc: String) -> Result<Response, ApiError> {
    // Obtener la clave API desde variables de entorno
    let api_key = env::var("OPENAI_API_KEY")
        .map_err(|_| ApiError::Generic("La clave API de OpenAI no está configurada en las variables de entorno.".to_string()))?;

        // Crear el cliente HTTP
    let client = reqwest::Client::new();
    
    // Construir los mensajes para la solicitud
    let messages = vec![
        ChatMessage {
            role: "developer".to_string(),
            content: PROMPT.to_string(),
        },
        ChatMessage {
            role: "user".to_string(), 
            content: html_ruc
        },
    ];

    // Construir la solicitud completa
    let request = ChatCompletionRequest {
        model: "gpt-4o".to_string(),
        messages,
        temperature: Some(1f32),
        max_completion_tokens: Some(2048),
    };

    info!("Enviando solicitud a OpenAI API");
    
    // Realizar la petición HTTP
    let response = client
        .post("https://api.openai.com/v1/chat/completions")
        .header("Content-Type", "application/json")
        .header("Authorization", format!("Bearer {}", api_key))
        .json(&request)
        .send()
        .await.map_err(|e| ApiError::ReqwestError(e))?;
    
    return Ok(response);
}