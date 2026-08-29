package com.syzua.copyright.controller;

import com.syzua.copyright.dto.CopyrightRegisterRequest;
import com.syzua.copyright.dto.Result;
import com.syzua.copyright.entity.CopyrightRecord;
import com.syzua.copyright.service.CopyrightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/copyright")
@Tag(name = "版权管理", description = "版权登记、验证、查询、存证证明")
public class CopyrightController {

    private final CopyrightService copyrightService;

    public CopyrightController(CopyrightService copyrightService) {
        this.copyrightService = copyrightService;
    }

    @PostMapping("/register")
    @Operation(summary = "版权登记", description = "上传作品内容，生成SHA-256数字指纹并存证")
    public Result<?> register(@Valid @RequestBody CopyrightRegisterRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            CopyrightRecord record = copyrightService.register(request, userId);
            return Result.success(record);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/verify")
    @Operation(summary = "版权验证", description = "上传作品内容，比对哈希验证是否已登记")
    public Result<?> verify(@RequestBody Map<String, String> body) {
        String content = body.get("content");
        if (content == null || content.isBlank()) {
            return Result.error(400, "作品内容不能为空");
        }
        return Result.success(copyrightService.verify(content));
    }

    @GetMapping("/query/{regNo}")
    @Operation(summary = "按登记号查询版权")
    public Result<?> queryByRegNo(@PathVariable String regNo) {
        try {
            return Result.success(copyrightService.getByRegNo(regNo));
        } catch (RuntimeException e) {
            return Result.error(404, e.getMessage());
        }
    }

    @GetMapping("/my-list")
    @Operation(summary = "查询我的版权记录")
    public Result<?> myList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return Result.success(copyrightService.listByUser(userId, page, size));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索版权")
    public Result<?> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(copyrightService.search(keyword, page, size));
    }

    @GetMapping("/proof/{id}")
    @Operation(summary = "获取区块链存证证明", description = "生成版权存证的文字证明，包含哈希、时间戳等信息")
    public Result<?> getBlockchainProof(@PathVariable Long id) {
        try {
            return Result.success(copyrightService.getBlockchainProof(id));
        } catch (RuntimeException e) {
            return Result.error(404, e.getMessage());
        }
    }
}
