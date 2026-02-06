-- =============================================
-- 酒店管理系统数据库脚本
-- 可直接在 Navicat 中运行
-- =============================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS hotel_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hotel_management;

-- =============================================
-- 系统管理表
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

-- =============================================
-- 业务表
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
