-- ============================================
-- Tạo database
-- ============================================
CREATE DATABASE ShopAcc;
GO
USE ShopAcc;
GO

-- ============================================
-- Bảng STAFFS (nhân viên / quản trị)
-- ============================================
CREATE TABLE STAFFS (
                        staff_id INT IDENTITY PRIMARY KEY,
                        full_name NVARCHAR(100) NOT NULL,
                        email NVARCHAR(100) UNIQUE NOT NULL,
                        password NVARCHAR(100) NOT NULL,
                        role NVARCHAR(20) NOT NULL DEFAULT 'EMPLOYEE', -- ADMIN / EMPLOYEE
                        created_at DATETIME NOT NULL DEFAULT GETDATE()
);
GO

-- ============================================
-- Bảng CUSTOMERS (khách hàng)
-- ============================================
CREATE TABLE CUSTOMERS (
                           customer_id INT IDENTITY PRIMARY KEY,
                           full_name NVARCHAR(100) NOT NULL,
                           email NVARCHAR(100) UNIQUE NOT NULL,
                           password NVARCHAR(100) NOT NULL,
                           balance DECIMAL(18,2) NOT NULL DEFAULT 0
);
GO

-- ============================================
-- Bảng ACCOUNTS (tài khoản game bán)
-- ============================================
CREATE TABLE ACCOUNTS (
                          account_id INT IDENTITY PRIMARY KEY,
                          game_type NVARCHAR(50) NOT NULL,
                          account_name NVARCHAR(100) NOT NULL,
                          level INT NOT NULL,
                          rank NVARCHAR(50) NOT NULL,
                          description NVARCHAR(MAX),
                          price DECIMAL(18,2) NOT NULL,
                          sold BIT NOT NULL DEFAULT 0,
                          image NVARCHAR(200),
                          listed_at DATETIME DEFAULT GETDATE(),
                          staff_id INT NOT NULL,
                          CONSTRAINT fk_accounts_staff FOREIGN KEY (staff_id) REFERENCES STAFFS(staff_id)
);
GO

-- ============================================
-- Bảng ORDERS (đơn hàng)
-- ============================================
CREATE TABLE ORDERS (
                        order_id INT IDENTITY PRIMARY KEY,
                        customer_id INT NOT NULL,
                        account_id INT NOT NULL,
                        staff_id INT NOT NULL, -- nhân viên đăng account này
                        order_date DATETIME NOT NULL DEFAULT GETDATE(),
                        amount DECIMAL(18,2) NOT NULL,
                        status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
                        FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(customer_id),
                        FOREIGN KEY (account_id) REFERENCES ACCOUNTS(account_id),
                        FOREIGN KEY (staff_id) REFERENCES STAFFS(staff_id),
                        CONSTRAINT chk_orders_status CHECK (status IN ('PENDING','COMPLETED','FAILED'))
);
GO

-- ============================================
-- Bảng PAYMENTS (thanh toán)
-- ============================================
CREATE TABLE PAYMENTS (
                          payment_id INT IDENTITY PRIMARY KEY,
                          order_id INT NOT NULL,
                          amount DECIMAL(18,2) NOT NULL,
                          payment_date DATETIME NOT NULL DEFAULT GETDATE(),
                          FOREIGN KEY (order_id) REFERENCES ORDERS(order_id)
);
GO

-- ============================================
-- Bảng TRANSACTIONS (nạp tiền)
-- ============================================
CREATE TABLE TRANSACTIONS (
                              transaction_id INT IDENTITY PRIMARY KEY,
                              customer_id INT NOT NULL,
                              amount DECIMAL(18,2) NOT NULL,
                              transaction_date DATETIME NOT NULL DEFAULT GETDATE(),
                              FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(customer_id)
);
GO

-- ============================================
-- Trigger: Nạp tiền tự động cộng vào ví
-- ============================================
CREATE TRIGGER trg_AutoTopup
    ON TRANSACTIONS
    AFTER INSERT
    AS
