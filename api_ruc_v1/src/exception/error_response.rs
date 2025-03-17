use serde::Serialize;
use super::error_type::ErrorType;
use actix_web::{HttpResponse, ResponseError, http::StatusCode};
use std::fmt;

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

// Implement Display for ApiException
impl fmt::Display for ApiException {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "Error {} ({}): {}", self.code, self.component, self.message)
    }
}

// Implement Debug for ApiException
impl fmt::Debug for ApiException {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let details = self.details.as_deref().unwrap_or("No details provided");
        write!(
            f,
            "ApiException {{ code: {}, message: {}, component: {}, details: {} }}",
            self.code,
            self.message,
            self.component,
            details
        )
    }
}

// Implement ResponseError for ApiException to use with actix-web
impl ResponseError for ApiException {
    fn status_code(&self) -> StatusCode {
        StatusCode::from_u16(self.code).unwrap_or(StatusCode::INTERNAL_SERVER_ERROR)
    }

    fn error_response(&self) -> HttpResponse {
        HttpResponse::build(self.status_code())
            .json(self)
    }
}