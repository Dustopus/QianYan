package com.dustopus.qianyan.model.enums;

import lombok.Getter;

/**
 * 聊天历史消息类型枚举
 */
@Getter
public enum ChatHistoryTypeEnum {

    USER("user", "用户消息"),
    ASSISTANT("assistant", "AI 回复"),
    SYSTEM("system", "系统消息");

    private final String type;
    private final String description;

    ChatHistoryTypeEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }
}
