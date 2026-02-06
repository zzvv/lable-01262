package com.hotel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final RoomService roomService;
    private final ReservationService reservationService;
    private final CheckInService checkInService;
    private final BillService billService;
    private final CustomerService customerService;

    /**
     * 获取统计数据
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 房间统计
            List<Map<String, Object>> roomStats = roomService.countByStatus();
            int totalRooms = 0;
            int freeRooms = 0;
            int occupiedRooms = 0;
            if (roomStats != null) {
                for (Map<String, Object> stat : roomStats) {
                    Object statusObj = stat.get("status");
                    Object countObj = stat.get("count");
                    if (statusObj != null && countObj != null) {
                        int status = ((Number) statusObj).intValue();
                        int count = ((Number) countObj).intValue();
                        totalRooms += count;
                        if (status == 0) freeRooms = count;
                        if (status == 2) occupiedRooms = count;
                    }
                }
            }
            result.put("totalRooms", totalRooms);
            result.put("freeRooms", freeRooms);
            result.put("occupiedRooms", occupiedRooms);
            result.put("occupancyRate", totalRooms > 0 ? 
                    Math.round(occupiedRooms * 100.0 / totalRooms) : 0);

            // 今日统计
            Integer todayCheckIn = reservationService.countTodayCheckIn();
            Integer todayCheckOut = reservationService.countTodayCheckOut();
            Integer currentGuests = checkInService.countCurrentGuests();
            result.put("todayCheckIn", todayCheckIn != null ? todayCheckIn : 0);
            result.put("todayCheckOut", todayCheckOut != null ? todayCheckOut : 0);
            result.put("currentGuests", currentGuests != null ? currentGuests : 0);

            // 营收统计
            result.put("todayRevenue", billService.sumTodayRevenue());
            result.put("monthRevenue", billService.sumMonthRevenue());

            // 客户统计
            result.put("totalCustomers", customerService.count());
        } catch (Exception e) {
            // 返回默认值
            result.put("totalRooms", 0);
            result.put("freeRooms", 0);
            result.put("occupiedRooms", 0);
            result.put("occupancyRate", 0);
            result.put("todayCheckIn", 0);
            result.put("todayCheckOut", 0);
            result.put("currentGuests", 0);
            result.put("todayRevenue", 0);
            result.put("monthRevenue", 0);
            result.put("totalCustomers", 0);
        }

        return result;
    }

    /**
     * 获取图表数据
     */
    public Map<String, Object> getChartData() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 房间状态分布
            result.put("roomStatusData", roomService.countByStatus());

            // 楼层房间分布
            result.put("floorData", roomService.countByFloor());

            // 每日入住趋势
            result.put("dailyCheckInData", checkInService.countDailyCheckIn());

            // 每日营收趋势
            result.put("dailyRevenueData", checkInService.countDailyRevenue());

            // 账单类型分布
            result.put("billTypeData", billService.sumByBillType());
        } catch (Exception e) {
            // 返回空数据
            result.put("roomStatusData", List.of());
            result.put("floorData", List.of());
            result.put("dailyCheckInData", List.of());
            result.put("dailyRevenueData", List.of());
            result.put("billTypeData", List.of());
        }

        return result;
    }
}
