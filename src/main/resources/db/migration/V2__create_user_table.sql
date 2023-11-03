CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    birthday DATE,
    address_id INT,
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);
