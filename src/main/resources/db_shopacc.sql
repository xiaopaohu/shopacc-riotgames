-- ============================================
-- TẠO DATABASE
-- ============================================
CREATE DATABASE ShopAcc;
GO

USE ShopAcc;
GO

-- ============================================
-- BẢNG STAFFS (nhân viên)
-- ============================================
CREATE TABLE STAFFS (
                        staff_id INT IDENTITY PRIMARY KEY,
                        full_name NVARCHAR(100) NOT NULL,
                        email NVARCHAR(100) UNIQUE NOT NULL,
                        phone_number NVARCHAR(15),
                        password_hash NVARCHAR(255) NOT NULL,
                        role NVARCHAR(50) NOT NULL,
                        image NVARCHAR(255),
                        created_at DATETIME DEFAULT GETDATE(),
                        updated_at DATETIME DEFAULT GETDATE(),
                        deleted_at DATETIME NULL
);
CREATE INDEX idx_staffs_deleted ON STAFFS(deleted_at);
GO

-- ============================================
-- BẢNG CUSTOMERS (khách hàng)
-- ============================================
CREATE TABLE CUSTOMERS (
                           customer_id INT IDENTITY PRIMARY KEY,
                           full_name NVARCHAR(100) NOT NULL,
                           email NVARCHAR(100) UNIQUE NOT NULL,
                           phone_number NVARCHAR(15),
                           password_hash NVARCHAR(255) NOT NULL,
                           balance DECIMAL(18,2) NOT NULL DEFAULT 0,
                           image NVARCHAR(255),
                           created_at DATETIME DEFAULT GETDATE(),
                           updated_at DATETIME DEFAULT GETDATE(),
                           deleted_at DATETIME NULL
);
CREATE INDEX idx_customers_deleted ON CUSTOMERS(deleted_at);
GO

-- ============================================
-- BẢNG ACCOUNTS (tài khoản game)
-- ============================================
CREATE TABLE ACCOUNTS (
                          account_id INT IDENTITY PRIMARY KEY,
                          game_type NVARCHAR(50) NOT NULL,
                          account_name NVARCHAR(100) NOT NULL,
                          username NVARCHAR(100) NOT NULL,
                          password NVARCHAR(255) NOT NULL,
                          level INT,
                          rank NVARCHAR(50),
                          description NVARCHAR(500),
                          price DECIMAL(18,2) NOT NULL,
                          status NVARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',  -- AVAILABLE / SOLD / HIDDEN / DELIVERED
                          image NVARCHAR(255),
                          listed_at DATETIME DEFAULT GETDATE(),
                          staff_id INT NULL,
                          deleted_at DATETIME NULL,
                          CONSTRAINT fk_accounts_staff FOREIGN KEY (staff_id) REFERENCES STAFFS(staff_id) ON DELETE NO ACTION
);
CREATE INDEX idx_accounts_status ON ACCOUNTS(status);
CREATE INDEX idx_accounts_deleted ON ACCOUNTS(deleted_at);
CREATE INDEX idx_accounts_game_type ON ACCOUNTS(game_type);
GO

-- ============================================
-- BẢNG CART (giỏ hàng)
-- ============================================
CREATE TABLE CART (
                      cart_id INT IDENTITY PRIMARY KEY,
                      customer_id INT NOT NULL,
                      account_id INT NOT NULL,
                      added_at DATETIME DEFAULT GETDATE(),
                      CONSTRAINT fk_cart_customer FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(customer_id) ON DELETE NO ACTION,
                      CONSTRAINT fk_cart_account FOREIGN KEY (account_id) REFERENCES ACCOUNTS(account_id) ON DELETE NO ACTION,
                      CONSTRAINT uq_cart_customer_account UNIQUE (customer_id, account_id)
);
CREATE INDEX idx_cart_customer ON CART(customer_id);
GO

-- ============================================
-- BẢNG VOUCHERS (Mã giảm giá)
-- ============================================
CREATE TABLE VOUCHERS (
                          voucher_id INT IDENTITY PRIMARY KEY,
                          code NVARCHAR(50) UNIQUE NOT NULL,
                          discount_type NVARCHAR(20) NOT NULL CHECK (discount_type IN ('PERCENT', 'FIXED')),
                          discount_value DECIMAL(18,2) NOT NULL,
                          min_order_amount DECIMAL(18,2) DEFAULT 0,
                          max_discount_amount DECIMAL(18,2) NULL,
                          usage_limit INT NULL,
                          used_count INT DEFAULT 0,
                          valid_from DATETIME NOT NULL,
                          valid_to DATETIME NOT NULL,
                          is_active BIT DEFAULT 1,
                          description NVARCHAR(255),
                          created_at DATETIME DEFAULT GETDATE(),
                          updated_at DATETIME DEFAULT GETDATE(),
                          deleted_at DATETIME NULL
);
CREATE INDEX idx_vouchers_code ON VOUCHERS(code);
CREATE INDEX idx_vouchers_active ON VOUCHERS(is_active, valid_from, valid_to);
CREATE INDEX idx_vouchers_deleted ON VOUCHERS(deleted_at);
GO

