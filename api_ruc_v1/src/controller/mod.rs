use actix_web::{get, post, web, HttpResponse, Responder};

pub mod ruc_controller;

pub fn config(cfg: &mut web::ServiceConfig) {
    // Configuramos la ruta de consulta
    cfg.service(
        web::scope("/consulta").configure(ruc_controller::config)
    );
}
