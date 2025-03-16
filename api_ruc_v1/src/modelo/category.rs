use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
pub struct Category {
    pub id: i32,
    pub name: String,
    pub image: String,
}