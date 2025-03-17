use super::error_response::ApiException;
use super::error_type::ErrorType;

// Default component name, can be overridden when calling the error functions
pub const DEFAULT_COMPONENT: &str = "api-ruc-v1";

// Generic errors
pub fn internal_server_error(msg: &str) -> ApiException {
    ApiException::new(
        ErrorType::InternalServerError,
        DEFAULT_COMPONENT.to_string(),
        Some(format!("Error interno del servidor: {}", msg))
    )
}

pub fn unknown_error(msg: &str) -> ApiException {
    ApiException::new(
        ErrorType::UnknownError,
        DEFAULT_COMPONENT.to_string(),
        Some(format!("Error desconocido: {}", msg))
    )
}

// Validation errors
pub fn invalid_input_error(msg: &str) -> ApiException {
    ApiException::new(
        ErrorType::InvalidInput,
        DEFAULT_COMPONENT.to_string(),
        Some(format!("Datos de entrada inválidos: {}", msg))
    )
}

pub fn invalid_ruc_error(ruc: &str) -> ApiException {
    ApiException::new(
        ErrorType::InvalidRuc,
        DEFAULT_COMPONENT.to_string(),
        Some(format!("El RUC {} no tiene el formato correcto", ruc))
    )
}

// External service errors
pub fn sunat_service_error(msg: &str) -> ApiException {
    ApiException::new(
        ErrorType::SunatError,
        DEFAULT_COMPONENT.to_string(),
        Some(format!("Error al consultar el servicio de SUNAT: {}", msg))
    )
}

pub fn openai_service_error(msg: &str) -> ApiException {
    ApiException::new(
        ErrorType::OpenAiError,
        DEFAULT_COMPONENT.to_string(),
        Some(format!("Error al consultar el servicio de OpenAI: {}", msg))
    )
}

pub fn request_timeout_error(msg: &str) -> ApiException {
    ApiException::new(
        ErrorType::RequestTimeout,
        DEFAULT_COMPONENT.to_string(),
        Some(format!("Tiempo de espera agotado: {}", msg))
    )
}

pub fn bad_gateway_error(msg: &str) -> ApiException {
    ApiException::new(
        ErrorType::BadGateway,
        DEFAULT_COMPONENT.to_string(),
        Some(format!("Error en el servicio externo: {}", msg))
    )
}

// Serialization errors
pub fn serialization_error(msg: &str) -> ApiException {
    ApiException::new(
        ErrorType::SerializationError,
        DEFAULT_COMPONENT.to_string(),
        Some(format!("Error al serializar datos: {}", msg))
    )
}

pub fn deserialization_error(msg: &str) -> ApiException {
    ApiException::new(
        ErrorType::DeserializationError,
        DEFAULT_COMPONENT.to_string(),
        Some(format!("Error al deserializar datos: {}", msg))
    )
}

// Custom message errors with specific error types
pub fn custom_error(error_type: ErrorType, msg: &str) -> ApiException {
    ApiException::with_message(
        error_type,
        DEFAULT_COMPONENT.to_string(),
        msg.to_string(),
        None
    )
}

pub fn custom_error_with_details(error_type: ErrorType, msg: &str, details: &str) -> ApiException {
    ApiException::with_message(
        error_type,
        DEFAULT_COMPONENT.to_string(),
        msg.to_string(),
        Some(details.to_string())
    )
}
