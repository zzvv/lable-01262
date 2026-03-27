-- =============================================
-- 消费记录表
-- =============================================

USE hotel_management;

DROP TABLE IF EXISTS consumption_record;
CREATE TABLE consumption_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消费记录ID',
    check_in_id BIGINT NOT NULL COMMENT '入住记录ID',
    item_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    quantity INT DEFAULT 1 COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额（数量×单价）',
    payment_method TINYINT COMMENT '支付方式：0-现金，1-微信，2-支付宝，3-银行卡，4-挂账',
    payment_status TINYINT DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付，2-已退款',
    payment_time DATETIME COMMENT '支付时间',
    operator_id BIGINT NOT NULL COMMENT '操作员ID',
    remark VARCHAR(255) COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态：0-已作废，1-正常',
    cancelled_by BIGINT COMMENT '作废操作人ID',
    cancelled_time DATETIME COMMENT '作废时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_check_in (check_in_id),
    KEY idx_create_time (create_time),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消费记录表';

-- 添加一些初始数据（可选）
INSERT INTO consumption_record (check_in_id, item_name, quantity, unit_price, amount, payment_method, payment_status, payment_time, operator_id, remark, status, create_time) VALUES
(1, '矿泉水', 2, 10.00, 20.00, 1, 1, '2026-02-02 20:00:00', 1, '客房消费', 1, '2026-02-02 20:00:00'),
(1, '洗衣服务', 1, 30.00, 30.00, 1, 1, '2026-02-03 09:00:00', 1, '衣物清洗', 1, '2026-02-03 09:00:00'),
(5, '红酒', 1, 200.00, 200.00, 1, 1, '2026-02-06 20:00:00', 1, '客房消费', 1, '2026-02-06 20:00:00'),
(7, '早餐', 2, 15.00, 30.00, 1, 1, '2026-02-06 08:00:00', 2, '两份早餐', 1, '2026-02-06 08:00:00');
