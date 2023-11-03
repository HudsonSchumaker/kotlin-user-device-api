CREATE TABLE IF NOT EXISTS devices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    serial_number VARCHAR(255) NOT NULL,
    uuid VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    user_id INT, -- Reference to the User table
    FOREIGN KEY (user_id) REFERENCES users(id)
);
