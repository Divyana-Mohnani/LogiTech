-- ====================================================================
-- Inventory Management System Sample Seed Data
-- ====================================================================

USE inventory_db;

-- 1. Insert Colours
INSERT INTO colour (colour_code, colour_name) VALUES
('BK', 'Black'),
('BL', 'Blue'),
('RD', 'Red'),
('WH', 'White'),
('GR', 'Green'),
('YL', 'Yellow'),
('GY', 'Grey'),
('NV', 'Navy'),
('PK', 'Pink'),
('OR', 'Orange')
ON DUPLICATE KEY UPDATE colour_name=VALUES(colour_name);

-- 2. Insert Stock Conditions
INSERT INTO stock_condition (condition_id, condition_name) VALUES
(1, 'Normal'),
(2, 'Defective'),
(3, 'Old'),
(4, 'Dead stock')
ON DUPLICATE KEY UPDATE condition_name=VALUES(condition_name);

-- 3. Insert Users (Owner and Staff)
-- In production/real deployment, passwords can be hashed (SHA-256)
INSERT INTO app_user (user_id, username, password, full_name, role) VALUES
(1, 'owner', 'owner123', 'Store Owner', 'OWNER'),
(2, 'staff', 'staff123', 'Counter Staff', 'STAFF')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name), role=VALUES(role);

-- 4. Insert Sample Designs
INSERT INTO design (design_no, design_name, category, description) VALUES
('D1024', 'Classic Retail Swim Trunk', 'Swimwear', 'Chlorine-resistant 4-way stretch swim trunks for training and racing.'),
('D1025', 'AquaShield Long Sleeve Rashguard', 'Swimwear', 'UPF 50+ UV sun protection compression rash vest.'),
('D2210', 'HydroTrek Aqua Water Shoes', 'Footwear', 'Non-slip barefoot water shoes with rubber drainage outsole.'),
('D2215', 'Poolside Slide Sandals', 'Footwear', 'Lightweight EVA quick-dry slide sandals.'),
('D3050', 'Mirrored Anti-Fog Swim Goggles', 'Accessories', 'Competition wide-vision goggles with interchangeable nose bridges.'),
('D3060', 'Elite Silicone Swim Cap', 'Accessories', 'Non-snag 100% hypoallergenic silicone swimming cap.')
ON DUPLICATE KEY UPDATE design_name=VALUES(design_name), category=VALUES(category);

-- 5. Insert Sample Variants (Format: GENDER/SIZE/LENGTH/COLOUR e.g. M/L/B/BK)
INSERT INTO variant (variant_code, design_no, gender, size, length, colour_code) VALUES
-- Swimwear Trunks (Length = B)
('M/L/B/BK', 'D1024', 'M', 'L', 'B', 'BK'),
('M/M/B/NV', 'D1024', 'M', 'M', 'B', 'NV'),
('M/XL/SH/BL', 'D1024', 'M', 'XL', 'SH', 'BL'),
('B/S/B/RD', 'D1024', 'B', 'S', 'B', 'RD'),

-- Swimwear Tops (Sleeve length applied)
('M/L/FS/BK', 'D1025', 'M', 'L', 'FS', 'BK'),
('W/M/HS/BL', 'D1025', 'W', 'M', 'HS', 'BL'),
('W/S/SL/PK', 'D1025', 'W', 'S', 'SL', 'PK'),

-- Footwear (Numeric size, length NA)
('M/9/NA/BK', 'D2210', 'M', '9', 'NA', 'BK'),
('M/10/NA/GY', 'D2210', 'M', '10', 'NA', 'GY'),
('W/7/NA/BL', 'D2210', 'W', '7', 'NA', 'BL'),
('M/8/NA/NV', 'D2215', 'M', '8', 'NA', 'NV'),

-- Accessories (Free size or NA, length NA)
('M/FS/NA/NV', 'D3050', 'M', 'FS', 'NA', 'NV'),
('W/FS/NA/PK', 'D3050', 'W', 'FS', 'NA', 'PK'),
('M/FS/NA/BK', 'D3060', 'M', 'FS', 'NA', 'BK'),
('G/FS/NA/YL', 'D3060', 'G', 'FS', 'NA', 'YL')
ON DUPLICATE KEY UPDATE design_no=VALUES(design_no);

-- 6. Insert Sample Stock Lines (with conditions and special notes)
INSERT INTO stock (stock_id, variant_code, quantity, vendor_price, mrp, condition_id, note) VALUES
-- Normal sellable stock
(1, 'M/L/B/BK', 35, 450.00, 999.00, 1, 'Top seller for summer training camp.'),
(2, 'M/M/B/NV', 24, 450.00, 999.00, 1, 'Fresh arrival from vendor lot #48.'),
(3, 'M/L/FS/BK', 18, 650.00, 1499.00, 1, 'Standard stock.'),
(4, 'M/9/NA/BK', 15, 380.00, 799.00, 1, 'Fast moving footwear line.'),
(5, 'M/FS/NA/NV', 40, 220.00, 599.00, 1, 'Counter display stock.'),

-- Defective stock (Condition 2)
(6, 'B/S/B/RD', 2, 450.00, 999.00, 2, 'Waist drawcord stitching loose. Return to vendor.'),
(7, 'G/FS/NA/YL', 3, 110.00, 299.00, 2, 'Silicone tear near front logo imprint.'),

-- Old season stock (Condition 3)
(8, 'M/XL/SH/BL', 8, 420.00, 950.00, 3, 'Previous season leftover. Marked for 20% discount.'),
(9, 'M/8/NA/NV', 5, 250.00, 599.00, 3, 'Previous model run.'),

-- Dead stock (Condition 4)
(10, 'W/S/SL/PK', 12, 600.00, 1399.00, 4, 'Pink sleeveless no longer in demand. Mark down 50% for clearance.')
ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), mrp=VALUES(mrp), note=VALUES(note);
