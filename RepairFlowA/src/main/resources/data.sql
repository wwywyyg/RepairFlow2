-- ===========================
-- Device Category
-- ===========================
INSERT INTO device_category (id, name) VALUES
                                           (101, 'Phone'),
                                           (102, 'Laptop'),
                                           (103, 'Tablet'),
                                           (104, 'Smart Watch'),
                                           (105, 'Headphones / Earbuds'),
                                           (106, 'Game Console');

-- ===========================
-- Issue Types
-- Note: each question bind with device_category_id
-- ===========================

-- Phone issues
INSERT INTO issue_type (id, name, device_category_id) VALUES
                                                          (201, 'Screen Broken', 101),
                                                          (202, 'Battery Replacement', 101),
                                                          (203, 'Charging Issue', 101),
                                                          (204, 'Camera Repair', 101),
                                                          (205, 'Speaker / Mic Issue', 101),
                                                          (206, 'Water Damage', 101);

-- Laptop issues
INSERT INTO issue_type (id, name, device_category_id) VALUES
                                                          (301, 'Keyboard not working', 102),
                                                          (302, 'Trackpad malfunction', 102),
                                                          (303, 'SSD/HDD replacement', 102),
                                                          (304, 'Screen cracked / flickering', 102),
                                                          (305, 'Overheating / Fan issue', 102);

-- Tablet issues
INSERT INTO issue_type (id, name, device_category_id) VALUES
                                                          (401, 'Screen touch not responding', 103),
                                                          (402, 'Battery swelling', 103),
                                                          (403, 'Charging Port Repair', 103);

-- Smart Watch issues
INSERT INTO issue_type (id, name, device_category_id) VALUES
                                                          (501, 'Strap Replacement', 104),
                                                          (502, 'Battery Replacement', 104),
                                                          (503, 'Screen Crack', 104);

-- Headphones / Earbuds issues
INSERT INTO issue_type (id, name, device_category_id) VALUES
                                                          (601, 'Left/Right side no sound', 105),
                                                          (602, 'Charging case issue', 105),
                                                          (603, 'Battery replacement', 105),
                                                          (604, 'Bluetooth connection unstable', 105);

-- Game console issues
INSERT INTO issue_type (id, name, device_category_id) VALUES
                                                          (701, 'Joystick drift', 106),
                                                          (702, 'HDMI output failure', 106),
                                                          (703, 'Overheating', 106),
                                                          (704, 'Disk reader issue', 106);
