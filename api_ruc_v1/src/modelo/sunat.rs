use serde::{Serialize, Deserialize};

// Modelo de entrada - Solicitud
#[derive(Serialize, Deserialize)]
pub struct RucRequest {
    #[serde(rename = "nroDoc")]
    pub nro_ruc: String,
}

// Modelo de salida - Respuesta con información del RUC
#[derive(Serialize, Deserialize)]
pub struct RucResponse {
    pub status: bool,
    pub ruc: String,
    #[serde(rename = "nombrePersonaEmpresa")]
    pub nombre_persona_empresa: String,
    #[serde(rename = "nombreComercial")]
    pub nombre_comercial: String,
    #[serde(rename = "estadoContribuyente")]
    pub estado_contribuyente: String,
    #[serde(rename = "condicionContribuyente")]
    pub condicion_contribuyente: String,
    #[serde(rename = "domicilioFiscal")]
    pub domicilio_fiscal: String,
}

#[derive(Serialize, Deserialize)]
pub struct SunatRequest {
    #[serde(rename = "nroDoc")]
    pub nro_ruc: String,
    #[serde(rename = "recaptchaToken", default)]
    pub recaptcha_token: Option<String>,
}