-- ============================================
-- BẢNG ORDERS (đơn hàng)
-- ============================================
CREATE TABLE ORDERS (
                        order_id INT IDENTITY PRIMARY KEY,
                        customer_id INT NOT NULL,
                        staff_id INT NULL,
                        total_amount DECIMAL(18,2) NOT NULL,
                        discount_amount DECIMAL(18,2) DEFAULT 0,
                        final_amount DECIMAL(18,2) NOT NULL,
                        order_date DATETIME DEFAULT GETDATE(),
                        status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING / COMPLETED / FAILED / CANCELLED
                        deleted_at DATETIME NULL,
                        CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(customer_id) ON DELETE NO ACTION,
                        CONSTRAINT fk_orders_staff FOREIGN KEY (staff_id) REFERENCES STAFFS(staff_id) ON DELETE NO ACTION
);
CREATE INDEX idx_orders_status ON ORDERS(status);
CREATE INDEX idx_orders_customer ON ORDERS(customer_id);
CREATE INDEX idx_orders_deleted ON ORDERS(deleted_at);
CREATE INDEX idx_orders_date ON ORDERS(order_date);
GO

-- ============================================
-- BẢNG ORDER_DETAILS (chi tiết đơn hàng)
-- ============================================
CREATE TABLE ORDER_DETAILS (
                               order_detail_id INT IDENTITY PRIMARY KEY,
                               order_id INT NOT NULL,
                               account_id INT NOT NULL,
                               price DECIMAL(18,2) NOT NULL,
                               CONSTRAINT fk_orderdetails_order FOREIGN KEY (order_id) REFERENCES ORDERS(order_id) ON DELETE CASCADE,
                               CONSTRAINT fk_orderdetails_account FOREIGN KEY (account_id) REFERENCES ACCOUNTS(account_id) ON DELETE NO ACTION
);
CREATE INDEX idx_orderdetails_order ON ORDER_DETAILS(order_id);
CREATE INDEX idx_orderdetails_account ON ORDER_DETAILS(account_id);
GO

-- ============================================
-- BẢNG ORDER_VOUCHERS (Voucher đã dùng)
-- ============================================
CREATE TABLE ORDER_VOUCHERS (
                                order_voucher_id INT IDENTITY PRIMARY KEY,
                                order_id INT NOT NULL,
                                voucher_id INT NOT NULL,
                                discount_amount DECIMAL(18,2) NOT NULL,
                                applied_at DATETIME DEFAULT GETDATE(),
                                CONSTRAINT fk_ordervouchers_order FOREIGN KEY (order_id) REFERENCES ORDERS(order_id) ON DELETE CASCADE,
                                CONSTRAINT fk_ordervouchers_voucher FOREIGN KEY (voucher_id) REFERENCES VOUCHERS(voucher_id) ON DELETE NO ACTION
);
CREATE INDEX idx_ordervouchers_order ON ORDER_VOUCHERS(order_id);
CREATE INDEX idx_ordervouchers_voucher ON ORDER_VOUCHERS(voucher_id);
GO

-- ============================================
-- BẢNG PAYMENT_METHODS (phương thức thanh toán)
-- ============================================
CREATE TABLE PAYMENT_METHODS (
                                 method_id INT IDENTITY PRIMARY KEY,
                                 method_name NVARCHAR(50) NOT NULL,
                                 description NVARCHAR(255),
                                 is_active BIT DEFAULT 1
);
GO

-- ============================================
-- BẢNG TRANSACTIONS (nạp tiền vào ví)
-- ============================================
CREATE TABLE TRANSACTIONS (
                              transaction_id INT IDENTITY PRIMARY KEY,
                              customer_id INT NOT NULL,
                              amount DECIMAL(18,2) NOT NULL,
                              transaction_date DATETIME NOT NULL DEFAULT GETDATE(),
                              note NVARCHAR(255),
                              method_id INT NULL,
                              status NVARCHAR(20) NOT NULL DEFAULT 'COMPLETED',  -- PENDING / COMPLETED / FAILED
                              deleted_at DATETIME NULL,
                              CONSTRAINT fk_transactions_customer FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(customer_id) ON DELETE NO ACTION,
                              CONSTRAINT fk_transactions_method FOREIGN KEY (method_id) REFERENCES PAYMENT_METHODS(method_id) ON DELETE NO ACTION
);
CREATE INDEX idx_transactions_customer ON TRANSACTIONS(customer_id);
CREATE INDEX idx_transactions_status ON TRANSACTIONS(status);
CREATE INDEX idx_transactions_deleted ON TRANSACTIONS(deleted_at);
CREATE INDEX idx_transactions_date ON TRANSACTIONS(transaction_date);
GO

