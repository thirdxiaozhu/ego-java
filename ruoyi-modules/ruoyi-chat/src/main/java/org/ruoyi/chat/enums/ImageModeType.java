package org.ruoyi.chat.enums;


import lombok.Getter;

@Getter
public enum ImageModeType {
    Qwen("qwen", "通义千问");

    private final String code;
    private final String description;

    ImageModeType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
