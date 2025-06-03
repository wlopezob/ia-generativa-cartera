-- Migración para permitir asignación manual de IDs en chat_sessions
-- Elimina cualquier restricción de auto-generación y asegura que la columna ID sea UUID

-- Verificar y actualizar la estructura de la tabla si es necesario
ALTER TABLE chat_sessions 
ALTER COLUMN id SET NOT NULL;

-- Agregar índice en user_id para mejor performance
CREATE INDEX IF NOT EXISTS idx_chat_sessions_user_id ON chat_sessions(user_id);

-- Agregar índice en is_active para consultas de sesiones activas
CREATE INDEX IF NOT EXISTS idx_chat_sessions_active ON chat_sessions(is_active);

-- Agregar índice compuesto para consultas frecuentes
CREATE INDEX IF NOT EXISTS idx_chat_sessions_user_active ON chat_sessions(user_id, is_active);

-- Comentario sobre la tabla
COMMENT ON TABLE chat_sessions IS 'Tabla de sesiones de chat con soporte para IDs manuales'; 