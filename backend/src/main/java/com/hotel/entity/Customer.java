package com.hotel.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客户实体
 */
@Data
@TableName("customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelIgnore
    @TableId(type = IdType.AUTO)
    private Long id;

    @ExcelProperty("姓名")
    @ColumnWidth(15)
    private String name;

    @ExcelProperty("身份证号")
    @ColumnWidth(25)
    private String idCard;

    @ExcelProperty("手机号")
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty("邮箱")
    @ColumnWidth(25)
    private String email;

    @ExcelProperty("性别")
    @ColumnWidth(10)
    private Integer gender;

    @ExcelProperty("生日")
    @ColumnWidth(15)
    private LocalDate birthday;

    @ExcelProperty("地址")
    @ColumnWidth(30)
    private String address;

    @ExcelProperty("会员等级")
    @ColumnWidth(12)
    private Integer memberLevel;

    @ExcelProperty("积分")
    @ColumnWidth(10)
    private Integer points;

    @ExcelProperty("累计消费")
    @ColumnWidth(15)
    private BigDecimal totalConsumption;

    @ExcelProperty("入住次数")
    @ColumnWidth(12)
    private Integer visitCount;

    @ExcelProperty("备注")
    @ColumnWidth(30)
    private String remark;

    @ExcelIgnore
    @TableLogic
    private Integer deleted;

    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ExcelIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
