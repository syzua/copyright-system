package com.syzua.copyright.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CopyrightRegisterRequest {

    @NotBlank(message = "作品标题不能为空")
    private String title;

    @NotBlank(message = "作者不能为空")
    private String authorName;

    @NotBlank(message = "作品内容不能为空")
    private String content;

    private String workType;

    private String description;
}
