use actix_web::{App, HttpServer, web};
use crate::controller;
use std::env;
use log::info;

pub async fn run() -> std::io::Result<()> {
    // Obtener base_path o usar un valor predeterminado (string vacío)
    let base_path = env::var("API_BASE_PATH").unwrap_or_else(|_| {
        info!("API_BASE_PATH no configurado, usando valor predeterminado ''");
        String::from("")
    });

    info!("Iniciando servidor con base path: '{}'", base_path);

    HttpServer::new(move || {
        let app = App::new();
        
        // Si el base path es vacío, no usamos scope
        if base_path.is_empty() {
            app.configure(controller::config)
        } else {
            app.service(
                web::scope(&base_path)
                    .configure(controller::config)
            )
        }
    })
    .bind(("0.0.0.0", 8080))?
    .run()
    .await
}
