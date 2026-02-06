package com.hotel.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单导出VO
 */
@Data
public class BillExportVO {

    @ExcelProperty("账单号")
    @ColumnWidth(25)
    private String billNo;

    @ExcelProperty("客户姓名")
    @ColumnWidth(15)
    private String customerName;

    @ExcelProperty("房间号")
    @ColumnWidth(12)
    private String roomNumber;

    @ExcelProperty("账单类型")
    @ColumnWidth(12)
    private String billTypeName;

    @ExcelProperty("项目名称")
    @ColumnWidth(20)
    private String itemName;

    @ExcelProperty("金额")
    @ColumnWidth(12)
    private BigDecimal amount;

    @ExcelProperty("支付方式")
    @ColumnWidth(12)
    private String paymentMethodName;

    @ExcelProperty("支付状态")
    @ColumnWidth(12)
    private String paymentStatusName;

    @ExcelProperty("支付时间")
    @ColumnWidth(20)
    private LocalDateTime paymentTime;

    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private LocalDateTime createTime;

    @ExcelProperty("备注")
    @ColumnWidth(25)
    private String remark;
}
