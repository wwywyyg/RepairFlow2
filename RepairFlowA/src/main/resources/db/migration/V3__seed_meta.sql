-- V3__seed_meta.sql
-- Seed device categories and issue types

-- ===========================
-- Device Categories
-- ===========================
INSERT INTO device_category (name) VALUES
                                       ('Phone'),
                                       ('Laptop'),
                                       ('Tablet'),
                                       ('Smart Watch'),
                                       ('Headphones / Earbuds'),
                                       ('Game Console')
    ON CONFLICT (name) DO NOTHING;

-- ===========================
-- Issue Types
-- ===========================

-- Phone issues
INSERT INTO issue_type (name, device_category_id) VALUES
                                                      ('Screen Broken',        (SELECT id FROM device_category WHERE name='Phone')),
                                                      ('Battery Replacement',  (SELECT id FROM device_category WHERE name='Phone')),
                                                      ('Charging Issue',       (SELECT id FROM device_category WHERE name='Phone')),
                                                      ('Camera Repair',        (SELECT id FROM device_category WHERE name='Phone')),
                                                      ('Speaker / Mic Issue',  (SELECT id FROM device_category WHERE name='Phone')),
                                                      ('Water Damage',         (SELECT id FROM device_category WHERE name='Phone'))
    ON CONFLICT DO NOTHING;

-- Laptop issues
INSERT INTO issue_type (name, device_category_id) VALUES
                                                      ('Keyboard not working',        (SELECT id FROM device_category WHERE name='Laptop')),
                                                      ('Trackpad malfunction',       (SELECT id FROM device_category WHERE name='Laptop')),
                                                      ('SSD/HDD replacement',        (SELECT id FROM device_category WHERE name='Laptop')),
                                                      ('Screen cracked / flickering', (SELECT id FROM device_category WHERE name='Laptop')),
                                                      ('Overheating / Fan issue',     (SELECT id FROM device_category WHERE name='Laptop'))
    ON CONFLICT DO NOTHING;

-- Tablet issues
INSERT INTO issue_type (name, device_category_id) VALUES
                                                      ('Screen touch not responding', (SELECT id FROM device_category WHERE name='Tablet')),
                                                      ('Battery swelling',            (SELECT id FROM device_category WHERE name='Tablet')),
                                                      ('Charging Port Repair',        (SELECT id FROM device_category WHERE name='Tablet'))
    ON CONFLICT DO NOTHING;

-- Smart Watch issues
INSERT INTO issue_type (name, device_category_id) VALUES
                                                      ('Strap Replacement',   (SELECT id FROM device_category WHERE name='Smart Watch')),
                                                      ('Battery Replacement', (SELECT id FROM device_category WHERE name='Smart Watch')),
                                                      ('Screen Crack',        (SELECT id FROM device_category WHERE name='Smart Watch'))
    ON CONFLICT DO NOTHING;

-- Headphones / Earbuds issues
INSERT INTO issue_type (name, device_category_id) VALUES
                                                      ('Left/Right side no sound',       (SELECT id FROM device_category WHERE name='Headphones / Earbuds')),
                                                      ('Charging case issue',            (SELECT id FROM device_category WHERE name='Headphones / Earbuds')),
                                                      ('Battery replacement',            (SELECT id FROM device_category WHERE name='Headphones / Earbuds')),
                                                      ('Bluetooth connection unstable',  (SELECT id FROM device_category WHERE name='Headphones / Earbuds'))
    ON CONFLICT DO NOTHING;

-- Game Console issues
INSERT INTO issue_type (name, device_category_id) VALUES
                                                      ('Joystick drift',        (SELECT id FROM device_category WHERE name='Game Console')),
                                                      ('HDMI output failure',   (SELECT id FROM device_category WHERE name='Game Console')),
                                                      ('Overheating',           (SELECT id FROM device_category WHERE name='Game Console')),
                                                      ('Disk reader issue',     (SELECT id FROM device_category WHERE name='Game Console'))
    ON CONFLICT DO NOTHING;
