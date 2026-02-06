package com.hotel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.entity.DailyReport;
import com.hotel.entity.MonthlyReport;
import com.hotel.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 报表服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final DailyReportMapper dailyReportMapper;
    private final MonthlyReportMapper monthlyReportMapper;
    private final BillMapper billMapper;
    private final CheckInMapper checkInMapper;
    private final ReservationMapper reservationMapper;
    private final CustomerMapper customerMapper;
    private final RoomMapper roomMapper;

    /**
     * 生成日报表
     */
    public DailyReport generateDailyReport(LocalDate date) {
        log.info("开始生成日报表: {}", date);
        
        // 检查是否已存在
        DailyReport existing = dailyReportMapper.selectByDate(date);
        if (existing != null) {
            dailyReportMapper.deleteById(existing.getId());
        }

        DailyReport report = new DailyReport();
        report.setReportDate(date);

        // 统计营收
        Map<String, BigDecimal> revenueMap = calculateDailyRevenue(date);
        report.setTotalRevenue(revenueMap.getOrDefault("total", BigDecimal.ZERO));
        report.setRoomRevenue(revenueMap.getOrDefault("room", BigDecimal.ZERO));
        report.setConsumeRevenue(revenueMap.getOrDefault("consume", BigDecimal.ZERO));
        report.setDepositRevenue(revenueMap.getOrDefault("deposit", BigDecimal.ZERO));
        report.setRefundAmount(revenueMap.getOrDefault("refund", BigDecimal.ZERO));

        // 统计入住/退房/预订
        report.setCheckInCount(countDailyCheckIn(date));
        report.setCheckOutCount(countDailyCheckOut(date));
        report.setReservationCount(countDailyReservation(date));
        report.setNewCustomerCount(countDailyNewCustomer(date));

        // 计算入住率
        report.setOccupancyRate(calculateOccupancyRate(date));

        report.setCreateTime(LocalDateTime.now());
        dailyReportMapper.insert(report);
        
        log.info("日报表生成完成: {}", date);
        return report;
    }

    /**
     * 生成月报表
     */
    public MonthlyReport generateMonthlyReport(int year, int month) {
        log.info("开始生成月报表: {}-{}", year, month);
        
        // 检查是否已存在
        MonthlyReport existing = monthlyReportMapper.selectByYearMonth(year, month);
        if (existing != null) {
            monthlyReportMapper.deleteById(existing.getId());
        }

        MonthlyReport report = new MonthlyReport();
        report.setReportYear(year);
        report.setReportMonth(month);

        // 从日报表汇总
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        LambdaQueryWrapper<DailyReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(DailyReport::getReportDate, startDate, endDate);
        List<DailyReport> dailyReports = dailyReportMapper.selectList(wrapper);

        if (dailyReports.isEmpty()) {
            // 如果没有日报表，直接从原始数据统计
            Map<String, BigDecimal> revenueMap = calculateMonthlyRevenue(year, month);
            report.setTotalRevenue(revenueMap.getOrDefault("total", BigDecimal.ZERO));
            report.setRoomRevenue(revenueMap.getOrDefault("room", BigDecimal.ZERO));
            report.setConsumeRevenue(revenueMap.getOrDefault("consume", BigDecimal.ZERO));
            report.setDepositRevenue(revenueMap.getOrDefault("deposit", BigDecimal.ZERO));
            report.setRefundAmount(revenueMap.getOrDefault("refund", BigDecimal.ZERO));
            report.setCheckInCount(0);
            report.setCheckOutCount(0);
            report.setReservationCount(0);
            report.setNewCustomerCount(0);
            report.setAvgOccupancyRate(BigDecimal.ZERO);
            report.setAvgDailyRevenue(BigDecimal.ZERO);
        } else {
            // 汇总日报表数据
            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal roomRevenue = BigDecimal.ZERO;
            BigDecimal consumeRevenue = BigDecimal.ZERO;
            BigDecimal depositRevenue = BigDecimal.ZERO;
            BigDecimal refundAmount = BigDecimal.ZERO;
            int checkInCount = 0;
            int checkOutCount = 0;
            int reservationCount = 0;
            int newCustomerCount = 0;
            BigDecimal totalOccupancy = BigDecimal.ZERO;

            for (DailyReport daily : dailyReports) {
                totalRevenue = totalRevenue.add(daily.getTotalRevenue() != null ? daily.getTotalRevenue() : BigDecimal.ZERO);
                roomRevenue = roomRevenue.add(daily.getRoomRevenue() != null ? daily.getRoomRevenue() : BigDecimal.ZERO);
                consumeRevenue = consumeRevenue.add(daily.getConsumeRevenue() != null ? daily.getConsumeRevenue() : BigDecimal.ZERO);
                depositRevenue = depositRevenue.add(daily.getDepositRevenue() != null ? daily.getDepositRevenue() : BigDecimal.ZERO);
                refundAmount = refundAmount.add(daily.getRefundAmount() != null ? daily.getRefundAmount() : BigDecimal.ZERO);
                checkInCount += daily.getCheckInCount() != null ? daily.getCheckInCount() : 0;
                checkOutCount += daily.getCheckOutCount() != null ? daily.getCheckOutCount() : 0;
                reservationCount += daily.getReservationCount() != null ? daily.getReservationCount() : 0;
                newCustomerCount += daily.getNewCustomerCount() != null ? daily.getNewCustomerCount() : 0;
                totalOccupancy = totalOccupancy.add(daily.getOccupancyRate() != null ? daily.getOccupancyRate() : BigDecimal.ZERO);
            }

            report.setTotalRevenue(totalRevenue);
            report.setRoomRevenue(roomRevenue);
            report.setConsumeRevenue(consumeRevenue);
            report.setDepositRevenue(depositRevenue);
            report.setRefundAmount(refundAmount);
            report.setCheckInCount(checkInCount);
            report.setCheckOutCount(checkOutCount);
            report.setReservationCount(reservationCount);
            report.setNewCustomerCount(newCustomerCount);
            report.setAvgOccupancyRate(totalOccupancy.divide(new BigDecimal(dailyReports.size()), 2, RoundingMode.HALF_UP));
            report.setAvgDailyRevenue(totalRevenue.divide(new BigDecimal(dailyReports.size()), 2, RoundingMode.HALF_UP));
        }

        report.setCreateTime(LocalDateTime.now());
        monthlyReportMapper.insert(report);
        
        log.info("月报表生成完成: {}-{}", year, month);
        return report;
    }

    /**
     * 分页查询日报表
     */
    public IPage<DailyReport> pageDailyReport(Page<DailyReport> page, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<DailyReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(startDate != null, DailyReport::getReportDate, startDate)
               .le(endDate != null, DailyReport::getReportDate, endDate)
               .orderByDesc(DailyReport::getReportDate);
        return dailyReportMapper.selectPage(page, wrapper);
    }

    /**
     * 分页查询月报表
     */
    public IPage<MonthlyReport> pageMonthlyReport(Page<MonthlyReport> page, Integer year) {
        LambdaQueryWrapper<MonthlyReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(year != null, MonthlyReport::getReportYear, year)
               .orderByDesc(MonthlyReport::getReportYear)
               .orderByDesc(MonthlyReport::getReportMonth);
        return monthlyReportMapper.selectPage(page, wrapper);
    }

    // ========== 私有方法 ==========

    private Map<String, BigDecimal> calculateDailyRevenue(LocalDate date) {
        BigDecimal room = billMapper.sumByTypeAndDate(0, date);
        BigDecimal consume = billMapper.sumByTypeAndDate(1, date);
        BigDecimal deposit = billMapper.sumByTypeAndDate(2, date);
        BigDecimal refund = billMapper.sumByTypeAndDate(3, date);
        
        BigDecimal total = (room != null ? room : BigDecimal.ZERO)
                .add(consume != null ? consume : BigDecimal.ZERO)
                .add(deposit != null ? deposit : BigDecimal.ZERO)
                .subtract(refund != null ? refund : BigDecimal.ZERO);
        
        return Map.of(
            "total", total,
            "room", room != null ? room : BigDecimal.ZERO,
            "consume", consume != null ? consume : BigDecimal.ZERO,
            "deposit", deposit != null ? deposit : BigDecimal.ZERO,
            "refund", refund != null ? refund : BigDecimal.ZERO
        );
    }

    private Map<String, BigDecimal> calculateMonthlyRevenue(int year, int month) {
        BigDecimal room = billMapper.sumByTypeAndMonth(0, year, month);
        BigDecimal consume = billMapper.sumByTypeAndMonth(1, year, month);
        BigDecimal deposit = billMapper.sumByTypeAndMonth(2, year, month);
        BigDecimal refund = billMapper.sumByTypeAndMonth(3, year, month);
        
        BigDecimal total = (room != null ? room : BigDecimal.ZERO)
                .add(consume != null ? consume : BigDecimal.ZERO)
                .add(deposit != null ? deposit : BigDecimal.ZERO)
                .subtract(refund != null ? refund : BigDecimal.ZERO);
        
        return Map.of(
            "total", total,
            "room", room != null ? room : BigDecimal.ZERO,
            "consume", consume != null ? consume : BigDecimal.ZERO,
            "deposit", deposit != null ? deposit : BigDecimal.ZERO,
            "refund", refund != null ? refund : BigDecimal.ZERO
        );
    }

    private Integer countDailyCheckIn(LocalDate date) {
        return checkInMapper.countByDate(date);
    }

    private Integer countDailyCheckOut(LocalDate date) {
        return checkInMapper.countCheckOutByDate(date);
    }

    private Integer countDailyReservation(LocalDate date) {
        return reservationMapper.countByDate(date);
    }

    private Integer countDailyNewCustomer(LocalDate date) {
        return customerMapper.countByDate(date);
    }

    private BigDecimal calculateOccupancyRate(LocalDate date) {
        Integer totalRooms = roomMapper.countTotal();
        Integer occupiedRooms = roomMapper.countOccupied();
        if (totalRooms == null || totalRooms == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(occupiedRooms != null ? occupiedRooms : 0)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(totalRooms), 2, RoundingMode.HALF_UP);
    }
}
