package com.dustopus.qianyan.model.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * API Key 请求/响应模型
 * 用于前端提交和获取 Qwen API Key 状态
 */
@Data
public class ApiKeyDto implements Serializable {

    /**
     * API Key 值
     */
    private String apiKey;

    /**
     * 是否使用自定义 Key（响应用）
     */
    private Boolean usingCustomKey;

    /**
     * 是否有有效 Key（响应用）
     */
    private Boolean hasValidKey;

    /**
     * 脱敏后的 Key（响应用，仅显示前4位和后4位）
     */
    private String maskedKey;

    /**
     * 对 API Key 进行脱敏处理
     */
    public static String maskKey(String key) {
        if (key == null || key.length() < 12) {
            return "****";
        }
        return key.substring(0, 4) + "****" + key.substring(key.length() - 4);
    }
}