-- ============================================
-- BẢNG PAYMENTS (thanh toán đơn hàng)
-- ============================================
CREATE TABLE PAYMENTS (
                          payment_id INT IDENTITY PRIMARY KEY,
                          order_id INT NOT NULL,
                          amount DECIMAL(18,2) NOT NULL,
                          payment_date DATETIME DEFAULT GETDATE(),
                          deleted_at DATETIME NULL,
                          CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES ORDERS(order_id) ON DELETE CASCADE
);
CREATE INDEX idx_payments_order ON PAYMENTS(order_id);
CREATE INDEX idx_payments_deleted ON PAYMENTS(deleted_at);
CREATE INDEX idx_payments_date ON PAYMENTS(payment_date);
GO

-- ============================================
-- BẢNG NOTIFICATIONS (Thông báo)
-- ============================================
CREATE TABLE NOTIFICATIONS (
                               notification_id INT IDENTITY PRIMARY KEY,
                               customer_id INT NULL,
                               staff_id INT NULL,
                               title NVARCHAR(200) NOT NULL,
                               message NVARCHAR(500) NOT NULL,
                               type NVARCHAR(50) NOT NULL, -- ORDER / PAYMENT / PROMOTION / SYSTEM
                               is_read BIT DEFAULT 0,
                               created_at DATETIME DEFAULT GETDATE(),
                               deleted_at DATETIME NULL,
                               CONSTRAINT fk_notifications_customer FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(customer_id) ON DELETE CASCADE,
                               CONSTRAINT fk_notifications_staff FOREIGN KEY (staff_id) REFERENCES STAFFS(staff_id) ON DELETE CASCADE
);
CREATE INDEX idx_notifications_customer ON NOTIFICATIONS(customer_id, is_read);
CREATE INDEX idx_notifications_staff ON NOTIFICATIONS(staff_id, is_read);
CREATE INDEX idx_notifications_deleted ON NOTIFICATIONS(deleted_at);
GO

-- ============================================
-- DỮ LIỆU MẪU - PAYMENT_METHODS
-- ============================================
INSERT INTO PAYMENT_METHODS (method_name, description, is_active)
VALUES
    ('BANK_TRANSFER', N'Chuyển khoản ngân hàng', 1),
    ('MOMO', N'Thanh toán qua ví MoMo', 1),
    ('ZALOPAY', N'Thanh toán qua ZaloPay', 1),
    ('CREDIT_CARD', N'Thanh toán qua thẻ tín dụng', 1),
    ('ADMIN_ADJUST', N'Điều chỉnh thủ công bởi quản trị viên', 1);
GO

-- ============================================
-- DỮ LIỆU MẪU - VOUCHERS
-- ============================================
INSERT INTO VOUCHERS (code, discount_type, discount_value, min_order_amount, max_discount_amount, usage_limit, valid_from, valid_to, description, is_active)
VALUES
    ('NEWUSER10', 'PERCENT', 10, 0, 50000, 100, GETDATE(), DATEADD(MONTH, 1, GETDATE()), N'Giảm 10% cho khách hàng mới (tối đa 50k)', 1),
    ('SALE50K', 'FIXED', 50000, 200000, NULL, 50, GETDATE(), DATEADD(DAY, 7, GETDATE()), N'Giảm 50k cho đơn hàng từ 200k', 1),
    ('VIP20', 'PERCENT', 20, 500000, 200000, 20, GETDATE(), DATEADD(MONTH, 3, GETDATE()), N'Giảm 20% cho VIP (tối đa 200k)', 1),
    ('WELCOME100K', 'FIXED', 100000, 1000000, NULL, 30, GETDATE(), DATEADD(MONTH, 1, GETDATE()), N'Giảm 100k cho đơn hàng từ 1 triệu', 1);
GO

-- ============================================
-- TRIGGER: CỘNG TIỀN VÀO VÍ KHI NẠP
-- ============================================
CREATE TRIGGER trg_AutoTopup
    ON TRANSACTIONS
    AFTER INSERT
    AS
BEGIN
    SET NOCOUNT ON;

    UPDATE c
    SET c.balance = c.balance + i.amount,
        c.updated_at = GETDATE()
    FROM CUSTOMERS c
             JOIN inserted i ON c.customer_id = i.customer_id
    WHERE c.deleted_at IS NULL
      AND i.deleted_at IS NULL
      AND i.status = 'COMPLETED';
END;
GO

-- ============================================
-- TRIGGER: GỬI THÔNG BÁO KHI NẠP TIỀN THÀNH CÔNG
-- ============================================
CREATE TRIGGER trg_NotifyTransaction
    ON TRANSACTIONS
    AFTER INSERT
    AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO NOTIFICATIONS (customer_id, title, message, type)
    SELECT
        i.customer_id,
        N'Nạp tiền thành công',
        N'Bạn đã nạp ' + FORMAT(i.amount, 'N0') + N'đ vào tài khoản. Số dư hiện tại: ' +
        FORMAT((SELECT balance FROM CUSTOMERS WHERE customer_id = i.customer_id), 'N0') + N'đ',
        'PAYMENT'
    FROM inserted i
    WHERE i.deleted_at IS NULL AND i.status = 'COMPLETED';
END;
GO

