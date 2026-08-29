package com.syzua.copyright.controller;

import com.syzua.copyright.dto.LoginRequest;
import com.syzua.copyright.dto.RegisterRequest;
import com.syzua.copyright.dto.Result;
import com.syzua.copyright.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "用户管理", description = "用户注册、登录、信息查询")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            return Result.success(userService.register(request));
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            return Result.success(userService.login(request));
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public Result<?> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getById(userId));
    }
}
