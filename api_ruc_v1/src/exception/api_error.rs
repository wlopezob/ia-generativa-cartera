#[derive(thiserror::Error, Debug)]
pub enum ApiError {
    #[error("Generic error: {0}")]
	Generic(String),
    
    #[error("Error de servicio: {0}")]
    ReqwestError(#[from] reqwest::Error),

}