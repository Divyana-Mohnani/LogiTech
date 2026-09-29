-- ====================================================================
-- Inventory Management System Database Schema
-- Matches RTU B.Tech Project Synopsis Specifications
-- ====================================================================

CREATE DATABASE IF NOT EXISTS inventory_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE inventory_db;

-- 1. Table: DESIGN (Parent of variants)
CREATE TABLE IF NOT EXISTS design (
    design_no VARCHAR(50) NOT NULL,
    design_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL COMMENT 'Swimwear, Footwear, Accessories',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (design_no),
    INDEX idx_design_category (category)
) ENGINE=InnoDB;

-- 2. Table: COLOUR (Master lookup for extendable colours)
CREATE TABLE IF NOT EXISTS colour (
    colour_code VARCHAR(10) NOT NULL,
    colour_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (colour_code)
) ENGINE=InnoDB;

-- 3. Table: STOCK_CONDITION (Master lookup: Normal, Defective, Old, Dead stock)
CREATE TABLE IF NOT EXISTS stock_condition (
    condition_id INT AUTO_INCREMENT NOT NULL,
    condition_name VARCHAR(50) NOT NULL UNIQUE,
    PRIMARY KEY (condition_id)
) ENGINE=InnoDB;

-- 4. Table: VARIANT (Structured variant under design)
-- Code format: GENDER/SIZE/LENGTH/COLOUR (e.g. M/L/B/BK for black trunks for males in size large)
CREATE TABLE IF NOT EXISTS variant (
    variant_code VARCHAR(100) NOT NULL,
    design_no VARCHAR(50) NOT NULL,
    gender VARCHAR(10) NOT NULL COMMENT 'M = Gents, W = Ladies, B = Boys, G = Girls',
    size VARCHAR(20) NOT NULL COMMENT 'S, M, L, XL, XXL, FS, 6, 7, 8, etc., or NA',
    length VARCHAR(20) NOT NULL COMMENT 'T, SH, CP, FP, FS, HS, SL, or NA',
    colour_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (variant_code),
    INDEX idx_variant_design (design_no),
    INDEX idx_variant_colour (colour_code),
    CONSTRAINT fk_variant_design FOREIGN KEY (design_no) REFERENCES design(design_no) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_variant_colour FOREIGN KEY (colour_code) REFERENCES colour(colour_code) ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 5. Table: STOCK (Quantities, pricing, condition, special notes)
CREATE TABLE IF NOT EXISTS stock (
    stock_id INT AUTO_INCREMENT NOT NULL,
    variant_code VARCHAR(100) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    vendor_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    mrp DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    condition_id INT NOT NULL,
    note TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (stock_id),
    INDEX idx_stock_variant (variant_code),
    INDEX idx_stock_condition (condition_id),
    CONSTRAINT fk_stock_variant FOREIGN KEY (variant_code) REFERENCES variant(variant_code) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_stock_condition FOREIGN KEY (condition_id) REFERENCES stock_condition(condition_id) ON UPDATE CASCADE,
    CONSTRAINT chk_quantity_non_negative CHECK (quantity >= 0)
) ENGINE=InnoDB;

-- 6. Table: APP_USER (Authentication and Role-Based Access: OWNER, STAFF)
CREATE TABLE IF NOT EXISTS app_user (
    user_id INT AUTO_INCREMENT NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL COMMENT 'OWNER or STAFF',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
) ENGINE=InnoDB;
