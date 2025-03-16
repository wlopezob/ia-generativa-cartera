use reqwest::Error;
use std::env;
use log::{info, error};

use crate::api_caller::open_api_caller::obtener_datos_caller;
use crate::api_caller::sunat_api_caller::consultar_ruc_caller;
use crate::exception::api_error::ApiError;
use crate::exception::error_type::ErrorType;
use crate::modelo::openai::{ChatCompletionRequest, ChatCompletionResponse};
use crate::modelo::sunat::RucResponse;

pub async fn obtener_empresa(nro_ruc: &str) -> Result<RucResponse, ApiError> {
    // consulta sunat ruc
    let html_response = consultar_ruc_caller(nro_ruc, 30).await
        .map_err(|e| {
            error!("Error al consultar RUC en SUNAT: {:?}", e);
            e
        })?;
    
    // Verificar si la respuesta es exitosa
    if !html_response.status().is_success() {
        return Err(ApiError::ExternalServiceError(
            format!("Error en la respuesta de SUNAT: {}", html_response.status()),
            ErrorType::SunatError
        ));
    }
    
    let html_ruc = html_response.text().await
        .map_err(|e| ApiError::ExternalServiceError(
            format!("Error al obtener texto de la respuesta: {}", e),
            ErrorType::SunatError
        ))?;
    
    info!("HTML de respuesta recibido con éxito");
    
    let response = obtener_datos_caller(html_ruc).await?;
    
    // Manejar la respuesta
    if response.status().is_success() {
        let completion: ChatCompletionResponse = response.json().await
            .map_err(|e| ApiError::SerdeError(e))?;
        
        // Registrar información sobre tokens utilizados
        info!(
            "Tokens utilizados - Prompt: {}, Completion: {}, Total: {}", 
            completion.usage.prompt_tokens,
            completion.usage.completion_tokens,
            completion.usage.total_tokens
        );
        
        // Extraer la respuesta del asistente
        if let Some(choice) = completion.choices.first() {
            info!(
                "Respuesta recibida con finish_reason: {}", 
                choice.finish_reason
            );
            
            // Intentar parsear el JSON que viene como string a nuestro struct RucResponse
            let ruc_info = serde_json::from_str::<RucResponse>(&choice.message.content)
                .map_err(|e| ApiError::SerdeError(e))?;
                
            info!("Información de RUC parseada correctamente");
            Ok(ruc_info)
        } else {
            Err(ApiError::ExternalServiceError(
                "No se recibió respuesta del modelo".to_string(),
                ErrorType::OpenAiError
            ))
        }
    } else {
        let status = response.status();
        let error_text = response.text().await
            .unwrap_or_else(|_| "No se pudo obtener el texto del error".to_string());
            
        error!("Error al llamar a OpenAI API: {} - {}", status, error_text);
        
        Err(ApiError::ExternalServiceError(
            format!("Error al comunicarse con OpenAI: {} - {}", status, error_text),
            ErrorType::OpenAiError
        ))
    }
}