-- ============================================
-- TRIGGER: XỬ LÝ ĐƠN HÀNG TỰ ĐỘNG
-- ============================================
CREATE TRIGGER trg_AutoOrderPayment
    ON ORDERS
    AFTER INSERT
    AS
BEGIN
    SET NOCOUNT ON;

    -- Cập nhật trạng thái đơn hàng dựa trên số dư
    UPDATE o
    SET o.status = CASE
                       WHEN c.balance >= o.final_amount THEN 'COMPLETED'
                       ELSE 'FAILED'
        END
    FROM ORDERS o
             JOIN inserted i ON o.order_id = i.order_id
             JOIN CUSTOMERS c ON o.customer_id = c.customer_id
    WHERE o.deleted_at IS NULL AND c.deleted_at IS NULL;

    -- Tạo payment cho đơn hàng thành công
    INSERT INTO PAYMENTS (order_id, amount)
    SELECT i.order_id, i.final_amount
    FROM inserted i
             JOIN CUSTOMERS c ON i.customer_id = c.customer_id
    WHERE c.balance >= i.final_amount
      AND c.deleted_at IS NULL
      AND i.deleted_at IS NULL;

    -- Trừ tiền trong ví khách hàng
    UPDATE c
    SET c.balance = c.balance - i.final_amount,
        c.updated_at = GETDATE()
    FROM CUSTOMERS c
             JOIN inserted i ON c.customer_id = i.customer_id
    WHERE c.balance >= i.final_amount
      AND c.deleted_at IS NULL
      AND i.deleted_at IS NULL;

    -- Cập nhật trạng thái account sang SOLD
    UPDATE a
    SET a.status = 'SOLD'
    FROM ACCOUNTS a
             JOIN ORDER_DETAILS od ON a.account_id = od.account_id
             JOIN inserted i ON od.order_id = i.order_id
             JOIN ORDERS o ON i.order_id = o.order_id
    WHERE a.deleted_at IS NULL
      AND i.deleted_at IS NULL
      AND o.status = 'COMPLETED';
END;
GO

-- ============================================
-- TRIGGER: GỬI THÔNG BÁO KHI ĐẶT HÀNG
-- ============================================
CREATE TRIGGER trg_NotifyOrderStatus
    ON ORDERS
    AFTER INSERT, UPDATE
    AS
BEGIN
    SET NOCOUNT ON;

    -- Thông báo đơn hàng thành công
    INSERT INTO NOTIFICATIONS (customer_id, title, message, type)
    SELECT
        i.customer_id,
        N'Đơn hàng thành công',
        N'Đơn hàng #' + CAST(i.order_id AS NVARCHAR) + N' đã được xử lý thành công. ' +
        N'Tổng thanh toán: ' + FORMAT(i.final_amount, 'N0') + N'đ. ' +
        N'Vui lòng kiểm tra thông tin tài khoản trong chi tiết đơn hàng.',
        'ORDER'
    FROM inserted i
             LEFT JOIN deleted d ON i.order_id = d.order_id
    WHERE i.status = 'COMPLETED'
      AND (d.order_id IS NULL OR d.status != 'COMPLETED');

    -- Thông báo đơn hàng thất bại
    INSERT INTO NOTIFICATIONS (customer_id, title, message, type)
    SELECT
        i.customer_id,
        N'Đơn hàng thất bại',
        N'Đơn hàng #' + CAST(i.order_id AS NVARCHAR) + N' thất bại do số dư không đủ. ' +
        N'Số tiền cần: ' + FORMAT(i.final_amount, 'N0') + N'đ. Vui lòng nạp thêm tiền.',
        'ORDER'
    FROM inserted i
             LEFT JOIN deleted d ON i.order_id = d.order_id
    WHERE i.status = 'FAILED'
      AND (d.order_id IS NULL OR d.status != 'FAILED');
END;
GO

-- ============================================
-- TRIGGER: CẬP NHẬT used_count CỦA VOUCHER
-- ============================================
CREATE TRIGGER trg_UpdateVoucherUsage
    ON ORDER_VOUCHERS
    AFTER INSERT
    AS
BEGIN
    SET NOCOUNT ON;

    UPDATE v
    SET v.used_count = v.used_count + 1,
        v.updated_at = GETDATE()
    FROM VOUCHERS v
             JOIN inserted i ON v.voucher_id = i.voucher_id;
END;
GO

-- ============================================
-- TRIGGER: XÓA CART SAU KHI ĐẶT HÀNG THÀNH CÔNG
-- ============================================
CREATE TRIGGER trg_ClearCartAfterOrder
    ON ORDER_DETAILS
    AFTER INSERT
    AS
BEGIN
    SET NOCOUNT ON;

    DELETE c
    FROM CART c
             JOIN inserted i ON c.account_id = i.account_id
             JOIN ORDERS o ON i.order_id = o.order_id
    WHERE c.customer_id = o.customer_id;
END;
GO