BEGIN
    SET NOCOUNT ON;

    UPDATE c
    SET c.balance = c.balance + i.amount
    FROM CUSTOMERS c
             JOIN inserted i ON c.customer_id = i.customer_id;
END;
GO

-- ============================================
-- Trigger: Tự động thanh toán khi đặt đơn
-- ============================================
CREATE TRIGGER trg_AutoPayment
    ON ORDERS
    AFTER INSERT
    AS
BEGIN
    SET NOCOUNT ON;

    -- Cập nhật trạng thái đơn: đủ tiền + chưa bán => COMPLETED, ngược lại FAILED
    UPDATE o
    SET status = CASE
                     WHEN c.balance >= o.amount AND a.sold = 0 THEN 'COMPLETED'
                     ELSE 'FAILED'
        END
    FROM ORDERS o
             JOIN inserted i ON o.order_id = i.order_id
             JOIN CUSTOMERS c ON o.customer_id = c.customer_id
             JOIN ACCOUNTS a ON o.account_id = a.account_id;

    -- Insert vào PAYMENTS nếu đơn hợp lệ
    INSERT INTO PAYMENTS (order_id, amount)
    SELECT i.order_id, i.amount
    FROM inserted i
             JOIN CUSTOMERS c ON i.customer_id = c.customer_id
             JOIN ACCOUNTS a ON i.account_id = a.account_id
    WHERE c.balance >= i.amount AND a.sold = 0;

    -- Trừ tiền khách
    UPDATE c
    SET c.balance = c.balance - i.amount
    FROM CUSTOMERS c
             JOIN inserted i ON c.customer_id = i.customer_id
             JOIN ACCOUNTS a ON i.account_id = a.account_id
    WHERE c.balance >= i.amount AND a.sold = 0;

    -- Đánh dấu account đã bán
    UPDATE a
    SET sold = 1
    FROM ACCOUNTS a
             JOIN inserted i ON a.account_id = i.account_id
             JOIN CUSTOMERS c ON i.customer_id = c.customer_id
    WHERE c.balance >= i.amount AND a.sold = 0;
END;
GO

-- ============================================
-- Dữ liệu mẫu
-- ============================================

-- Staff
INSERT INTO STAFFS (full_name, email, password, role)
VALUES
    (N'Phạm Tuấn Hào', N'pthao101023@shopacc.com', N'123456', 'ADMIN'),
    (N'Le Employee', N'employee@shopacc.com', N'123456', 'EMPLOYEE');
GO

-- Khách hàng
INSERT INTO CUSTOMERS (full_name, email, password, balance)
VALUES
    (N'Nguyen Van A', N'a@example.com', N'123456', 0),
    (N'Tran Thi B', N'b@example.com', N'123456', 0),
    (N'Le Van C', N'c@example.com', N'123456', 0);
GO

