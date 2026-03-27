package com.hotel.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ConsumptionRequest {

    @NotNull(message = "入住记录ID不能为空")
    private Long checkInId;

    @NotBlank(message = "商品名称不能为空")
    private String itemName;

    private Integer quantity = 1;

    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    private BigDecimal unitPrice;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    private String remark;
}
