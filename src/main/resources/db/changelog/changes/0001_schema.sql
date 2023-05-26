-- liquibase formatted sql
-- changeset alejandro:0001

CREATE
OR
REPLACE
TABLE
    products(
        id INT PRIMARY KEY AUTO_INCREMENT,
        name VARCHAR(512) UNIQUE NOT NULL CHECK (TRIM(description) != ''),
        description TEXT,
        cost DECIMAL(19, 4) NOT NULL
    );

CREATE
OR
REPLACE
TABLE
    orders(
        id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
        time DATETIME NOT NULL
    );

CREATE
OR
REPLACE
TABLE
    order_item(
        order_id BIGINT UNSIGNED NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
        product_id INT NOT NULL REFERENCES products(id),
        PRIMARY KEY (order_id, product_id)
    );