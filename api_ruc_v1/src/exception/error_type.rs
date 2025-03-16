use serde::{Serialize, Deserialize};

#[derive(Debug, Serialize, Deserialize, Clone)]
pub enum ErrorType {
    // Validation errors
    InvalidInput,
    InvalidRuc,
    
    // External service errors
    SunatError,
    OpenAiError,
    
    RequestTimeout,
    BadGateway,
    
    // Server errors
    InternalServerError,
    SerializationError,
    DeserializationError,
    
    // Generic error
    UnknownError,
}

impl ErrorType {
    pub fn as_code(&self) -> u16 {
        match self {
            // 400 range - client errors
            Self::InvalidInput => 400,
            Self::InvalidRuc => 400,
            
            // 500 range - server errors
            Self::SunatError => 502,
            Self::OpenAiError => 502,
            Self::RequestTimeout => 504,
            Self::BadGateway => 502,
            Self::InternalServerError => 500,
            Self::SerializationError => 500,
            Self::DeserializationError => 500,
            Self::UnknownError => 500,
        }
    }
    
    pub fn as_message(&self) -> String {
        match self {
            Self::InvalidInput => "Datos de entrada inválidos".to_string(),
            Self::InvalidRuc => "RUC inválido".to_string(),
            Self::SunatError => "Error al consultar el servicio de SUNAT".to_string(),
            Self::OpenAiError => "Error al consultar el servicio de OpenAI".to_string(),
            Self::RequestTimeout => "Tiempo de espera agotado".to_string(),
            Self::BadGateway => "Error en el servicio externo".to_string(),
            Self::InternalServerError => "Error interno del servidor".to_string(),
            Self::SerializationError => "Error al serializar datos".to_string(),
            Self::DeserializationError => "Error al deserializar datos".to_string(),
            Self::UnknownError => "Error desconocido".to_string(),
        }
    }
}
