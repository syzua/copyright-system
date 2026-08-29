package com.syzua.copyright.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syzua.copyright.dto.LoginRequest;
import com.syzua.copyright.dto.RegisterRequest;
import com.syzua.copyright.entity.User;
import com.syzua.copyright.mapper.UserMapper;
import com.syzua.copyright.utils.HashUtils;
import com.syzua.copyright.utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Map<String, Object> register(RegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(HashUtils.sha256(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER");
        userMapper.insert(user);

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        return result;
    }

    public Map<String, Object> login(LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!user.getPassword().equals(HashUtils.sha256(request.getPassword()))) {
            throw new RuntimeException("密码错误");
        }

        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        return result;
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}
