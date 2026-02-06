-- =============================================
-- 酒店管理系统数据库脚本（续）
-- =============================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE hotel_management;

-- 入住记录表
DROP TABLE IF EXISTS check_in;
CREATE TABLE check_in (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '入住ID',
    check_in_no VARCHAR(32) NOT NULL COMMENT '入住单号',
    reservation_id BIGINT COMMENT '关联预订ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    check_in_time DATETIME NOT NULL COMMENT '入住时间',
    check_out_time DATETIME COMMENT '退房时间',
    expected_check_out DATE COMMENT '预计退房日期',
    nights INT COMMENT '入住天数',
    room_price DECIMAL(10,2) COMMENT '房间单价',
    total_price DECIMAL(10,2) COMMENT '房费总计',
    deposit DECIMAL(10,2) DEFAULT 0 COMMENT '押金',
    extra_charges DECIMAL(10,2) DEFAULT 0 COMMENT '额外费用',
    discount DECIMAL(10,2) DEFAULT 0 COMMENT '折扣',
    actual_amount DECIMAL(10,2) COMMENT '实际金额',
    paid_amount DECIMAL(10,2) DEFAULT 0 COMMENT '已付金额',
    status TINYINT DEFAULT 0 COMMENT '状态：0-入住中，1-已退房，2-续住中',
    remark VARCHAR(255) COMMENT '备注',
    operator_id BIGINT COMMENT '操作员ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_check_in_no (check_in_no),
    KEY idx_customer (customer_id),
    KEY idx_room (room_id),
    KEY idx_check_in_time (check_in_time),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入住记录表';

-- 入住人员表（一个房间可能多人入住）
DROP TABLE IF EXISTS check_in_guest;
CREATE TABLE check_in_guest (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    check_in_id BIGINT NOT NULL COMMENT '入住记录ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    id_card VARCHAR(20) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '手机号',
    is_primary TINYINT DEFAULT 0 COMMENT '是否主住客：0-否，1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_check_in (check_in_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入住人员表';

-- 账单表
DROP TABLE IF EXISTS bill;
CREATE TABLE bill (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账单ID',
    bill_no VARCHAR(32) NOT NULL COMMENT '账单号',
    check_in_id BIGINT COMMENT '入住记录ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    bill_type TINYINT DEFAULT 0 COMMENT '账单类型：0-房费，1-消费，2-押金，3-退款',
    item_name VARCHAR(100) COMMENT '项目名称',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    payment_method TINYINT COMMENT '支付方式：0-现金，1-微信，2-支付宝，3-银行卡，4-挂账',
    payment_status TINYINT DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付，2-已退款',
    payment_time DATETIME COMMENT '支付时间',
    remark VARCHAR(255) COMMENT '备注',
    operator_id BIGINT COMMENT '操作员ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_bill_no (bill_no),
    KEY idx_check_in (check_in_id),
    KEY idx_customer (customer_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单表';

-- 操作日志表
DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作',
    method VARCHAR(200) COMMENT '方法名',
    params TEXT COMMENT '参数',
    ip VARCHAR(50) COMMENT 'IP地址',
    duration BIGINT COMMENT '执行时长(毫秒)',
    status TINYINT DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    error_msg TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_user (user_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 日报表
DROP TABLE IF EXISTS daily_report;
CREATE TABLE daily_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报表ID',
    report_date DATE NOT NULL COMMENT '报表日期',
    total_revenue DECIMAL(12,2) DEFAULT 0 COMMENT '总营收',
    room_revenue DECIMAL(12,2) DEFAULT 0 COMMENT '房费收入',
    consume_revenue DECIMAL(12,2) DEFAULT 0 COMMENT '消费收入',
    deposit_revenue DECIMAL(12,2) DEFAULT 0 COMMENT '押金收入',
    refund_amount DECIMAL(12,2) DEFAULT 0 COMMENT '退款金额',
    check_in_count INT DEFAULT 0 COMMENT '入住数',
    check_out_count INT DEFAULT 0 COMMENT '退房数',
    reservation_count INT DEFAULT 0 COMMENT '预订数',
    new_customer_count INT DEFAULT 0 COMMENT '新增客户数',
    occupancy_rate DECIMAL(5,2) DEFAULT 0 COMMENT '入住率',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
    UNIQUE KEY uk_report_date (report_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日报表';

-- 月报表
DROP TABLE IF EXISTS monthly_report;
CREATE TABLE monthly_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报表ID',
    report_year INT NOT NULL COMMENT '年份',
    report_month INT NOT NULL COMMENT '月份',
    total_revenue DECIMAL(14,2) DEFAULT 0 COMMENT '总营收',
    room_revenue DECIMAL(14,2) DEFAULT 0 COMMENT '房费收入',
    consume_revenue DECIMAL(14,2) DEFAULT 0 COMMENT '消费收入',
    deposit_revenue DECIMAL(14,2) DEFAULT 0 COMMENT '押金收入',
    refund_amount DECIMAL(14,2) DEFAULT 0 COMMENT '退款金额',
    check_in_count INT DEFAULT 0 COMMENT '入住总数',
    check_out_count INT DEFAULT 0 COMMENT '退房总数',
    reservation_count INT DEFAULT 0 COMMENT '预订总数',
    new_customer_count INT DEFAULT 0 COMMENT '新增客户数',
    avg_occupancy_rate DECIMAL(5,2) DEFAULT 0 COMMENT '平均入住率',
    avg_daily_revenue DECIMAL(12,2) DEFAULT 0 COMMENT '平均每日营收',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
    UNIQUE KEY uk_year_month (report_year, report_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月报表';
