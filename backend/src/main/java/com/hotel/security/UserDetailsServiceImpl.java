package com.hotel.security;

import com.hotel.entity.SysUser;
import com.hotel.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户详情服务实现
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        if (user.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 查询角色
        List<String> roleKeys = userMapper.selectRoleKeysByUserId(user.getId());
        Set<String> roles = new HashSet<>(roleKeys);

        // 查询权限
        Set<String> permissions = new HashSet<>();
        if (roles.contains("admin")) {
            // 管理员拥有所有权限
            permissions.add("*:*:*");
        } else {
            List<String> perms = userMapper.selectPermsByUserId(user.getId());
            permissions.addAll(perms);
        }

        return new LoginUser(user, roles, permissions);
    }
}
