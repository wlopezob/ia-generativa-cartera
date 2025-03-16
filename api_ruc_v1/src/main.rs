mod server;
mod controller;
mod modelo;
mod services;
mod util;
mod api_caller;
mod exception;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    // Cargar variables de entorno y configurar logger solo aquí
    dotenv::dotenv().ok();
    env_logger::init_from_env(env_logger::Env::new().default_filter_or("info"));
    
    server::run().await
}

