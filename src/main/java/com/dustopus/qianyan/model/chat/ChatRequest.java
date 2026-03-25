package com.dustopus.qianyan.model.chat;

import lombok.Data;

/**
 * 聊天请求模型
 * 用于前端发送聊天请求，包含会话ID、记忆ID和用户消息
 */
@Data
public class ChatRequest {

    /**
     * 会话唯一标识（用于区分不同会话）
     */
    private Long chatId;

    /**
     * 记忆ID（用于 LangChain4j ChatMemory 隔离）
     */
    private Long memoryId;

    /**
     * 用户ID（监控用）
     */
    private Long userId;

    /**
     * 会话ID（监控用，与 chatId 功能类似但用于监控上下文）
     */
    private Long sessionId;

    /**
     * 用户输入的消息内容
     */
    private String message;

    /**
     * 兼容旧版字段
     */
    private String prompt;

    /**
     * 获取实际用户消息内容
     */
    public String getActualMessage() {
        if (message != null && !message.isBlank()) {
            return message;
        }
        return prompt;
    }

    /**
     * 获取实际记忆ID
     */
    public Long getActualMemoryId() {
        if (memoryId != null) {
            return memoryId;
        }
        if (chatId != null) {
            return chatId;
        }
        return sessionId;
    }
}
