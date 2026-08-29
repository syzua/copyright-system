package com.syzua.copyright.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("copyright_records")
public class CopyrightRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String regNo;

    private String title;

    private String authorName;

    private Long userId;

    private String workType;

    private String contentHash;

    private String blockHash;

    private Long timestamp;

    private String status;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