-- ============================================
-- STORED PROCEDURE: ÁP DỤNG VOUCHER
-- ============================================
CREATE OR ALTER PROCEDURE sp_ApplyVoucher
    @order_id INT,
    @voucher_code NVARCHAR(50),
    @result NVARCHAR(255) OUTPUT,
    @discount_amount DECIMAL(18,2) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        DECLARE @voucher_id INT;
        DECLARE @discount_type NVARCHAR(20);
        DECLARE @discount_value DECIMAL(18,2);
        DECLARE @min_order_amount DECIMAL(18,2);
        DECLARE @max_discount_amount DECIMAL(18,2);
        DECLARE @usage_limit INT;
        DECLARE @used_count INT;
        DECLARE @order_amount DECIMAL(18,2);
        DECLARE @valid_from DATETIME;
        DECLARE @valid_to DATETIME;
        DECLARE @is_active BIT;

        -- Lấy thông tin voucher
        SELECT
            @voucher_id = voucher_id,
            @discount_type = discount_type,
            @discount_value = discount_value,
            @min_order_amount = min_order_amount,
            @max_discount_amount = max_discount_amount,
            @usage_limit = usage_limit,
            @used_count = used_count,
            @valid_from = valid_from,
            @valid_to = valid_to,
            @is_active = is_active
        FROM VOUCHERS
        WHERE code = @voucher_code AND deleted_at IS NULL;

        -- Kiểm tra voucher tồn tại
        IF @voucher_id IS NULL
            BEGIN
                SET @result = N'Mã voucher không tồn tại';
                SET @discount_amount = 0;
                RETURN;
            END

        -- Kiểm tra voucher còn hiệu lực
        IF @is_active = 0 OR GETDATE() < @valid_from OR GETDATE() > @valid_to
            BEGIN
                SET @result = N'Mã voucher không còn hiệu lực';
                SET @discount_amount = 0;
                RETURN;
            END

        -- Kiểm tra số lần sử dụng
        IF @usage_limit IS NOT NULL AND @used_count >= @usage_limit
            BEGIN
                SET @result = N'Mã voucher đã hết lượt sử dụng';
                SET @discount_amount = 0;
                RETURN;
            END

        -- Lấy giá trị đơn hàng
        SELECT @order_amount = total_amount FROM ORDERS WHERE order_id = @order_id;

        -- Kiểm tra giá trị đơn hàng tối thiểu
        IF @order_amount < @min_order_amount
            BEGIN
                SET @result = N'Đơn hàng chưa đủ giá trị tối thiểu: ' + FORMAT(@min_order_amount, 'N0') + N'đ';
                SET @discount_amount = 0;
                RETURN;
            END

        -- Tính discount
        IF @discount_type = 'PERCENT'
            BEGIN
                SET @discount_amount = @order_amount * (@discount_value / 100);
                IF @max_discount_amount IS NOT NULL AND @discount_amount > @max_discount_amount
                    SET @discount_amount = @max_discount_amount;
            END
        ELSE
            BEGIN
                SET @discount_amount = @discount_value;
            END

        -- Không cho discount vượt quá giá trị đơn hàng
        IF @discount_amount > @order_amount
            SET @discount_amount = @order_amount;

        -- Cập nhật đơn hàng
        UPDATE ORDERS
        SET discount_amount = @discount_amount,
            final_amount = total_amount - @discount_amount
        WHERE order_id = @order_id;

        -- Lưu lại voucher đã dùng
        INSERT INTO ORDER_VOUCHERS (order_id, voucher_id, discount_amount)
        VALUES (@order_id, @voucher_id, @discount_amount);

        SET @result = N'Áp dụng voucher thành công. Giảm ' + FORMAT(@discount_amount, 'N0') + N'đ';
    END TRY
    BEGIN CATCH
        SET @result = N'Lỗi: ' + ERROR_MESSAGE();
        SET @discount_amount = 0;
    END CATCH
END;
GO

-- ============================================
-- STORED PROCEDURE: ĐÁNH DẤU GIAO ACC
-- ============================================
CREATE OR ALTER PROCEDURE sp_MarkAccountDelivered
@account_id INT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @rows_affected INT;

    UPDATE ACCOUNTS
    SET status = 'DELIVERED'
    WHERE account_id = @account_id
      AND status = 'SOLD'
      AND deleted_at IS NULL;

    SET @rows_affected = @@ROWCOUNT;

    IF @rows_affected > 0
        PRINT N'✅ Đã đánh dấu account #' + CAST(@account_id AS NVARCHAR) + N' là DELIVERED';
    ELSE
        PRINT N'❌ Không thể đánh dấu account #' + CAST(@account_id AS NVARCHAR);

    RETURN @rows_affected;
END;
GO

-- ============================================
-- STORED PROCEDURE: DOANH THU THEO THÁNG
-- ============================================
CREATE OR ALTER PROCEDURE sp_GetRevenueByMonth
@year INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        MONTH(payment_date) AS month,
        YEAR(payment_date) AS year,
        COUNT(*) AS total_payments,
        SUM(amount) AS total_revenue
    FROM PAYMENTS
    WHERE YEAR(payment_date) = @year AND deleted_at IS NULL
    GROUP BY YEAR(payment_date), MONTH(payment_date)
    ORDER BY month;
END;
GO

