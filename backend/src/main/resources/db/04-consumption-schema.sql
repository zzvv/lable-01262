-- =============================================
-- 消费记录表
-- =============================================

USE hotel_management;

DROP TABLE IF EXISTS consumption;
CREATE TABLE consumption (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消费记录ID',
    consumption_no VARCHAR(32) NOT NULL COMMENT '消费记录号',
    check_in_id BIGINT NOT NULL COMMENT '入住记录ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    item_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    quantity INT DEFAULT 1 COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    status TINYINT DEFAULT 0 COMMENT '状态：0-有效，1-已作废',
    is_billed TINYINT DEFAULT 0 COMMENT '是否已对账：0-未对账，1-已对账',
    bill_id BIGINT COMMENT '账单ID',
    operator_id BIGINT COMMENT '操作人ID',
    cancel_operator_id BIGINT COMMENT '作废操作人ID',
    cancel_time DATETIME COMMENT '作废时间',
    cancel_remark VARCHAR(255) COMMENT '作废备注',
    remark VARCHAR(255) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_consumption_no (consumption_no),
    KEY idx_check_in (check_in_id),
    KEY idx_customer (customer_id),
    KEY idx_status (status),
    KEY idx_is_billed (is_billed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消费记录表';
