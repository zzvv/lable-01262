-- =============================================
-- 酒店管理系统完整数据库脚本
-- 可直接在 Navicat 中运行
-- 创建时间: 2026-02-06
-- =============================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS hotel_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hotel_management;

-- =============================================
-- 第一部分：系统管理表
-- =============================================

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL COMMENT '角色标识',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    remark VARCHAR(255) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 菜单表
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    path VARCHAR(200) COMMENT '路由地址',
    component VARCHAR(255) COMMENT '组件路径',
    perms VARCHAR(100) COMMENT '权限标识',
    icon VARCHAR(100) COMMENT '菜单图标',
    menu_type CHAR(1) COMMENT '菜单类型：M-目录，C-菜单，F-按钮',
    visible TINYINT DEFAULT 1 COMMENT '是否显示：0-隐藏，1-显示',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色菜单关联表
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

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

-- =============================================
-- 第二部分：业务表
-- =============================================

-- 房型表
DROP TABLE IF EXISTS room_type;
CREATE TABLE room_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房型ID',
    type_name VARCHAR(50) NOT NULL COMMENT '房型名称',
    price DECIMAL(10,2) NOT NULL COMMENT '标准价格',
    bed_type VARCHAR(50) COMMENT '床型',
    max_guests INT DEFAULT 2 COMMENT '最大入住人数',
    area DECIMAL(6,2) COMMENT '房间面积(平方米)',
    description TEXT COMMENT '房型描述',
    amenities VARCHAR(500) COMMENT '设施(JSON格式)',
    images VARCHAR(1000) COMMENT '图片(JSON格式)',
    status TINYINT DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房型表';

-- 房间表
DROP TABLE IF EXISTS room;
CREATE TABLE room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房间ID',
    room_number VARCHAR(20) NOT NULL COMMENT '房间号',
    room_type_id BIGINT NOT NULL COMMENT '房型ID',
    floor INT COMMENT '楼层',
    status TINYINT DEFAULT 0 COMMENT '状态：0-空闲，1-已预订，2-入住中，3-清洁中，4-维修中',
    remark VARCHAR(255) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_room_number (room_number),
    KEY idx_room_type (room_type_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间表';

-- 客户表
DROP TABLE IF EXISTS customer;
CREATE TABLE customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '客户ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    id_card VARCHAR(20) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    birthday DATE COMMENT '生日',
    address VARCHAR(255) COMMENT '地址',
    member_level TINYINT DEFAULT 0 COMMENT '会员等级：0-普通，1-银卡，2-金卡，3-钻石',
    points INT DEFAULT 0 COMMENT '积分',
    total_consumption DECIMAL(12,2) DEFAULT 0 COMMENT '累计消费',
    visit_count INT DEFAULT 0 COMMENT '入住次数',
    remark VARCHAR(255) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_phone (phone),
    KEY idx_id_card (id_card)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 预订表
DROP TABLE IF EXISTS reservation;
CREATE TABLE reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预订ID',
    reservation_no VARCHAR(32) NOT NULL COMMENT '预订单号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    room_type_id BIGINT NOT NULL COMMENT '房型ID',
    room_id BIGINT COMMENT '分配的房间ID',
    check_in_date DATE NOT NULL COMMENT '预计入住日期',
    check_out_date DATE NOT NULL COMMENT '预计离店日期',
    nights INT COMMENT '入住天数',
    room_count INT DEFAULT 1 COMMENT '房间数量',
    guest_count INT DEFAULT 1 COMMENT '入住人数',
    total_price DECIMAL(10,2) COMMENT '预订总价',
    deposit DECIMAL(10,2) DEFAULT 0 COMMENT '押金',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待确认，1-已确认，2-已入住，3-已完成，4-已取消',
    source TINYINT DEFAULT 0 COMMENT '来源：0-前台，1-网站，2-APP，3-第三方',
    remark VARCHAR(255) COMMENT '备注',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    operator_id BIGINT COMMENT '操作员ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_reservation_no (reservation_no),
    KEY idx_customer (customer_id),
    KEY idx_check_in_date (check_in_date),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预订表';

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

-- 入住人员表
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

-- =============================================
-- 第三部分：报表表（Quartz定时任务生成）
-- =============================================

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

-- =============================================
-- 第四部分：初始数据
-- =============================================

-- 初始化角色
INSERT INTO sys_role (id, role_name, role_key, sort, status, remark) VALUES
(1, '超级管理员', 'admin', 1, 1, '拥有所有权限'),
(2, '前台人员', 'reception', 2, 1, '负责预订、入住、退房等操作'),
(3, '普通用户', 'user', 3, 1, '普通注册用户');

-- 初始化用户（密码: admin123）
INSERT INTO sys_user (id, username, password, nickname, email, phone, gender, status) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 'admin@hotel.com', '13800000000', 1, 1),
(2, 'reception', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '前台小王', 'reception@hotel.com', '13800000001', 2, 1),
(3, 'user', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '测试用户', 'user@hotel.com', '13800000002', 1, 1);