-- ============================================
-- STORED PROCEDURE: KHÁCH HÀNG MUA NHIỀU NHẤT
-- ============================================
CREATE OR ALTER PROCEDURE sp_GetTopCustomersByPurchase
@limit INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP (@limit)
        c.customer_id,
        c.full_name,
        c.email,
        c.phone_number,
        c.balance,
        COUNT(o.order_id) AS total_orders,
        SUM(o.final_amount) AS total_spent,
        AVG(o.final_amount) AS avg_order_value,
        MAX(o.order_date) AS last_order_date
    FROM CUSTOMERS c
             JOIN ORDERS o ON c.customer_id = o.customer_id
    WHERE c.deleted_at IS NULL
      AND o.deleted_at IS NULL
      AND o.status = 'COMPLETED'
    GROUP BY c.customer_id, c.full_name, c.email, c.phone_number, c.balance
    ORDER BY total_spent DESC;
END;
GO

-- ============================================
-- STORED PROCEDURE: TOP ACCOUNT BÁN CHẠY
-- ============================================
CREATE OR ALTER PROCEDURE sp_GetTopSellingAccounts
    @limit INT,
    @game_type NVARCHAR(50) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP (@limit)
        a.account_id,
        a.account_name,
        a.game_type,
        a.level,
        a.rank,
        a.price,
        a.status,
        a.image,
        COUNT(od.order_detail_id) AS total_sold,
        SUM(od.price) AS total_revenue
    FROM ACCOUNTS a
             JOIN ORDER_DETAILS od ON a.account_id = od.account_id
             JOIN ORDERS o ON od.order_id = o.order_id
    WHERE a.deleted_at IS NULL
      AND o.deleted_at IS NULL
      AND o.status = 'COMPLETED'
      AND (@game_type IS NULL OR a.game_type = @game_type)
    GROUP BY a.account_id, a.account_name, a.game_type, a.level, a.rank, a.price, a.status, a.image
    ORDER BY total_sold DESC;
END;
GO

-- ============================================
-- STORED PROCEDURE: THỐNG KÊ DOANH THU CHI TIẾT
-- ============================================
CREATE OR ALTER PROCEDURE sp_GetDetailedRevenue
    @start_date DATETIME,
    @end_date DATETIME
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        CAST(o.order_date AS DATE) AS order_date,
        COUNT(DISTINCT o.order_id) AS total_orders,
        COUNT(DISTINCT o.customer_id) AS unique_customers,
        SUM(o.total_amount) AS gross_revenue,
        SUM(ISNULL(o.discount_amount, 0)) AS total_discount,
        SUM(o.final_amount) AS net_revenue,
        AVG(o.final_amount) AS avg_order_value
    FROM ORDERS o
    WHERE o.order_date BETWEEN @start_date AND @end_date
      AND o.deleted_at IS NULL
      AND o.status = 'COMPLETED'
    GROUP BY CAST(o.order_date AS DATE)
    ORDER BY order_date DESC;
END;
GO

-- ============================================
-- STORED PROCEDURE: TẠO ĐƠN HÀNG
-- ============================================
CREATE OR ALTER PROCEDURE sp_CreateOrder
    @customer_id INT,
    @account_ids NVARCHAR(MAX), -- Danh sách account_id cách nhau bởi dấu phẩy: "1,2,3"
    @voucher_code NVARCHAR(50) = NULL,
    @order_id INT OUTPUT,
    @result NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRANSACTION;
    BEGIN TRY
        DECLARE @total_amount DECIMAL(18,2) = 0;
        DECLARE @discount_amount DECIMAL(18,2) = 0;
        DECLARE @final_amount DECIMAL(18,2) = 0;

        -- Tính tổng tiền
        SELECT @total_amount = SUM(price)
        FROM ACCOUNTS
        WHERE account_id IN (SELECT value FROM STRING_SPLIT(@account_ids, ','))
          AND status = 'AVAILABLE'
          AND deleted_at IS NULL;

        IF @total_amount IS NULL OR @total_amount = 0
            BEGIN
                SET @result = N'Không có account hợp lệ để đặt hàng';
                ROLLBACK TRANSACTION;
                RETURN;
            END

        -- Tạo đơn hàng
        INSERT INTO ORDERS (customer_id, total_amount, final_amount, status)
        VALUES (@customer_id, @total_amount, @total_amount, 'PENDING');

        SET @order_id = SCOPE_IDENTITY();

        -- Thêm chi tiết đơn hàng
        INSERT INTO ORDER_DETAILS (order_id, account_id, price)
        SELECT @order_id, account_id, price
        FROM ACCOUNTS
        WHERE account_id IN (SELECT value FROM STRING_SPLIT(@account_ids, ','))
          AND status = 'AVAILABLE'
          AND deleted_at IS NULL;

        -- Áp dụng voucher nếu có
        IF @voucher_code IS NOT NULL AND @voucher_code != ''
            BEGIN
                DECLARE @voucher_result NVARCHAR(255);
                EXEC sp_ApplyVoucher @order_id, @voucher_code, @voucher_result OUTPUT, @discount_amount OUTPUT;
            END

        SET @result = N'Tạo đơn hàng thành công. Order ID: ' + CAST(@order_id AS NVARCHAR);
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        SET @result = N'Lỗi: ' + ERROR_MESSAGE();
        SET @order_id = NULL;
    END CATCH
