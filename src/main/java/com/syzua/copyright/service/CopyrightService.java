package com.syzua.copyright.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syzua.copyright.dto.CopyrightRegisterRequest;
import com.syzua.copyright.entity.CopyrightRecord;
import com.syzua.copyright.entity.User;
import com.syzua.copyright.mapper.CopyrightRecordMapper;
import com.syzua.copyright.utils.HashUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CopyrightService {

    private final CopyrightRecordMapper copyrightMapper;
    private final UserService userService;

    public CopyrightService(CopyrightRecordMapper copyrightMapper, UserService userService) {
        this.copyrightMapper = copyrightMapper;
        this.userService = userService;
    }

    public CopyrightRecord register(CopyrightRegisterRequest request, Long userId) {
        String contentHash = HashUtils.sha256(request.getContent());
        long timestamp = System.currentTimeMillis();

        LambdaQueryWrapper<CopyrightRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CopyrightRecord::getContentHash, contentHash);
        wrapper.eq(CopyrightRecord::getDeleted, 0);
        if (copyrightMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该作品已登记版权，不可重复登记");
        }

        CopyrightRecord record = new CopyrightRecord();
        record.setTitle(request.getTitle());
        record.setAuthorName(request.getAuthorName());
        record.setUserId(userId);
        record.setWorkType(request.getWorkType() != null ? request.getWorkType() : "TEXT");
        record.setContentHash(contentHash);
        record.setBlockHash(HashUtils.generateBlockHash(contentHash, request.getAuthorName(), timestamp));
        record.setTimestamp(timestamp);
        record.setStatus("REGISTERED");
        record.setDescription(request.getDescription());
        copyrightMapper.insert(record);

        record.setRegNo(HashUtils.generateRegNo(timestamp, record.getId()));
        copyrightMapper.updateById(record);

        return record;
    }

    public Map<String, Object> verify(String content) {
        String contentHash = HashUtils.sha256(content);

        LambdaQueryWrapper<CopyrightRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CopyrightRecord::getContentHash, contentHash);
        wrapper.eq(CopyrightRecord::getStatus, "REGISTERED");
        CopyrightRecord record = copyrightMapper.selectOne(wrapper);

        Map<String, Object> result = new HashMap<>();
        if (record != null) {
            result.put("verified", true);
            result.put("regNo", record.getRegNo());
            result.put("title", record.getTitle());
            result.put("authorName", record.getAuthorName());
            result.put("blockHash", record.getBlockHash());
            result.put("timestamp", record.getTimestamp());
            result.put("message", "版权验证通过，该作品已登记");
        } else {
            result.put("verified", false);
            result.put("message", "该作品未登记版权");
        }
        return result;
    }

    public CopyrightRecord getByRegNo(String regNo) {
        LambdaQueryWrapper<CopyrightRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CopyrightRecord::getRegNo, regNo);
        CopyrightRecord record = copyrightMapper.selectOne(wrapper);
        if (record == null) {
            throw new RuntimeException("未找到登记号为 " + regNo + " 的版权记录");
        }
        return record;
    }

    public Page<CopyrightRecord> listByUser(Long userId, int page, int size) {
        Page<CopyrightRecord> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<CopyrightRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CopyrightRecord::getUserId, userId);
        wrapper.orderByDesc(CopyrightRecord::getCreateTime);
        return copyrightMapper.selectPage(pageObj, wrapper);
    }

    public Page<CopyrightRecord> search(String keyword, int page, int size) {
        Page<CopyrightRecord> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<CopyrightRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(CopyrightRecord::getTitle, keyword)
               .or()
               .like(CopyrightRecord::getAuthorName, keyword)
               .orderByDesc(CopyrightRecord::getCreateTime);
        return copyrightMapper.selectPage(pageObj, wrapper);
    }

    public String getBlockchainProof(Long id) {
        CopyrightRecord record = copyrightMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("版权记录不存在");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== 版权存证证明 ===\n");
        sb.append("登记号: ").append(record.getRegNo()).append("\n");
        sb.append("作品标题: ").append(record.getTitle()).append("\n");
        sb.append("作者: ").append(record.getAuthorName()).append("\n");
        sb.append("作品类型: ").append(record.getWorkType()).append("\n");
        sb.append("内容哈希(SHA-256): ").append(record.getContentHash()).append("\n");
        sb.append("区块哈希: ").append(record.getBlockHash()).append("\n");
        sb.append("存证时间戳: ").append(record.getTimestamp()).append("\n");
        sb.append("状态: ").append(record.getStatus()).append("\n");
        sb.append("=== 验证说明 ===\n");
        sb.append("1. 内容哈希由作品原始内容经SHA-256算法生成\n");
        sb.append("2. 区块哈希由内容哈希+作者+时间戳经SHA-256再次加密生成\n");
        sb.append("3. 任何原始内容篡改将导致哈希不匹配，验证失败\n");
        return sb.toString();
    }
}
