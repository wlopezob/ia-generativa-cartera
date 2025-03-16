use super::error_response::ApiException;
use super::error_type::ErrorType;
use std::fmt;

#[derive(thiserror::Error, Debug)]
pub enum ApiError {
    #[error("Generic error: {0}")]
    Generic(String),
    
    #[error("Error de servicio: {0}")]
    ReqwestError(#[from] reqwest::Error),
    
    #[error("Error al deserializar: {0}")]
    SerdeError(#[from] serde_json::Error),
    
    #[error("Error de validación: {0}")]
    ValidationError(String),
    
    #[error("Error de servicio externo: {0}")]
    ExternalServiceError(String, ErrorType),
    
    #[error("Error del servidor: {0}")]
    ServerError(String),
}

impl ApiError {
    pub fn to_api_exception(&self, component: &str) -> ApiException {
        match self {
            ApiError::Generic(msg) => {
                ApiException::new(ErrorType::UnknownError, component.to_string(), Some(msg.clone()))
            },
            ApiError::ReqwestError(err) => {
                if err.is_timeout() {
                    ApiException::new(ErrorType::RequestTimeout, component.to_string(), Some(err.to_string()))
                } else if err.is_connect() {
                    ApiException::new(ErrorType::BadGateway, component.to_string(), Some(err.to_string()))
                } else {
                    ApiException::new(ErrorType::InternalServerError, component.to_string(), Some(err.to_string()))
                }
            },
            ApiError::SerdeError(err) => {
                ApiException::new(ErrorType::DeserializationError, component.to_string(), Some(err.to_string()))
            },
            ApiError::ValidationError(msg) => {
                ApiException::new(ErrorType::InvalidInput, component.to_string(), Some(msg.clone()))
            },
            ApiError::ExternalServiceError(msg, error_type) => {
                ApiException::new(error_type.clone(), component.to_string(), Some(msg.clone()))
            },
            ApiError::ServerError(msg) => {
                ApiException::new(ErrorType::InternalServerError, component.to_string(), Some(msg.clone()))
            },
        }
    }
}