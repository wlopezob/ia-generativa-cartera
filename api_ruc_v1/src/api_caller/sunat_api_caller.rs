use rand::distr::Alphanumeric;
use rand::{rng, thread_rng, Rng};
use reqwest::{Client, Error, Response};
use log::{info, error, warn};
use std::collections::HashMap;
use std::time::Duration;
use std::thread;

use crate::exception::api_error::ApiError;

/// Consulta información de un RUC en la página de SUNAT.
///
/// # Arguments
///
/// * `nro_ruc` - Número de RUC a consultar
/// * `timeout_secs` - Tiempo máximo de espera para la respuesta (segundos)
///
/// # Returns
///
/// Resultado con la respuesta HTTP o un error
pub async fn consultar_ruc_caller(nro_ruc: &str, timeout_secs: u64) -> Result<Response, ApiError> {
    let url = "https://e-consultaruc.sunat.gob.pe/cl-ti-itmrconsruc/jcrS00Alias";
    let recaptcha_token = generate_recaptcha_token();
    
    // Crear un cliente HTTP con timeout configurado
    let client = Client::builder()
        .timeout(Duration::from_secs(timeout_secs))
        .build()?;

    // Configurar los headers - exactamente como en Python
    let mut headers = reqwest::header::HeaderMap::new();
    headers.insert("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7".parse().unwrap());
    headers.insert("Accept-Language", "es-ES,es;q=0.9,en;q=0.8".parse().unwrap());
    headers.insert("Cache-Control", "no-cache".parse().unwrap());
    headers.insert("Connection", "keep-alive".parse().unwrap());
    headers.insert("Content-Type", "application/x-www-form-urlencoded".parse().unwrap());
    headers.insert("Origin", "https://e-consultaruc.sunat.gob.pe".parse().unwrap());
    headers.insert("Pragma", "no-cache".parse().unwrap());
    headers.insert("Referer", "https://e-consultaruc.sunat.gob.pe/cl-ti-itmrconsruc/FrameCriterioBusquedaWeb.jsp".parse().unwrap());
    headers.insert("Sec-Fetch-Dest", "document".parse().unwrap());
    headers.insert("Sec-Fetch-Mode", "navigate".parse().unwrap());
    headers.insert("Sec-Fetch-Site", "same-origin".parse().unwrap());
    headers.insert("Sec-Fetch-User", "?1".parse().unwrap());
    headers.insert("Upgrade-Insecure-Requests", "1".parse().unwrap());
    headers.insert("sec-ch-ua", "\"Google Chrome\";v=\"133\", \"Chromium\";v=\"133\", \"Not-A.Brand\";v=\"99\"".parse().unwrap());
    headers.insert("sec-ch-ua-mobile", "?0".parse().unwrap());
    headers.insert("sec-ch-ua-platform", "\"Windows\"".parse().unwrap());
    
    // Configurar los datos del formulario
    let mut form_data = HashMap::new();
    form_data.insert("accion", "consPorRuc");
    form_data.insert("razSoc", "");
    form_data.insert("nroRuc", nro_ruc);
    form_data.insert("nrodoc", "");
    form_data.insert("token", &recaptcha_token);
    form_data.insert("contexto", "ti-it");
    form_data.insert("modo", "1");
    form_data.insert("rbtnTipo", "1");
    form_data.insert("search1", nro_ruc);
    form_data.insert("tipdoc", "1");
    form_data.insert("search2", "");
    form_data.insert("search3", "");
    form_data.insert("codigo", "");
    
    info!("Enviando formulario de consulta para RUC {}", nro_ruc);
    
    // Realizar la petición POST
    let response = client.post(url)
        .headers(headers)
        .form(&form_data)
        .send()
        .await.map_err(|e| ApiError::ReqwestError(e));
        
    match &response {
        Ok(res) => {
            if res.status().is_success() {
                info!("Consulta exitosa para RUC {}", nro_ruc);
            } else {
                error!("Error en la consulta. Código de estado: {}", res.status());
            }
        },
        Err(e) => {
            error!("Error al realizar la petición: {}", e);
        }
    }
    
    // Si llegamos aquí, devolvemos la respuesta o error del último intento
    response
}

/// Genera una cadena aleatoria para simular un token de reCAPTCHA
/// 
/// # Arguments
///
/// * `token_length` - La longitud deseada del token
///
/// # Returns
///
/// Una cadena aleatoria que simula un token
pub fn generate_key(token_length: usize) -> String {
    // Generar una cadena aleatoria de caracteres alfanuméricos
    rng().sample_iter(&Alphanumeric)
        .take(token_length)
        .map(char::from)
        .collect()
}

/// Genera un token de reCAPTCHA simulado con longitud predeterminada
/// 
/// # Returns
///
/// Una cadena aleatoria que simula un token de reCAPTCHA
pub fn generate_recaptcha_token() -> String {
    // La longitud típica de los tokens de reCAPTCHA es alrededor de 400-500 caracteres
    generate_key(52)
}
