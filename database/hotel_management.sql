-- import in phpMyAdmin (XAMPP), all names, ic and phone numbers are made up
CREATE DATABASE IF NOT EXISTS hotel_management CHARACTER SET utf8mb4;
USE hotel_management;

CREATE TABLE staff (
    staffID VARCHAR(5) PRIMARY KEY,
    name VARCHAR(32) NOT NULL,
    ic VARCHAR(14) NOT NULL,
    phone VARCHAR(13),
    gender TINYINT(1) NOT NULL,
    salary DOUBLE NOT NULL DEFAULT 0,
    password VARCHAR(255) NOT NULL DEFAULT '123456'
);

CREATE TABLE customer (
    customerID VARCHAR(5) PRIMARY KEY,
    name VARCHAR(32) NOT NULL,
    ic VARCHAR(14) NOT NULL,
    phone VARCHAR(13),
    gender TINYINT(1) NOT NULL,
    level INT NOT NULL DEFAULT 1,
    totalExpenses DOUBLE NOT NULL DEFAULT 0
);

CREATE TABLE room (
    roomID VARCHAR(5) PRIMARY KEY,
    roomType ENUM('DeluxeSingle', 'DeluxeDouble', 'PremiumSuite') NOT NULL,
    status ENUM('Available', 'Occupied', 'Preparing') NOT NULL DEFAULT 'Available'
);

CREATE TABLE diningtable (
    tableID VARCHAR(5) PRIMARY KEY,
    status ENUM('Available', 'Occupied', 'Preparing') NOT NULL DEFAULT 'Available'
);

CREATE TABLE booking (
    bookingID INT AUTO_INCREMENT PRIMARY KEY,
    customerID VARCHAR(5) NOT NULL,
    roomID VARCHAR(5) NOT NULL,
    checkInDate DATE NOT NULL,
    checkInTime TIME NOT NULL,
    checkOutDate DATE NOT NULL,
    mealPlan VARCHAR(255) NOT NULL,
    note VARCHAR(255) NOT NULL DEFAULT '',
    totalPrice DOUBLE NOT NULL,
    status ENUM('Confirmed', 'Checked Out', 'Cancelled') NOT NULL DEFAULT 'Confirmed',
    FOREIGN KEY (customerID) REFERENCES customer(customerID),
    FOREIGN KEY (roomID) REFERENCES room(roomID)
);

CREATE TABLE invoice (
    invoiceID INT AUTO_INCREMENT PRIMARY KEY,
    bookingID INT,
    customerID VARCHAR(5) NOT NULL,
    items VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (bookingID) REFERENCES booking(bookingID),
    FOREIGN KEY (customerID) REFERENCES customer(customerID)
);

CREATE TABLE receipt (
    receiptID INT AUTO_INCREMENT PRIMARY KEY,
    bookingID INT,
    customerID VARCHAR(5) NOT NULL,
    amount DOUBLE NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (bookingID) REFERENCES booking(bookingID),
    FOREIGN KEY (customerID) REFERENCES customer(customerID)
);

INSERT INTO staff (staffID, name, ic, phone, gender, salary, password) VALUES
('S0001', 'Aisyah binti Rahman', '950101-14-1234', '012-0000101', 0, 3200, '123456'),
('S0002', 'Tan Wei Jie', '970505-10-5678', '012-0000102', 1, 2600, '123456'),
('S0003', 'Ravi a/l Kumar', '990909-08-9012', '012-0000103', 1, 2400, '123456');

INSERT INTO customer (customerID, name, ic, phone, gender, level, totalExpenses) VALUES
('C0001', 'Lim Mei Ling', '880212-07-3344', '013-0000201', 0, 1, 39.99),
('C0002', 'Ahmad Faiz bin Osman', '900315-01-5566', '013-0000202', 1, 2, 284.94),
('C0003', 'Priya a/p Suresh', '920418-05-7788', '013-0000203', 0, 3, 0),
('C0004', 'Wong Kah Chun', '850621-12-9900', '013-0000204', 1, 4, 0),
('C0005', 'Nur Iman binti Zaki', '930724-03-1122', '013-0000205', 0, 5, 0),
('C0006', 'Daniel Lee', '010827-14-3344', '013-0000206', 1, 1, 30);

INSERT INTO room (roomID, roomType, status) VALUES
('R0101', 'DeluxeSingle', 'Occupied'),
('R0102', 'DeluxeSingle', 'Available'),
('R0103', 'DeluxeSingle', 'Available'),
('R0201', 'DeluxeDouble', 'Preparing'),
('R0202', 'DeluxeDouble', 'Available'),
('R0203', 'DeluxeDouble', 'Available'),
('R0301', 'PremiumSuite', 'Occupied'),
('R0302', 'PremiumSuite', 'Available');

INSERT INTO diningtable (tableID, status) VALUES
('T01', 'Available'), ('T02', 'Occupied'), ('T03', 'Available'), ('T04', 'Preparing');

-- totals follow BookingDetail: (room + meals) x nights, then the member discount
INSERT INTO booking (bookingID, customerID, roomID, checkInDate, checkInTime, checkOutDate, mealPlan, note, totalPrice, status) VALUES
(1, 'C0002', 'R0201', '2026-10-28', '15:00', '2026-10-31', 'Breakfast', 'Late check-in', 284.94, 'Checked Out'),
(2, 'C0003', 'R0301', '2026-11-01', '14:00', '2026-11-04', 'Breakfast, Dinner', '', 617.92, 'Confirmed'),
(3, 'C0001', 'R0101', '2026-11-02', '16:30', '2026-11-03', 'No meals', '', 39.99, 'Confirmed'),
(4, 'C0005', 'R0302', '2026-10-20', '14:00', '2026-10-22', 'Breakfast, Lunch, Dinner', 'Honeymoon', 462.16, 'Cancelled');

INSERT INTO invoice (bookingID, customerID, items, amount, date) VALUES
(2, 'C0003', 'R0301 Premium Suite, 3 night(s), Breakfast, Dinner', 617.92, '2026-11-01'),
(NULL, 'C0004', 'Extra Bed', 50.00, '2026-11-02');

INSERT INTO receipt (bookingID, customerID, amount, date) VALUES
(1, 'C0002', 284.94, '2026-10-31'),
(3, 'C0001', 39.99, '2026-11-02'),
(NULL, 'C0006', 30.00, '2026-11-02');
