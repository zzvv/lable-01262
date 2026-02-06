package com.hotel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.entity.Customer;
import com.hotel.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户服务
 */
@Service
@RequiredArgsConstructor
public class CustomerService extends ServiceImpl<CustomerMapper, Customer> {

    /**
     * 分页查询客户
     */
    public IPage<Customer> page(Page<Customer> page, String name, String phone, Integer memberLevel) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Customer::getName, name)
               .like(StringUtils.hasText(phone), Customer::getPhone, phone)
               .eq(memberLevel != null, Customer::getMemberLevel, memberLevel)
               .orderByDesc(Customer::getCreateTime);
        return page(page, wrapper);
    }

    /**
     * 根据手机号查询客户
     */
    public Customer getByPhone(String phone) {
        return baseMapper.selectByPhone(phone);
    }

    /**
     * 根据身份证号查询客户
     */
    public Customer getByIdCard(String idCard) {
        return baseMapper.selectByIdCard(idCard);
    }

    /**
     * 创建或获取客户
     */
    public Customer getOrCreate(Customer customer) {
        // 先根据手机号查询
        if (StringUtils.hasText(customer.getPhone())) {
            Customer existCustomer = getByPhone(customer.getPhone());
            if (existCustomer != null) {
                return existCustomer;
            }
        }

        // 再根据身份证号查询
        if (StringUtils.hasText(customer.getIdCard())) {
            Customer existCustomer = getByIdCard(customer.getIdCard());
            if (existCustomer != null) {
                return existCustomer;
            }
        }

        // 创建新客户
        save(customer);
        return customer;
    }

    /**
     * 更新客户消费信息
     */
    public void updateConsumption(Long customerId, BigDecimal amount) {
        // 计算积分（每消费1元得1积分）
        int points = amount.intValue();
        baseMapper.updateConsumption(customerId, amount, points);

        // 更新会员等级
        updateMemberLevel(customerId);
    }

    /**
     * 更新会员等级
     */
    private void updateMemberLevel(Long customerId) {
        Customer customer = getById(customerId);
        if (customer == null) return;

        int newLevel = 0;
        BigDecimal total = customer.getTotalConsumption();

        if (total.compareTo(new BigDecimal("50000")) >= 0) {
            newLevel = 3; // 钻石
        } else if (total.compareTo(new BigDecimal("20000")) >= 0) {
            newLevel = 2; // 金卡
        } else if (total.compareTo(new BigDecimal("5000")) >= 0) {
            newLevel = 1; // 银卡
        }

        if (newLevel != customer.getMemberLevel()) {
            Customer update = new Customer();
            update.setId(customerId);
            update.setMemberLevel(newLevel);
            updateById(update);
        }
    }

    /**
     * 导出客户列表
     */
    public List<Customer> listForExport(String name, String phone, Integer memberLevel) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Customer::getName, name)
               .like(StringUtils.hasText(phone), Customer::getPhone, phone)
               .eq(memberLevel != null, Customer::getMemberLevel, memberLevel)
               .eq(Customer::getDeleted, 0)
               .orderByDesc(Customer::getCreateTime);
        return list(wrapper);
    }
}
