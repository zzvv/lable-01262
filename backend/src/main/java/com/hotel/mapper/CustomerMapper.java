package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 客户Mapper
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

    /**
     * 根据手机号查询客户
     */
    @Select("SELECT * FROM customer WHERE phone = #{phone} AND deleted = 0")
    Customer selectByPhone(@Param("phone") String phone);

    /**
     * 根据身份证号查询客户
     */
    @Select("SELECT * FROM customer WHERE id_card = #{idCard} AND deleted = 0")
    Customer selectByIdCard(@Param("idCard") String idCard);

    /**
     * 更新客户消费信息
     */
    @Update("UPDATE customer SET total_consumption = total_consumption + #{amount}, " +
            "visit_count = visit_count + 1, points = points + #{points} WHERE id = #{id}")
    int updateConsumption(@Param("id") Long id, @Param("amount") BigDecimal amount, @Param("points") Integer points);

    /**
     * 统计指定日期新增客户数
     */
    @Select("SELECT COUNT(*) FROM customer WHERE DATE(create_time) = #{date} AND deleted = 0")
    Integer countByDate(@Param("date") LocalDate date);
}
