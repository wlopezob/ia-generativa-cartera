CREATE TABLE IF NOT EXISTS personas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    edad INTEGER NOT NULL,
    fecha DATE NOT NULL,
    tipo_persona VARCHAR(10) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    saldo DOUBLE
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_persona_id BIGINT NOT NULL,
    receiver_persona_id BIGINT NOT NULL,
    fecha TIMESTAMP NOT NULL,
    monto DOUBLE NOT NULL,
    FOREIGN KEY (sender_persona_id) REFERENCES personas(id),
    FOREIGN KEY (receiver_persona_id) REFERENCES personas(id)
);