-- 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1), (2, 2), (3, 3);

-- 初始化菜单
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, icon, menu_type, visible, sort) VALUES
(1, 0, '首页', '/dashboard', 'dashboard/index', NULL, 'House', 'C', 1, 1),
(2, 0, '系统管理', '/system', NULL, NULL, 'Setting', 'M', 1, 100),
(3, 0, '房间管理', '/room', NULL, NULL, 'House', 'M', 1, 10),
(4, 0, '预订管理', '/reservation', NULL, NULL, 'Calendar', 'M', 1, 20),
(5, 0, '入住管理', '/checkin', NULL, NULL, 'Key', 'M', 1, 30),
(6, 0, '客户管理', '/customer', NULL, NULL, 'User', 'M', 1, 40),
(7, 0, '财务管理', '/finance', NULL, NULL, 'Money', 'M', 1, 50),
(21, 2, '用户管理', '/system/user', 'system/user/index', 'system:user:list', 'User', 'C', 1, 1),
(22, 2, '角色管理', '/system/role', 'system/role/index', 'system:role:list', 'UserFilled', 'C', 1, 2),
(23, 2, '菜单管理', '/system/menu', 'system/menu/index', 'system:menu:list', 'Menu', 'C', 1, 3),
(24, 2, '操作日志', '/system/log', 'system/log/index', 'system:log:list', 'Document', 'C', 1, 4),
(31, 3, '房型管理', '/room/type', 'room/type/index', 'room:type:list', 'Grid', 'C', 1, 1),
(32, 3, '房间列表', '/room/list', 'room/list/index', 'room:list:list', 'List', 'C', 1, 2),
(33, 3, '房态总览', '/room/status', 'room/status/index', 'room:status:list', 'Monitor', 'C', 1, 3),
(41, 4, '预订列表', '/reservation/list', 'reservation/list/index', 'reservation:list', 'List', 'C', 1, 1),
(42, 4, '新建预订', '/reservation/create', 'reservation/create/index', 'reservation:create', 'Plus', 'C', 1, 2),
(51, 5, '入住列表', '/checkin/list', 'checkin/list/index', 'checkin:list', 'List', 'C', 1, 1),
(52, 5, '办理入住', '/checkin/create', 'checkin/create/index', 'checkin:create', 'Plus', 'C', 1, 2),
(53, 5, '退房结算', '/checkin/checkout', 'checkin/checkout/index', 'checkin:checkout', 'Finished', 'C', 1, 3),
(61, 6, '客户列表', '/customer/list', 'customer/list/index', 'customer:list', 'List', 'C', 1, 1),
(71, 7, '账单管理', '/finance/bill', 'finance/bill/index', 'finance:bill:list', 'Tickets', 'C', 1, 1),
(72, 7, '统计报表', '/finance/report', 'finance/report/index', 'finance:report:list', 'DataAnalysis', 'C', 1, 2);

-- 角色菜单关联（管理员拥有所有菜单）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- 前台人员菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 1), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7),
(2, 31), (2, 32), (2, 33), (2, 41), (2, 42),
(2, 51), (2, 52), (2, 53), (2, 61), (2, 71), (2, 72);

-- 普通用户菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(3, 1), (3, 4), (3, 41), (3, 42);

-- 初始化房型
INSERT INTO room_type (id, type_name, price, bed_type, max_guests, area, description, status) VALUES
(1, '标准单人间', 199.00, '单人床', 1, 20.00, '温馨舒适的单人房间，配备基础设施', 1),
(2, '标准双人间', 299.00, '双人床', 2, 25.00, '宽敞明亮的双人房间，适合情侣或朋友', 1),
(3, '豪华大床房', 399.00, '大床', 2, 35.00, '豪华装修，配备高端设施，享受尊贵体验', 1),
(4, '商务套房', 599.00, '大床', 2, 50.00, '独立客厅，适合商务人士', 1),
(5, '家庭房', 499.00, '双床', 4, 45.00, '宽敞空间，适合家庭出行', 1),
(6, '总统套房', 1299.00, '特大床', 2, 100.00, '顶级奢华，尊享私密空间', 1);

