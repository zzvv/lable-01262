package com.hotel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.entity.SysUser;
import com.hotel.entity.SysUserRole;
import com.hotel.mapper.SysUserMapper;
import com.hotel.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户服务
 */
@Service
@RequiredArgsConstructor
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户
     */
    public IPage<SysUser> page(Page<SysUser> page, String username, String phone, Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SysUser::getUsername, username)
               .like(StringUtils.hasText(phone), SysUser::getPhone, phone)
               .eq(status != null, SysUser::getStatus, status)
               .orderByDesc(SysUser::getCreateTime);
        return page(page, wrapper);
    }

    /**
     * 创建用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void createUser(SysUser user, List<Long> roleIds) {
        // 检查用户名
        if (baseMapper.selectByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);

        // 保存用户角色关联
        saveUserRoles(user.getId(), roleIds);
    }

    /**
     * 更新用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(SysUser user, List<Long> roleIds) {
        // 检查用户名
        SysUser existUser = baseMapper.selectByUsername(user.getUsername());
        if (existUser != null && !existUser.getId().equals(user.getId())) {
            throw new BusinessException("用户名已存在");
        }

        // 如果密码不为空则更新密码
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }

        updateById(user);

        // 更新用户角色关联
        if (roleIds != null) {
            userRoleMapper.deleteByUserId(user.getId());
            saveUserRoles(user.getId(), roleIds);
        }
    }

    /**
     * 保存用户角色关联
     */
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }

    /**
     * 重置密码
     */
    public void resetPassword(Long userId, String newPassword) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }
}
