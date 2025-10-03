-- ============================================
-- Tạo database
-- ============================================
CREATE DATABASE ShopAcc;
GO
USE ShopAcc;
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
                          listed_at DATETIME DEFAULT GETDATE()
);
GO

-- ============================================
-- Bảng ORDERS (đơn hàng)
-- ============================================
CREATE TABLE ORDERS (
                        order_id INT IDENTITY PRIMARY KEY,
                        customer_id INT NOT NULL,
                        account_id INT NOT NULL,
                        order_date DATETIME NOT NULL DEFAULT GETDATE(),
                        amount DECIMAL(18,2) NOT NULL,
                        status NVARCHAR(20) NOT NULL DEFAULT N'pending',
                        FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(customer_id),
                        FOREIGN KEY (account_id) REFERENCES ACCOUNTS(account_id)
);
GO

-- ============================================
-- Bảng PAYMENTS (thanh toán)
-- ============================================
CREATE TABLE PAYMENTS (
                          payment_id INT IDENTITY PRIMARY KEY,
                          order_id INT NOT NULL,
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

UPDATE CUSTOMERS
SET balance = balance + i.amount
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

    DECLARE @customer_id INT, @order_id INT, @amount DECIMAL(18,2),
        @balance DECIMAL(18,2), @account_id INT;

    -- Lấy thông tin đơn hàng
SELECT
    @order_id = order_id,
    @customer_id = customer_id,
    @amount = amount,
    @account_id = account_id
FROM inserted;

-- Lấy số dư khách
SELECT @balance = balance
FROM CUSTOMERS
WHERE customer_id = @customer_id;

-- Nếu đủ tiền
IF @balance >= @amount
BEGIN
            -- Trừ tiền
UPDATE CUSTOMERS
SET balance = balance - @amount
WHERE customer_id = @customer_id;

-- Thêm bản ghi thanh toán
INSERT INTO PAYMENTS (order_id)
VALUES (@order_id);

-- Cập nhật trạng thái đơn
UPDATE ORDERS
SET status = N'completed'
WHERE order_id = @order_id;

-- Đánh dấu acc đã bán
UPDATE ACCOUNTS
SET sold = 1
WHERE account_id = @account_id;
END
ELSE
BEGIN
            -- Không đủ tiền
UPDATE ORDERS
SET status = N'failed'
WHERE order_id = @order_id;
END
END;
GO

-- ============================================
-- Dữ liệu mẫu
-- ============================================

-- Khách hàng
INSERT INTO CUSTOMERS (full_name, email, password, balance)
VALUES
    (N'Nguyen Van A', N'a@example.com', N'123456', 0),
    (N'Tran Thi B', N'b@example.com', N'123456', 0),
    (N'Le Van C', N'c@example.com', N'123456', 0);
GO

-- Danh sách 50 tài khoản game
INSERT INTO ACCOUNTS (game_type, account_name, level, rank, description, price, sold, image, listed_at)
VALUES
    (N'LOL', N'DragonSlayer99', 150, N'Platinum IV', N'Tài khoản có 50 skin, bao gồm skin Thần Thoại K/DA Akali', 500000, 0, N'acc1.webp', GETDATE()),
    (N'Valorant', N'HeadshotKing', 80, N'Diamond II', N'Tài khoản có skin súng Prime Vandal, Reaver Sheriff', 750000, 0, N'acc2.webp', GETDATE()),
    (N'TFT', N'TacticianPro', 60, N'Challenger', N'Tài khoản có 20 Little Legends, bao gồm Poggles Huyền Thoại', 600000, 0, N'acc3.webp', GETDATE()),
    (N'LOL', N'ShadowNinja', 200, N'Gold I', N'Tài khoản có 80 skin, bao gồm Spirit Blossom Yasuo', 800000, 0, N'acc4.webp', GETDATE()),
    (N'Valorant', N'PhantomX', 50, N'Platinum I', N'Tài khoản có skin súng Ion Phantom, Reaver Vandal', 550000, 0, N'acc5.webp', GETDATE()),
    (N'TFT', N'StarMaster', 45, N'Master', N'Tài khoản có 15 Little Legends, 5 arena skin', 450000, 0, N'acc6.webp', GETDATE()),
    (N'LOL', N'FireStorm22', 120, N'Silver II', N'Tài khoản có 30 skin, bao gồm High Noon Ashe', 400000, 0, N'acc7.webp', GETDATE()),
    (N'Valorant', N'BlazeShot', 70, N'Gold III', N'Tài khoản có skin súng Glitchpop Phantom', 600000, 0, N'acc8.webp', GETDATE()),
    (N'TFT', N'GalaxyTactician', 55, N'Diamond IV', N'Tài khoản có 10 Little Legends, 3 arena skin', 350000, 0, N'acc9.webp', GETDATE()),
    (N'LOL', N'IceWarden', 180, N'Diamond III', N'Tài khoản có 70 skin, bao gồm PROJECT Vayne', 900000, 0, N'acc10.webp', GETDATE()),
    (N'Valorant', N'GhostRider', 65, N'Platinum II', N'Tài khoản có skin súng Oni Phantom', 650000, 0, N'acc11.webp', GETDATE()),
    (N'TFT', N'MoonlightPro', 70, N'Grandmaster', N'Tài khoản có 25 Little Legends, bao gồm Shisa Huyền Thoại', 700000, 0, N'acc12.webp', GETDATE()),
    (N'LOL', N'ThunderLord', 140, N'Gold II', N'Tài khoản có 40 skin, bao gồm Elementalist Lux', 550000, 0, N'acc13.webp', GETDATE()),
    (N'Valorant', N'SpecterX', 60, N'Silver I', N'Tài khoản có skin súng Reaver Operator', 500000, 0, N'acc14.webp', GETDATE()),
    (N'TFT', N'CosmicTactician', 50, N'Platinum III', N'Tài khoản có 12 Little Legends, 4 arena skin', 400000, 0, N'acc15.webp', GETDATE()),
    (N'LOL', N'StarGuardianX', 160, N'Platinum II', N'Tài khoản có 60 skin, bao gồm Star Guardian Jinx', 700000, 0, N'acc16.webp', GETDATE()),
    (N'Valorant', N'NeonBlaze', 75, N'Diamond I', N'Tài khoản có skin súng Prime Spectre', 800000, 0, N'acc17.webp', GETDATE()),
    (N'TFT', N'EclipseMaster', 65, N'Challenger', N'Tài khoản có 18 Little Legends, 6 arena skin', 650000, 0, N'acc18.webp', GETDATE()),
    (N'LOL', N'DarkKnight99', 130, N'Silver III', N'Tài khoản có 35 skin, bao gồm Dark Cosmic Jhin', 450000, 0, N'acc19.webp', GETDATE()),
    (N'Valorant', N'ShadowSnipe', 55, N'Gold II', N'Tài khoản có skin súng Ion Sheriff', 550000, 0, N'acc20.webp', GETDATE()),
    (N'TFT', N'VoidTactician', 40, N'Gold IV', N'Tài khoản có 8 Little Legends, 2 arena skin', 300000, 0, N'acc21.webp', GETDATE()),
    (N'LOL', N'FrostQueen', 170, N'Diamond IV', N'Tài khoản có 75 skin, bao gồm Winterblessed Diana', 850000, 0, N'acc22.webp', GETDATE()),
    (N'Valorant', N'FireViper', 70, N'Platinum III', N'Tài khoản có skin súng Glitchpop Vandal', 700000, 0, N'acc23.webp', GETDATE()),
    (N'TFT', N'StarlightPro', 60, N'Master', N'Tài khoản có 15 Little Legends, 5 arena skin', 500000, 0, N'acc24.webp', GETDATE()),
    (N'LOL', N'SkyHunter', 145, N'Gold III', N'Tài khoản có 45 skin, bao gồm Battle Queen Katarina', 600000, 0, N'acc25.webp', GETDATE()),
    (N'Valorant', N'ThunderShot', 50, N'Silver II', N'Tài khoản có skin súng Reaver Phantom', 450000, 0, N'acc26.webp', GETDATE()),
    (N'TFT', N'NebulaMaster', 55, N'Platinum I', N'Tài khoản có 10 Little Legends, 3 arena skin', 350000, 0, N'acc27.webp', GETDATE()),
    (N'LOL', N'MoonShadow', 155, N'Platinum III', N'Tài khoản có 55 skin, bao gồm Spirit Blossom Ahri', 750000, 0, N'acc28.webp', GETDATE()),
    (N'Valorant', N'IceBlaze', 65, N'Gold I', N'Tài khoản có skin súng Prime Guardian', 600000, 0, N'acc29.webp', GETDATE()),
    (N'TFT', N'SolarTactician', 50, N'Diamond II', N'Tài khoản có 12 Little Legends, 4 arena skin', 400000, 0, N'acc30.webp', GETDATE()),
    (N'LOL', N'StormRider', 180, N'Diamond II', N'Tài khoản có 80 skin, bao gồm PROJECT Akali', 900000, 0, N'acc31.webp', GETDATE()),
    (N'Valorant', N'GhostSniper', 60, N'Platinum IV', N'Tài khoản có skin súng Oni Vandal', 650000, 0, N'acc32.webp', GETDATE()),
    (N'TFT', N'LunarMaster', 70, N'Challenger', N'Tài khoản có 20 Little Legends, bao gồm Choncc Huyền Thoại', 700000, 0, N'acc33.webp', GETDATE()),
    (N'LOL', N'FireBlaze', 140, N'Gold IV', N'Tài khoản có 40 skin, bao gồm High Noon Lucian', 550000, 0, N'acc34.webp', GETDATE()),
    (N'Valorant', N'ShadowPhantom', 55, N'Silver III', N'Tài khoản có skin súng Ion Operator', 500000, 0, N'acc35.webp', GETDATE()),
    (N'TFT', N'CosmicPro', 45, N'Platinum II', N'Tài khoản có 10 Little Legends, 3 arena skin', 350000, 0, N'acc36.webp', GETDATE()),
    (N'LOL', N'IceStorm', 160, N'Platinum I', N'Tài khoản có 60 skin, bao gồm Star Guardian Lux', 700000, 0, N'acc37.webp', GETDATE()),
    (N'Valorant', N'NeonSniper', 75, N'Diamond III', N'Tài khoản có skin súng Prime Phantom', 800000, 0, N'acc38.webp', GETDATE()),
    (N'TFT', N'EclipseTactician', 65, N'Grandmaster', N'Tài khoản có 18 Little Legends, 5 arena skin', 650000, 0, N'acc39.webp', GETDATE()),
    (N'LOL', N'DarkSlayer', 130, N'Silver IV', N'Tài khoản có 35 skin, bao gồm Dark Cosmic Yasuo', 450000, 0, N'acc40.webp', GETDATE()),
    (N'Valorant', N'FireShot', 60, N'Gold II', N'Tài khoản có skin súng Glitchpop Sheriff', 550000, 0, N'acc41.webp', GETDATE()),
    (N'TFT', N'VoidMaster', 50, N'Platinum III', N'Tài khoản có 10 Little Legends, 2 arena skin', 300000, 0, N'acc42.webp', GETDATE()),
    (N'LOL', N'FrostKnight', 170, N'Diamond I', N'Tài khoản có 75 skin, bao gồm Winterblessed Zoe', 850000, 0, N'acc43.webp', GETDATE()),
    (N'Valorant', N'ThunderViper', 70, N'Platinum II', N'Tài khoản có skin súng Reaver Vandal', 700000, 0, N'acc44.webp', GETDATE()),
    (N'TFT', N'StarlightMaster', 60, N'Master', N'Tài khoản có 15 Little Legends, 4 arena skin', 500000, 0, N'acc45.webp', GETDATE()),
    (N'LOL', N'SkyBlaze', 145, N'Gold I', N'Tài khoản có 45 skin, bao gồm Battle Queen Jinx', 600000, 0, N'acc46.webp', GETDATE()),
    (N'Valorant', N'IceSniper', 50, N'Silver I', N'Tài khoản có skin súng Prime Operator', 450000, 0, N'acc47.webp', GETDATE()),
    (N'TFT', N'NebulaPro', 55, N'Platinum IV', N'Tài khoản có 12 Little Legends, 3 arena skin', 350000, 0, N'acc48.webp', GETDATE()),
    (N'LOL', N'MoonHunter', 155, N'Platinum II', N'Tài khoản có 55 skin, bao gồm Spirit Blossom Yone', 750000, 0, N'acc49.webp', GETDATE()),
    (N'Valorant', N'StormPhantom', 65, N'Gold III', N'Tài khoản có skin súng Ion Vandal', 600000, 0, N'acc50.webp', GETDATE());
GO

