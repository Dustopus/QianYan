package com.dustopus.qianyan.controller;

import com.dustopus.qianyan.agent.AgentFactory;
import com.dustopus.qianyan.common.BaseResponse;
import com.dustopus.qianyan.common.ResultUtils;
import com.dustopus.qianyan.config.ApiKeyProvider;
import com.dustopus.qianyan.model.dto.ApiKeyDto;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API Key 管理控制器
 * 支持前端动态输入和管理 Qwen API Key
 */
@RestController
@RequestMapping("/apikey")
@Slf4j
public class ApiKeyController {

    @Resource
    private ApiKeyProvider apiKeyProvider;

    @Resource
    private AgentFactory agentFactory;

    /**
     * 获取当前 API Key 状态
     */
    @GetMapping("/status")
    public BaseResponse<ApiKeyDto> getStatus() {
        ApiKeyDto dto = new ApiKeyDto();
        dto.setUsingCustomKey(apiKeyProvider.isUsingCustomKey());
        dto.setHasValidKey(apiKeyProvider.hasValidKey());

        String key = apiKeyProvider.getKey();
        if (key != null && !key.isBlank()) {
            dto.setMaskedKey(ApiKeyDto.maskKey(key));
        } else {
            dto.setMaskedKey("未配置");
        }

        return ResultUtils.success(dto);
    }

    /**
     * 更新 API Key
     * 更新后会自动重建 Agent，后续请求使用新 Key
     */
    @PostMapping("/update")
    public BaseResponse<ApiKeyDto> updateApiKey(@RequestBody ApiKeyDto request) {
        String newKey = request.getApiKey();

        if (newKey == null || newKey.isBlank()) {
            return com.dustopus.qianyan.common.ResultUtils.error(40000, "API Key 不能为空");
        }

        if (newKey.length() < 10) {
            return com.dustopus.qianyan.common.ResultUtils.error(40000, "API Key 格式不正确，请检查后重试");
        }

        log.info("正在更新 API Key（脱敏: {}）", ApiKeyDto.maskKey(newKey));

        // 1. 更新 Key
        apiKeyProvider.setKey(newKey);

        // 2. 重建 Agent（使用新 Key）
        try {
            agentFactory.rebuildAgent();
            log.info("API Key 更新成功，Agent 已重建");
        } catch (Exception e) {
            log.error("Agent 重建失败", e);
            return com.dustopus.qianyan.common.ResultUtils.error(50000, "Key 更新失败: " + e.getMessage());
        }

        // 3. 返回更新后的状态
        ApiKeyDto dto = new ApiKeyDto();
        dto.setUsingCustomKey(true);
        dto.setHasValidKey(true);
        dto.setMaskedKey(ApiKeyDto.maskKey(newKey));

        return ResultUtils.success(dto);
    }

    /**
     * 恢复为默认 API Key
     */
    @PostMapping("/reset")
    public BaseResponse<ApiKeyDto> resetApiKey() {
        log.info("恢复为默认 API Key");

        apiKeyProvider.resetToDefault();

        try {
            agentFactory.rebuildAgent();
        } catch (Exception e) {
            log.error("Agent 重建失败", e);
            return com.dustopus.qianyan.common.ResultUtils.error(50000, "恢复默认 Key 失败: " + e.getMessage());
        }

        ApiKeyDto dto = new ApiKeyDto();
        dto.setUsingCustomKey(false);
        dto.setHasValidKey(apiKeyProvider.hasValidKey());
        dto.setMaskedKey(ApiKeyDto.maskKey(apiKeyProvider.getKey()));

        return ResultUtils.success(dto);
    }
}
