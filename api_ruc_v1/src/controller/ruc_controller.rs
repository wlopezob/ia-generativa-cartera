use actix_web::{post, web, HttpResponse, Responder};
use crate::{
    modelo::sunat::{RucRequest, RucResponse, SunatRequest},
    services::{ruc_service::obtener_empresa},
    exception::{error_response::ApiException, error_type::ErrorType}
};
use serde::{Deserialize, Serialize};
use log::{error, info};

#[post("/ruc")]
pub async fn consulta_ruc(consulta: web::Json<RucRequest>) -> impl Responder {
    // Mostramos el valor recibido
    info!("Recibido RUC: {}", consulta.nro_ruc);
    
    // Validar RUC
    if consulta.nro_ruc.len() != 11 || !consulta.nro_ruc.chars().all(|c| c.is_digit(10)) {
        let api_exception = ApiException::new(
            ErrorType::InvalidRuc,
            "ruc_controller".to_string(), 
            Some(format!("El RUC {} no tiene el formato correcto", consulta.nro_ruc))
        );
        return HttpResponse::BadRequest().json(api_exception);
    }
    
    // Consultamos a OpenAI con el RUC como mensaje y obtenemos la estructura RucResponse directamente
    match obtener_empresa(&consulta.nro_ruc).await {
        Ok(ruc_response) => {
            // Retornamos directamente la estructura RucResponse como JSON
            HttpResponse::Ok().json(ruc_response)
        },
        Err(e) => {
            error!("Error al obtener datos del RUC {}: {:?}", consulta.nro_ruc, e);
            let api_exception = e.to_api_exception("ruc_controller");
            HttpResponse::InternalServerError().json(api_exception)
        }
    }
}

// #[post("/sunat")]
// pub async fn consulta_sunat(consulta: web::Json<SunatRequest>) -> impl Responder {
//     println!("Consultando RUC en SUNAT: {}", consulta.nro_ruc);
      
//     // Timeout de 30 segundos
//     match consultar_ruc(&consulta.nro_ruc, 30).await {
//         Ok(response) => {
//             if response.status().is_success() {
//                 // Procesar la respuesta HTML
//                 match procesar_respuesta_sunat(response).await {
//                     Ok(html) => {
//                         // Crear respuesta con información extraída
//                         let respuesta = RucResponse {
//                             status: true,
//                             ruc: consulta.nro_ruc.clone(),
//                             nombre_persona_empresa: "Información extraída de SUNAT".to_string(),
//                             nombre_comercial: "".to_string(),
//                             estado_contribuyente: "".to_string(),
//                             condicion_contribuyente: "".to_string(),
//                             domicilio_fiscal: html, // Por simplificar, aquí pongo un fragmento del HTML
//                         };
                        
//                         HttpResponse::Ok().json(respuesta)
//                     },
//                     Err(e) => {
//                         let error_response = RucResponse {
//                             status: false,
//                             ruc: consulta.nro_ruc.clone(),
//                             nombre_persona_empresa: format!("Error al procesar la respuesta: {}", e),
//                             nombre_comercial: "".to_string(),
//                             estado_contribuyente: "".to_string(),
//                             condicion_contribuyente: "".to_string(),
//                             domicilio_fiscal: "".to_string(),
//                         };
//                         HttpResponse::InternalServerError().json(error_response)
//                     }
//                 }
//             } else {
//                 let error_response = RucResponse {
//                     status: false,
//                     ruc: consulta.nro_ruc.clone(),
//                     nombre_persona_empresa: format!("Error en la consulta. Código: {}", response.status()),
//                     nombre_comercial: "".to_string(),
//                     estado_contribuyente: "".to_string(),
//                     condicion_contribuyente: "".to_string(),
//                     domicilio_fiscal: "".to_string(),
//                 };
                
//                 HttpResponse::BadGateway().json(error_response)
//             }
//         },
//         Err(e) => {
//             let error_response = RucResponse {
//                 status: false,
//                 ruc: consulta.nro_ruc.clone(),
//                 nombre_persona_empresa: format!("Error al realizar la consulta: {}", e),
//                 nombre_comercial: "".to_string(),
//                 estado_contribuyente: "".to_string(),
//                 condicion_contribuyente: "".to_string(),
//                 domicilio_fiscal: "".to_string(),
//             };
            
//             HttpResponse::InternalServerError().json(error_response)
//         }
//     }
// }

pub fn config(cfg: &mut web::ServiceConfig) {
    cfg.service(consulta_ruc);
}