END;
GO

-- ============================================
-- STORED PROCEDURE: HỦY ĐƠN HÀNG
-- ============================================
CREATE OR ALTER PROCEDURE sp_CancelOrder
    @order_id INT,
    @cancelled_by INT = NULL, -- staff_id nếu admin hủy
    @result NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        DECLARE @order_status NVARCHAR(20);
        DECLARE @customer_id INT;
        DECLARE @final_amount DECIMAL(18,2);

        -- Lấy thông tin đơn hàng
        SELECT
            @order_status = status,
            @customer_id = customer_id,
            @final_amount = final_amount
        FROM ORDERS
        WHERE order_id = @order_id AND deleted_at IS NULL;

        -- Kiểm tra đơn hàng tồn tại
        IF @order_status IS NULL
            BEGIN
                SET @result = N'Đơn hàng không tồn tại';
                RETURN;
            END

        -- Chỉ hủy được đơn PENDING
        IF @order_status != 'PENDING'
            BEGIN
                SET @result = N'Chỉ có thể hủy đơn hàng đang chờ xử lý';
                RETURN;
            END

        -- Cập nhật trạng thái đơn hàng
        UPDATE ORDERS
        SET status = 'CANCELLED',
            staff_id = @cancelled_by
        WHERE order_id = @order_id;

        -- Gửi thông báo
        INSERT INTO NOTIFICATIONS (customer_id, title, message, type)
        VALUES (
                   @customer_id,
                   N'Đơn hàng đã hủy',
                   N'Đơn hàng #' + CAST(@order_id AS NVARCHAR) + N' đã được hủy thành công.',
                   'ORDER'
               );

        SET @result = N'Hủy đơn hàng thành công';
    END TRY
    BEGIN CATCH
        SET @result = N'Lỗi: ' + ERROR_MESSAGE();
    END CATCH
END;
GO

-- ============================================
-- FUNCTION: TỔNG DOANH THU
-- ============================================
CREATE OR ALTER FUNCTION fn_GetTotalRevenue()
    RETURNS DECIMAL(18,2)
AS
BEGIN
    DECLARE @total DECIMAL(18,2);
    SELECT @total = SUM(amount) FROM PAYMENTS WHERE deleted_at IS NULL;
    RETURN ISNULL(@total, 0);
END;
GO

-- ============================================
-- FUNCTION: TÍNH ĐIỂM UY TÍN KHÁCH HÀNG
-- ============================================
CREATE OR ALTER FUNCTION fn_GetCustomerScore(@customer_id INT)
    RETURNS INT
AS
BEGIN
    DECLARE @score INT = 0;
    DECLARE @total_spent DECIMAL(18,2);
    DECLARE @total_orders INT;

    SELECT
        @total_spent = ISNULL(SUM(final_amount), 0),
        @total_orders = COUNT(*)
    FROM ORDERS
    WHERE customer_id = @customer_id
      AND status = 'COMPLETED'
      AND deleted_at IS NULL;

    -- Tính điểm: 1 điểm cho mỗi 100k chi tiêu + 10 điểm cho mỗi đơn hàng
    SET @score = FLOOR(@total_spent / 100000) + (@total_orders * 10);

    RETURN @score;
END;
GO

-- ============================================
-- FUNCTION: KIỂM TRA VOUCHER HỢP LỆ
-- ============================================
CREATE OR ALTER FUNCTION fn_IsVoucherValid(@voucher_code NVARCHAR(50))
    RETURNS BIT
AS
BEGIN
    DECLARE @is_valid BIT = 0;

    IF EXISTS (
        SELECT 1
        FROM VOUCHERS
        WHERE code = @voucher_code
          AND is_active = 1
          AND GETDATE() BETWEEN valid_from AND valid_to
          AND (usage_limit IS NULL OR used_count < usage_limit)
          AND deleted_at IS NULL
    )
        SET @is_valid = 1;

    RETURN @is_valid;
END;
GO

-- ============================================
-- VIEW: DOANH THU THEO GAME
-- ============================================
CREATE OR ALTER VIEW vw_RevenuePerGame AS
SELECT
    a.game_type,
    COUNT(DISTINCT a.account_id) AS total_accounts,
    COUNT(od.account_id) AS total_sold,
    SUM(od.price) AS total_revenue,
    AVG(od.price) AS avg_price,
    MIN(od.price) AS min_price,
    MAX(od.price) AS max_price
FROM ACCOUNTS a
         JOIN ORDER_DETAILS od ON a.account_id = od.account_id
         JOIN ORDERS o ON od.order_id = o.order_id
WHERE a.deleted_at IS NULL
  AND o.deleted_at IS NULL
  AND o.status = 'COMPLETED'
GROUP BY a.game_type;
GO

