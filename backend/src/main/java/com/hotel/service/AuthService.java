package com.hotel.service;

import com.hotel.common.BusinessException;
import com.hotel.common.Constants;
import com.hotel.dto.LoginRequest;
import com.hotel.dto.LoginResponse;
import com.hotel.dto.RegisterRequest;
import com.hotel.entity.SysUser;
import com.hotel.entity.SysUserRole;
import com.hotel.mapper.SysUserMapper;
import com.hotel.mapper.SysUserRoleMapper;
import com.hotel.security.JwtUtils;
import com.hotel.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 登录
     */
    public LoginResponse login(LoginRequest request) {
        // 认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 生成Token
        String token = jwtUtils.generateToken(loginUser.getUserId(), loginUser.getUsername());

        // 存入Redis
        String redisKey = Constants.RedisKey.USER_PREFIX + loginUser.getUserId();
        redisTemplate.opsForValue().set(redisKey, loginUser, 24, TimeUnit.HOURS);

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(loginUser.getUserId());
        response.setUsername(loginUser.getUsername());
        response.setNickname(loginUser.getNickname());
        response.setRoles(loginUser.getRoles());
        response.setPermissions(loginUser.getPermissions());

        return response;
    }

    /**
     * 注册
     */
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        // 检查用户名是否存在
        SysUser existUser = userMapper.selectByUsername(request.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        userMapper.insert(user);

        // 分配默认角色（普通用户）
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(3L); // 普通用户角色ID
        userRoleMapper.insert(userRole);
    }

    /**
     * 登出
     */
    public void logout(Long userId) {
        String redisKey = Constants.RedisKey.USER_PREFIX + userId;
        redisTemplate.delete(redisKey);
    }
}
