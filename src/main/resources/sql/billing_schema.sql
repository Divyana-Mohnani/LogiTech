-- ====================================================================
-- POS Billing & Sales Invoicing Schema Extension
-- Automatically links sales transactions to variant codes and decrements stock
-- ====================================================================

USE inventory_db;

-- 1. Table: SALES_BILL (Master invoice header)
CREATE TABLE IF NOT EXISTS sales_bill (
    bill_no VARCHAR(50) NOT NULL,
    customer_name VARCHAR(100) DEFAULT 'Walk-in Customer',
    customer_phone VARCHAR(20) DEFAULT '',
    subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT 'GST (e.g. 5% or 12%)',
    net_total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    payment_mode VARCHAR(20) NOT NULL DEFAULT 'CASH' COMMENT 'CASH, UPI, CARD',
    cashier_id INT,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (bill_no),
    INDEX idx_sales_date (bill_date),
    CONSTRAINT fk_sales_cashier FOREIGN KEY (cashier_id) REFERENCES app_user(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 2. Table: SALES_ITEM (Itemized sales line items)
CREATE TABLE IF NOT EXISTS sales_item (
    item_id INT AUTO_INCREMENT NOT NULL,
    bill_no VARCHAR(50) NOT NULL,
    variant_code VARCHAR(100) NOT NULL,
    design_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_mrp DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    discount_percent DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    line_total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (item_id),
    INDEX idx_item_bill (bill_no),
    INDEX idx_item_variant (variant_code),
    CONSTRAINT fk_item_bill FOREIGN KEY (bill_no) REFERENCES sales_bill(bill_no) ON DELETE CASCADE,
    CONSTRAINT fk_item_variant FOREIGN KEY (variant_code) REFERENCES variant(variant_code) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 3. Seed initial sample sales bills for historical reporting
INSERT INTO sales_bill (bill_no, customer_name, customer_phone, subtotal, discount_amount, tax_amount, net_total, payment_mode, cashier_id, bill_date) VALUES
('INV-2026-0001', 'Rahul Sharma', '9876543210', 999.00, 0.00, 49.95, 1048.95, 'UPI', 2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('INV-2026-0002', 'Priya Verma', '9811223344', 2098.00, 209.80, 94.41, 1982.61, 'CARD', 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('INV-2026-0003', 'Walk-in Customer', '', 599.00, 0.00, 29.95, 628.95, 'CASH', 2, NOW())
ON DUPLICATE KEY UPDATE net_total=VALUES(net_total);

INSERT INTO sales_item (bill_no, variant_code, design_name, quantity, unit_mrp, discount_percent, line_total) VALUES
('INV-2026-0001', 'M/L/B/BK', 'Classic Retail Swim Trunk', 1, 999.00, 0.00, 999.00),
('INV-2026-0002', 'M/M/B/NV', 'Classic Retail Swim Trunk', 1, 999.00, 10.00, 899.10),
('INV-2026-0002', 'M/FS/NA/NV', 'Mirrored Anti-Fog Swim Goggles', 1, 599.00, 0.00, 599.00),
('INV-2026-0003', 'M/9/NA/BK', 'HydroTrek Aqua Water Shoes', 1, 799.00, 0.00, 799.00)
ON DUPLICATE KEY UPDATE line_total=VALUES(line_total);
