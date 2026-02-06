package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.dto.LoginRequest;
import com.hotel.dto.LoginResponse;
import com.hotel.dto.RegisterRequest;
import com.hotel.security.SecurityUtils;
import com.hotel.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ApiOperation("登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }

    @ApiOperation("注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success();
    }

    @ApiOperation("登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        Long userId = SecurityUtils.getUserId();
        if (userId != null) {
            authService.logout(userId);
        }
        return Result.success();
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<LoginResponse> getUserInfo() {
        LoginResponse response = new LoginResponse();
        response.setUserId(SecurityUtils.getUserId());
        response.setUsername(SecurityUtils.getUsername());
        response.setNickname(SecurityUtils.getLoginUser().getNickname());
        response.setRoles(SecurityUtils.getLoginUser().getRoles());
        response.setPermissions(SecurityUtils.getLoginUser().getPermissions());
        return Result.success(response);
    }
}