-- 初始化房间
INSERT INTO room (id, room_number, room_type_id, floor, status) VALUES
(1, '101', 1, 1, 0), (2, '102', 1, 1, 0), (3, '103', 1, 1, 0),
(4, '105', 2, 1, 0), (5, '106', 2, 1, 0),
(6, '201', 2, 2, 0), (7, '202', 2, 2, 0), (8, '203', 2, 2, 0),
(9, '205', 3, 2, 0), (10, '206', 3, 2, 0),
(11, '301', 3, 3, 0), (12, '302', 3, 3, 0),
(13, '303', 4, 3, 0), (14, '305', 4, 3, 0),
(15, '401', 4, 4, 0), (16, '402', 5, 4, 0),
(17, '403', 5, 4, 0), (18, '405', 5, 4, 0),
(19, '501', 6, 5, 0), (20, '502', 6, 5, 0);

-- 初始化客户
INSERT INTO customer (id, name, id_card, phone, email, gender, member_level, points, total_consumption, visit_count) VALUES
(1, '张三', '110101199001011234', '13900001111', 'zhangsan@email.com', 1, 2, 5000, 8888.00, 10),
(2, '李四', '110101199202022345', '13900002222', 'lisi@email.com', 1, 1, 2000, 3500.00, 5),
(3, '王五', '110101199303033456', '13900003333', 'wangwu@email.com', 2, 0, 500, 1200.00, 2),
(4, '赵六', '110101199404044567', '13900004444', 'zhaoliu@email.com', 1, 3, 15000, 28000.00, 25),
(5, '钱七', '110101199505055678', '13900005555', 'qianqi@email.com', 2, 1, 3000, 4500.00, 6),
(6, '孙八', '110101199606066789', '13900006666', 'sunba@email.com', 1, 0, 200, 600.00, 1),
(7, '周九', '110101199707077890', '13900007777', 'zhoujiu@email.com', 2, 1, 2500, 4000.00, 4),
(8, '吴十', '110101199808088901', '13900008888', 'wushi@email.com', 1, 2, 6000, 12000.00, 12),
(9, '郑十一', '110101199909099012', '13900009999', 'zheng11@email.com', 2, 0, 100, 300.00, 1),
(10, '陈十二', '110101200010100123', '13900010000', 'chen12@email.com', 1, 1, 1800, 3200.00, 3);

-- =============================================
-- 预订数据
-- =============================================
INSERT INTO reservation (id, reservation_no, customer_id, room_type_id, room_id, check_in_date, check_out_date, nights, room_count, guest_count, total_price, deposit, status, source, remark, operator_id, create_time) VALUES
(1, 'RSV202602010001', 1, 2, 6, '2026-02-01', '2026-02-03', 2, 1, 2, 598.00, 200.00, 3, 0, '老客户预订', 1, '2026-01-28 10:00:00'),
(2, 'RSV202602020001', 2, 3, 9, '2026-02-02', '2026-02-04', 2, 1, 2, 798.00, 300.00, 3, 1, '网站预订', 1, '2026-01-30 14:30:00'),
(3, 'RSV202602050001', 3, 1, 1, '2026-02-05', '2026-02-06', 1, 1, 1, 199.00, 100.00, 2, 0, NULL, 1, '2026-02-04 09:00:00'),
(4, 'RSV202602060001', 4, 4, 13, '2026-02-06', '2026-02-08', 2, 1, 2, 1198.00, 500.00, 1, 2, 'APP预订，VIP客户', 1, '2026-02-03 16:00:00'),
(5, 'RSV202602070001', 5, 2, NULL, '2026-02-07', '2026-02-09', 2, 1, 2, 598.00, 200.00, 0, 0, '待确认', 2, '2026-02-05 11:00:00'),
(6, 'RSV202602080001', 6, 5, NULL, '2026-02-08', '2026-02-10', 2, 1, 3, 998.00, 300.00, 0, 1, '家庭出游', 2, '2026-02-05 15:00:00'),
(7, 'RSV202602100001', 7, 3, NULL, '2026-02-10', '2026-02-12', 2, 1, 2, 798.00, 300.00, 4, 0, '客户取消', 1, '2026-02-01 10:00:00'),
(8, 'RSV202602060002', 8, 6, 19, '2026-02-06', '2026-02-09', 3, 1, 2, 3897.00, 1000.00, 1, 3, '第三方平台预订', 1, '2026-02-04 20:00:00');

-- =============================================
-- 入住数据
-- =============================================
-- 更新部分房间状态为入住中
UPDATE room SET status = 2 WHERE id IN (1, 6, 9, 13, 19);
UPDATE room SET status = 1 WHERE id IN (7, 11, 16);