-- ============================================
-- VIEW: DASHBOARD TỔNG HỢP HỆ THỐNG
-- ============================================
CREATE OR ALTER VIEW vw_DashboardSummary AS
SELECT
    (SELECT COUNT(*) FROM CUSTOMERS WHERE deleted_at IS NULL) AS total_customers,
    (SELECT COUNT(*) FROM STAFFS WHERE deleted_at IS NULL) AS total_staffs,
    (SELECT COUNT(*) FROM ACCOUNTS WHERE status = 'AVAILABLE' AND deleted_at IS NULL) AS available_accounts,
    (SELECT COUNT(*) FROM ACCOUNTS WHERE status = 'SOLD' AND deleted_at IS NULL) AS sold_accounts,
    (SELECT COUNT(*) FROM ACCOUNTS WHERE status = 'DELIVERED' AND deleted_at IS NULL) AS delivered_accounts,
    (SELECT COUNT(*) FROM ORDERS WHERE status = 'COMPLETED' AND deleted_at IS NULL) AS total_orders,
    (SELECT COUNT(*) FROM ORDERS WHERE status = 'PENDING' AND deleted_at IS NULL) AS pending_orders,
    (SELECT COUNT(*) FROM ORDERS WHERE status = 'FAILED' AND deleted_at IS NULL) AS failed_orders,
    (SELECT dbo.fn_GetTotalRevenue()) AS total_revenue,
    (SELECT SUM(balance) FROM CUSTOMERS WHERE deleted_at IS NULL) AS total_customer_balance;
GO

-- ============================================
-- VIEW: THỐNG KÊ ACCOUNT THEO GAME
-- ============================================
CREATE OR ALTER VIEW vw_AccountStatsByGame AS
SELECT
    a.game_type,
    COUNT(*) AS total_accounts,
    COUNT(CASE WHEN a.status = 'AVAILABLE' THEN 1 END) AS available,
    COUNT(CASE WHEN a.status = 'SOLD' THEN 1 END) AS sold,
    COUNT(CASE WHEN a.status = 'DELIVERED' THEN 1 END) AS delivered,
    COUNT(CASE WHEN a.status = 'HIDDEN' THEN 1 END) AS hidden,
    MIN(a.price) AS min_price,
    MAX(a.price) AS max_price,
    AVG(a.price) AS avg_price
FROM ACCOUNTS a
WHERE a.deleted_at IS NULL
GROUP BY a.game_type;
GO

-- ============================================
-- VIEW: KHÁCH HÀNG VIP
-- ============================================
CREATE OR ALTER VIEW vw_VIPCustomers AS
SELECT TOP 100
    c.customer_id,
    c.full_name,
    c.email,
    c.phone_number,
    c.balance,
    COUNT(o.order_id) AS total_orders,
    SUM(o.final_amount) AS total_spent,
    dbo.fn_GetCustomerScore(c.customer_id) AS loyalty_score,
    MAX(o.order_date) AS last_order_date,
    DATEDIFF(DAY, MAX(o.order_date), GETDATE()) AS days_since_last_order
FROM CUSTOMERS c
         LEFT JOIN ORDERS o ON c.customer_id = o.customer_id
    AND o.status = 'COMPLETED'
    AND o.deleted_at IS NULL
WHERE c.deleted_at IS NULL
GROUP BY c.customer_id, c.full_name, c.email, c.phone_number, c.balance
HAVING COUNT(o.order_id) > 0
ORDER BY total_spent DESC;
GO

-- ============================================
-- VIEW: THỐNG KÊ VOUCHER
-- ============================================
CREATE OR ALTER VIEW vw_VoucherStats AS
SELECT
    v.voucher_id,
    v.code,
    v.discount_type,
    v.discount_value,
    v.min_order_amount,
    v.max_discount_amount,
    v.usage_limit,
    v.used_count,
    v.valid_from,
    v.valid_to,
    v.is_active,
    v.description,
    ISNULL(SUM(ov.discount_amount), 0) AS total_discount_given,
    COUNT(ov.order_voucher_id) AS times_used,
    CASE
        WHEN v.usage_limit IS NOT NULL THEN v.usage_limit - v.used_count
        ELSE NULL
        END AS remaining_usage,
    CASE
        WHEN v.is_active = 0 THEN N'Đã tắt'
        WHEN GETDATE() < v.valid_from THEN N'Chưa đến hạn'
        WHEN GETDATE() > v.valid_to THEN N'Hết hạn'
        WHEN v.usage_limit IS NOT NULL AND v.used_count >= v.usage_limit THEN N'Hết lượt'
        ELSE N'Đang hoạt động'
        END AS status_text
FROM VOUCHERS v
         LEFT JOIN ORDER_VOUCHERS ov ON v.voucher_id = ov.voucher_id
WHERE v.deleted_at IS NULL
GROUP BY v.voucher_id, v.code, v.discount_type, v.discount_value,
         v.min_order_amount, v.max_discount_amount, v.usage_limit,
         v.used_count, v.valid_from, v.valid_to, v.is_active, v.description;
GO
