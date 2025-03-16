use reqwest::Error;
use std::env;
use log::{info, error};

use crate::api_caller::open_api_caller::obtener_datos_caller;
use crate::api_caller::sunat_api_caller::consultar_ruc_caller;
use crate::exception::api_error::ApiError;
use crate::modelo::openai::{ChatCompletionRequest, ChatCompletionResponse};
use crate::modelo::sunat::RucResponse;



pub async fn obtener_empresa(nro_ruc: &str) -> Result<RucResponse, ApiError> {
    // consulta sunat ruc
    let html_ruc = consultar_ruc_caller(nro_ruc, 30).await?.text().await?;
    info!("HTML de respuesta: {}", html_ruc);
    
    let response = obtener_datos_caller(html_ruc).await?;
    // Manejar la respuesta
    if response.status().is_success() {
        let completion: ChatCompletionResponse = response.json().await
            .map_err(|_| ApiError::Generic("Error convirtieno json: ChatCompletionResponse".to_string()))?;
        
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
            match serde_json::from_str::<RucResponse>(&choice.message.content) {
                Ok(ruc_info) => {
                    info!("Información de RUC parseada correctamente");
                    Ok(ruc_info)
                },
                Err(e) => {
                    error!("Error al parsear la respuesta como JSON: {}", e);
                    // Crear una respuesta de error en caso de que el parseo falle
                    let error_response = RucResponse {
                        status: false,
                        ruc: nro_ruc.to_string(),
                        nombre_persona_empresa: format!("Error de parseo: {}", e),
                        nombre_comercial: "".to_string(),
                        estado_contribuyente: "".to_string(),
                        condicion_contribuyente: "".to_string(),
                        domicilio_fiscal: "".to_string(),
                    };
                    Ok(error_response)
                }
            }
        } else {
            // Crear una respuesta en caso de que no haya choices
            let error_response = RucResponse {
                status: false,
                ruc: nro_ruc.to_string(),
                nombre_persona_empresa: "No se recibió respuesta del modelo".to_string(),
                nombre_comercial: "".to_string(),
                estado_contribuyente: "".to_string(),
                condicion_contribuyente: "".to_string(),
                domicilio_fiscal: "".to_string(),
            };
            Ok(error_response)
        }
    } else {
        let status = response.status();
        let error_text = response.text().await?;
        error!("Error al llamar a OpenAI API: {} - {}", status, error_text);
        
        // Crear una respuesta de error para el error HTTP
        let error_response = RucResponse {
            status: false,
            ruc: nro_ruc.to_string(),
            nombre_persona_empresa: format!("Error al comunicarse con OpenAI: {}", status),
            nombre_comercial: "".to_string(),
            estado_contribuyente: "".to_string(),
            condicion_contribuyente: "".to_string(),
            domicilio_fiscal: "".to_string(),
        };
        Ok(error_response)
    }
}