INSERT INTO check_in (id, check_in_no, reservation_id, customer_id, room_id, check_in_time, check_out_time, expected_check_out, nights, room_price, total_price, deposit, extra_charges, discount, actual_amount, paid_amount, status, operator_id, create_time) VALUES
-- 已退房记录
(1, 'CI202602010001', 1, 1, 6, '2026-02-01 14:00:00', '2026-02-03 11:30:00', '2026-02-03', 2, 299.00, 598.00, 200.00, 50.00, 0.00, 648.00, 648.00, 1, 1, '2026-02-01 14:00:00'),
(2, 'CI202602020001', 2, 2, 9, '2026-02-02 15:30:00', '2026-02-04 10:00:00', '2026-02-04', 2, 399.00, 798.00, 300.00, 0.00, 50.00, 748.00, 748.00, 1, 1, '2026-02-02 15:30:00'),
-- 当前入住中
(3, 'CI202602050001', 3, 3, 1, '2026-02-05 13:00:00', NULL, '2026-02-06', 1, 199.00, 199.00, 100.00, 0.00, 0.00, 199.00, 299.00, 0, 1, '2026-02-05 13:00:00'),
(4, 'CI202602060001', 4, 4, 13, '2026-02-06 14:30:00', NULL, '2026-02-08', 2, 599.00, 1198.00, 500.00, 0.00, 100.00, 1098.00, 1598.00, 0, 1, '2026-02-06 14:30:00'),
(5, 'CI202602060002', 8, 8, 19, '2026-02-06 16:00:00', NULL, '2026-02-09', 3, 1299.00, 3897.00, 1000.00, 200.00, 0.00, 4097.00, 5097.00, 0, 1, '2026-02-06 16:00:00'),
-- 散客入住（无预订）
(6, 'CI202602050002', NULL, 6, 6, '2026-02-05 18:00:00', NULL, '2026-02-07', 2, 299.00, 598.00, 200.00, 0.00, 0.00, 598.00, 798.00, 0, 2, '2026-02-05 18:00:00'),
(7, 'CI202602060003', NULL, 9, 9, '2026-02-06 10:00:00', NULL, '2026-02-07', 1, 399.00, 399.00, 200.00, 30.00, 0.00, 429.00, 629.00, 0, 2, '2026-02-06 10:00:00');

-- 入住人员
INSERT INTO check_in_guest (check_in_id, name, id_card, phone, is_primary) VALUES
(1, '张三', '110101199001011234', '13900001111', 1),
(1, '张三妻子', '110101199201011235', '13900001112', 0),
(2, '李四', '110101199202022345', '13900002222', 1),
(3, '王五', '110101199303033456', '13900003333', 1),
(4, '赵六', '110101199404044567', '13900004444', 1),
(4, '赵六助理', '110101199504044568', '13900004445', 0),
(5, '吴十', '110101199808088901', '13900008888', 1),
(5, '吴十夫人', '110101199908088902', '13900008889', 0),
(6, '孙八', '110101199606066789', '13900006666', 1),
(7, '郑十一', '110101199909099012', '13900009999', 1);

-- =============================================
-- 账单数据
-- =============================================
INSERT INTO bill (id, bill_no, check_in_id, customer_id, bill_type, item_name, amount, payment_method, payment_status, payment_time, operator_id, create_time) VALUES
-- 已退房账单（已支付）
(1, 'BL202602010001', 1, 1, 0, '房费-标准双人间2晚', 598.00, 1, 1, '2026-02-03 11:30:00', 1, '2026-02-01 14:00:00'),
(2, 'BL202602010002', 1, 1, 2, '押金', 200.00, 0, 1, '2026-02-01 14:00:00', 1, '2026-02-01 14:00:00'),
(3, 'BL202602010003', 1, 1, 1, '客房消费-矿泉水', 20.00, 1, 1, '2026-02-02 20:00:00', 1, '2026-02-02 20:00:00'),
(4, 'BL202602010004', 1, 1, 1, '客房消费-洗衣服务', 30.00, 1, 1, '2026-02-03 09:00:00', 1, '2026-02-03 09:00:00'),
(5, 'BL202602010005', 1, 1, 3, '押金退还', 200.00, 0, 1, '2026-02-03 11:30:00', 1, '2026-02-03 11:30:00'),

