use serde::Serialize;
use super::error_type::ErrorType;

#[derive(Serialize)]
pub struct ApiException {
    pub code: u16,
    pub message: String,
    pub component: String,
    pub error_type: ErrorType,
    pub details: Option<String>,
}

impl ApiException {
    pub fn new(error_type: ErrorType, component: String, details: Option<String>) -> Self {
        ApiException {
            code: error_type.as_code(),
            message: error_type.as_message(),
            component,
            error_type,
            details,
        }
    }
    
    pub fn with_message(error_type: ErrorType, component: String, message: String, details: Option<String>) -> Self {
        ApiException {
            code: error_type.as_code(),
            message,
            component,
            error_type,
            details,
        }
    }
}