-- Danh sách 50 tài khoản game
INSERT INTO ACCOUNTS (game_type, account_name, level, rank, description, price, sold, image, listed_at, staff_id)
VALUES
    (N'LOL', N'DragonSlayer99', 150, N'Platinum IV', N'Tài khoản có 50 skin, bao gồm skin Thần Thoại K/DA Akali', 500000, 0, N'acc1.webp', GETDATE(), 1),
    (N'Valorant', N'HeadshotKing', 80, N'Diamond II', N'Tài khoản có skin súng Prime Vandal, Reaver Sheriff', 750000, 0, N'acc2.webp', GETDATE(), 1),
    (N'TFT', N'TacticianPro', 60, N'Challenger', N'Tài khoản có 20 Little Legends, bao gồm Poggles Huyền Thoại', 600000, 0, N'acc3.webp', GETDATE(), 1),
    (N'LOL', N'ShadowNinja', 200, N'Gold I', N'Tài khoản có 80 skin, bao gồm Spirit Blossom Yasuo', 800000, 0, N'acc4.webp', GETDATE(), 1),
    (N'Valorant', N'PhantomX', 50, N'Platinum I', N'Tài khoản có skin súng Ion Phantom, Reaver Vandal', 550000, 0, N'acc5.webp', GETDATE(), 1),
    (N'TFT', N'StarMaster', 45, N'Master', N'Tài khoản có 15 Little Legends, 5 arena skin', 450000, 0, N'acc6.webp', GETDATE(), 1),
    (N'LOL', N'FireStorm22', 120, N'Silver II', N'Tài khoản có 30 skin, bao gồm High Noon Ashe', 400000, 0, N'acc7.webp', GETDATE(), 1),
    (N'Valorant', N'BlazeShot', 70, N'Gold III', N'Tài khoản có skin súng Glitchpop Phantom', 600000, 0, N'acc8.webp', GETDATE(), 1),
    (N'TFT', N'GalaxyTactician', 55, N'Diamond IV', N'Tài khoản có 10 Little Legends, 3 arena skin', 350000, 0, N'acc9.webp', GETDATE(), 1),
    (N'LOL', N'IceWarden', 180, N'Diamond III', N'Tài khoản có 70 skin, bao gồm PROJECT Vayne', 900000, 0, N'acc10.webp', GETDATE(), 1),
    (N'LOL', N'DragonSlayer99', 150, N'Platinum IV', N'Tài khoản có 50 skin, bao gồm skin Thần Thoại K/DA Akali', 500000, 0, N'acc1.webp', GETDATE(), 1),
    (N'Valorant', N'HeadshotKing', 80, N'Diamond II', N'Tài khoản có skin súng Prime Vandal, Reaver Sheriff', 750000, 0, N'acc2.webp', GETDATE(), 1),
    (N'TFT', N'TacticianPro', 60, N'Challenger', N'Tài khoản có 20 Little Legends, bao gồm Poggles Huyền Thoại', 600000, 0, N'acc3.webp', GETDATE(), 1),
    (N'LOL', N'ShadowNinja', 200, N'Gold I', N'Tài khoản có 80 skin, bao gồm Spirit Blossom Yasuo', 800000, 0, N'acc4.webp', GETDATE(), 1),
    (N'Valorant', N'PhantomX', 50, N'Platinum I', N'Tài khoản có skin súng Ion Phantom, Reaver Vandal', 550000, 0, N'acc5.webp', GETDATE(), 1),
    (N'TFT', N'StarMaster', 45, N'Master', N'Tài khoản có 15 Little Legends, 5 arena skin', 450000, 0, N'acc6.webp', GETDATE(), 1),
    (N'LOL', N'FireStorm22', 120, N'Silver II', N'Tài khoản có 30 skin, bao gồm High Noon Ashe', 400000, 0, N'acc7.webp', GETDATE(), 1),
    (N'Valorant', N'BlazeShot', 70, N'Gold III', N'Tài khoản có skin súng Glitchpop Phantom', 600000, 0, N'acc8.webp', GETDATE(), 1),
    (N'TFT', N'GalaxyTactician', 55, N'Diamond IV', N'Tài khoản có 10 Little Legends, 3 arena skin', 350000, 0, N'acc9.webp', GETDATE(), 1),
    (N'LOL', N'IceWarden', 180, N'Diamond III', N'Tài khoản có 70 skin, bao gồm PROJECT Vayne', 900000, 0, N'acc10.webp', GETDATE(), 1),
    (N'Valorant', N'StormPhantom', 65, N'Gold III', N'Tài khoản có skin súng Ion Vandal', 600000, 0, N'acc50.webp', GETDATE(), 1),
    (N'Valorant', N'PhantomX', 50, N'Platinum I', N'Tài khoản có skin súng Ion Phantom, Reaver Vandal', 550000, 0, N'acc5.webp', GETDATE(), 1),
    (N'TFT', N'StarMaster', 45, N'Master', N'Tài khoản có 15 Little Legends, 5 arena skin', 450000, 0, N'acc6.webp', GETDATE(), 1),
    (N'LOL', N'FireStorm22', 120, N'Silver II', N'Tài khoản có 30 skin, bao gồm High Noon Ashe', 400000, 0, N'acc7.webp', GETDATE(), 1),
    (N'Valorant', N'BlazeShot', 70, N'Gold III', N'Tài khoản có skin súng Glitchpop Phantom', 600000, 0, N'acc8.webp', GETDATE(), 1),
    (N'TFT', N'GalaxyTactician', 55, N'Diamond IV', N'Tài khoản có 10 Little Legends, 3 arena skin', 350000, 0, N'acc9.webp', GETDATE(), 1),
    (N'LOL', N'IceWarden', 180, N'Diamond III', N'Tài khoản có 70 skin, bao gồm PROJECT Vayne', 900000, 0, N'acc10.webp', GETDATE(), 1),
    (N'LOL', N'DragonSlayer99', 150, N'Platinum IV', N'Tài khoản có 50 skin, bao gồm skin Thần Thoại K/DA Akali', 500000, 0, N'acc1.webp', GETDATE(), 1),
    (N'Valorant', N'HeadshotKing', 80, N'Diamond II', N'Tài khoản có skin súng Prime Vandal, Reaver Sheriff', 750000, 0, N'acc2.webp', GETDATE(), 1),
    (N'TFT', N'TacticianPro', 60, N'Challenger', N'Tài khoản có 20 Little Legends, bao gồm Poggles Huyền Thoại', 600000, 0, N'acc3.webp', GETDATE(), 1),
    (N'LOL', N'ShadowNinja', 200, N'Gold I', N'Tài khoản có 80 skin, bao gồm Spirit Blossom Yasuo', 800000, 0, N'acc4.webp', GETDATE(), 1),
    (N'Valorant', N'PhantomX', 50, N'Platinum I', N'Tài khoản có skin súng Ion Phantom, Reaver Vandal', 550000, 0, N'acc5.webp', GETDATE(), 1),
    (N'TFT', N'StarMaster', 45, N'Master', N'Tài khoản có 15 Little Legends, 5 arena skin', 450000, 0, N'acc6.webp', GETDATE(), 1),
    (N'LOL', N'FireStorm22', 120, N'Silver II', N'Tài khoản có 30 skin, bao gồm High Noon Ashe', 400000, 0, N'acc7.webp', GETDATE(), 1),
    (N'Valorant', N'BlazeShot', 70, N'Gold III', N'Tài khoản có skin súng Glitchpop Phantom', 600000, 0, N'acc8.webp', GETDATE(), 1),
    (N'TFT', N'GalaxyTactician', 55, N'Diamond IV', N'Tài khoản có 10 Little Legends, 3 arena skin', 350000, 0, N'acc9.webp', GETDATE(), 1),
    (N'LOL', N'IceWarden', 180, N'Diamond III', N'Tài khoản có 70 skin, bao gồm PROJECT Vayne', 900000, 0, N'acc10.webp', GETDATE(), 1),
    (N'Valorant', N'StormPhantom', 65, N'Gold III', N'Tài khoản có skin súng Ion Vandal', 600000, 0, N'acc50.webp', GETDATE(), 1);
GO


-- ============================================
-- Insert TRANSACTIONS (nạp tiền cho khách)
-- => tự động cộng tiền vào CUSTOMERS nhờ trigger
-- ============================================
INSERT INTO TRANSACTIONS (customer_id, amount)
VALUES
    (1, 2000000), -- Nguyen Van A nạp 2 triệu
    (2, 1000000), -- Tran Thi B nạp 1 triệu
    (3, 5000000);  -- Le Van C nạp 500k
GO

-- ============================================
-- Insert ORDERS (khách mua acc)
-- => Trigger sẽ tự động cập nhật status, trừ tiền, tạo PAYMENTS
-- ============================================
INSERT INTO ORDERS (customer_id, account_id, staff_id, amount)
VALUES
    (1, 1, 1, 500000),  -- A mua acc1 (LOL)
    (2, 2, 1, 750000),  -- B mua acc2 (Valorant)
    (3, 3, 1, 600000);  -- C mua acc3 (TFT) -> có thể FAILED do chỉ có 500k
GO
