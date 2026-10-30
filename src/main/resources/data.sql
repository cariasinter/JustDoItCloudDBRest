INSERT INTO users (user_name, name, email, type)
VALUES ('john_doe', 'John Doe','john@gmail.com', 'REGULAR')
ON DUPLICATE KEY UPDATE user_name = user_name;

INSERT INTO users (user_name, name, email, type)
VALUES ('ana_soto', 'Ana Soto','ana@gmail.com', 'ADMIN')
ON DUPLICATE KEY UPDATE user_name = user_name;

-- Insertar dos tareas para 'john_doe' de forma idempotente
INSERT INTO tasks (user_id, description, status)
SELECT u.id, 'Comprar leche',   'PENDING'
FROM users u WHERE u.user_name = 'john_doe'
ON DUPLICATE KEY UPDATE description = description;

INSERT INTO tasks (user_id, description, status)
SELECT u.id, 'Reparar llantas del carro',   'INPROGRESS'
FROM users u WHERE u.user_name = 'john_doe'
ON DUPLICATE KEY UPDATE description = description;

-- Insertar dos tareas para 'ana_soto' de forma idempotente
INSERT INTO tasks (user_id, description, status)
SELECT u.id, 'Configurar Usuarios',   'PENDING'
FROM users u WHERE u.user_name = 'ana_soto'
ON DUPLICATE KEY UPDATE description = description;
