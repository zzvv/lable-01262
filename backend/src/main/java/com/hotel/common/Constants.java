package com.hotel.common;

/**
 * 常量定义
 */
public class Constants {

    /**
     * 房间状态
     */
    public static class RoomStatus {
        public static final int FREE = 0;       // 空闲
        public static final int RESERVED = 1;   // 已预订
        public static final int OCCUPIED = 2;   // 入住中
        public static final int CLEANING = 3;   // 清洁中
        public static final int MAINTENANCE = 4; // 维修中
    }

    /**
     * 预订状态
     */
    public static class ReservationStatus {
        public static final int PENDING = 0;    // 待确认
        public static final int CONFIRMED = 1;  // 已确认
        public static final int CHECKED_IN = 2; // 已入住
        public static final int COMPLETED = 3;  // 已完成
        public static final int CANCELLED = 4;  // 已取消
    }

    /**
     * 入住状态
     */
    public static class CheckInStatus {
        public static final int IN_STAY = 0;    // 入住中
        public static final int CHECKED_OUT = 1; // 已退房
        public static final int EXTENDED = 2;   // 续住中
    }

    /**
     * 账单类型
     */
    public static class BillType {
        public static final int ROOM_FEE = 0;   // 房费
        public static final int CONSUMPTION = 1; // 消费
        public static final int DEPOSIT = 2;    // 押金
        public static final int REFUND = 3;     // 退款
    }

    /**
     * 支付状态
     */
    public static class PaymentStatus {
        public static final int UNPAID = 0;     // 未支付
        public static final int PAID = 1;       // 已支付
        public static final int REFUNDED = 2;   // 已退款
    }

    /**
     * Redis Key 前缀
     */
    public static class RedisKey {
        public static final String TOKEN_PREFIX = "hotel:token:";
        public static final String USER_PREFIX = "hotel:user:";
        public static final String CAPTCHA_PREFIX = "hotel:captcha:";
    }
}
