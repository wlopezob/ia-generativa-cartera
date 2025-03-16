use serde::Serialize;

#[derive(Serialize)]
pub struct ApiExceptionResponse {
    pub code: u16,
    pub message: String,
    pub component: String,
}