(6, 'BL202602020001', 2, 2, 0, '房费-豪华大床房2晚', 798.00, 2, 1, '2026-02-04 10:00:00', 1, '2026-02-02 15:30:00'),
(7, 'BL202602020002', 2, 2, 2, '押金', 300.00, 0, 1, '2026-02-02 15:30:00', 1, '2026-02-02 15:30:00'),
(8, 'BL202602020003', 2, 2, 3, '优惠折扣', 50.00, NULL, 1, '2026-02-04 10:00:00', 1, '2026-02-04 10:00:00'),
(9, 'BL202602020004', 2, 2, 3, '押金退还', 300.00, 0, 1, '2026-02-04 10:00:00', 1, '2026-02-04 10:00:00'),

-- 当前入住账单
(10, 'BL202602050001', 3, 3, 0, '房费-标准单人间1晚', 199.00, NULL, 0, NULL, 1, '2026-02-05 13:00:00'),
(11, 'BL202602050002', 3, 3, 2, '押金', 100.00, 0, 1, '2026-02-05 13:00:00', 1, '2026-02-05 13:00:00'),

(12, 'BL202602060001', 4, 4, 0, '房费-商务套房2晚', 1198.00, NULL, 0, NULL, 1, '2026-02-06 14:30:00'),
(13, 'BL202602060002', 4, 4, 2, '押金', 500.00, 3, 1, '2026-02-06 14:30:00', 1, '2026-02-06 14:30:00'),
(14, 'BL202602060003', 4, 4, 3, 'VIP折扣', 100.00, NULL, 1, '2026-02-06 14:30:00', 1, '2026-02-06 14:30:00'),

(15, 'BL202602060004', 5, 8, 0, '房费-总统套房3晚', 3897.00, NULL, 0, NULL, 1, '2026-02-06 16:00:00'),
(16, 'BL202602060005', 5, 8, 2, '押金', 1000.00, 3, 1, '2026-02-06 16:00:00', 1, '2026-02-06 16:00:00'),
(17, 'BL202602060006', 5, 8, 1, '客房消费-红酒', 200.00, 1, 1, '2026-02-06 20:00:00', 1, '2026-02-06 20:00:00'),

(18, 'BL202602050003', 6, 6, 0, '房费-标准双人间2晚', 598.00, NULL, 0, NULL, 2, '2026-02-05 18:00:00'),
(19, 'BL202602050004', 6, 6, 2, '押金', 200.00, 0, 1, '2026-02-05 18:00:00', 2, '2026-02-05 18:00:00'),

(20, 'BL202602060007', 7, 9, 0, '房费-豪华大床房1晚', 399.00, NULL, 0, NULL, 2, '2026-02-06 10:00:00'),
(21, 'BL202602060008', 7, 9, 2, '押金', 200.00, 1, 1, '2026-02-06 10:00:00', 2, '2026-02-06 10:00:00'),
(22, 'BL202602060009', 7, 9, 1, '客房消费-早餐', 30.00, 1, 1, '2026-02-06 08:00:00', 2, '2026-02-06 08:00:00');

-- =============================================
-- 日报表测试数据
-- =============================================
INSERT INTO daily_report (report_date, total_revenue, room_revenue, consume_revenue, deposit_revenue, refund_amount, check_in_count, check_out_count, reservation_count, new_customer_count, occupancy_rate, create_time) VALUES
('2026-02-01', 818.00, 598.00, 0.00, 200.00, 0.00, 1, 0, 2, 0, 5.00, '2026-02-02 01:00:00'),
('2026-02-02', 1148.00, 798.00, 50.00, 300.00, 0.00, 1, 0, 1, 0, 10.00, '2026-02-03 01:00:00'),
('2026-02-03', 598.00, 598.00, 30.00, 0.00, 200.00, 0, 1, 0, 0, 5.00, '2026-02-04 01:00:00'),
('2026-02-04', 748.00, 798.00, 0.00, 0.00, 350.00, 0, 1, 1, 0, 0.00, '2026-02-05 01:00:00'),
('2026-02-05', 499.00, 199.00, 0.00, 300.00, 0.00, 2, 0, 2, 2, 10.00, '2026-02-06 01:00:00');

-- =============================================
-- 月报表测试数据
-- =============================================
INSERT INTO monthly_report (report_year, report_month, total_revenue, room_revenue, consume_revenue, deposit_revenue, refund_amount, check_in_count, check_out_count, reservation_count, new_customer_count, avg_occupancy_rate, avg_daily_revenue, create_time) VALUES
(2026, 1, 45680.00, 38500.00, 3200.00, 5800.00, 1820.00, 45, 42, 52, 8, 35.50, 1473.55, '2026-02-01 02:00:00');

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 脚本执行完成
-- 默认账号: admin / admin123
-- =============================================
