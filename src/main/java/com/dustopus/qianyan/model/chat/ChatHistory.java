package com.dustopus.qianyan.model.chat;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天历史记录模型
 * 封装一条完整的会话记录，包含用户消息、AI 回复及系统提示
 */
@Data
@NoArgsConstructor
public class ChatHistory {

    /**
     * 用户输入的消息
     */
    private String userMessage;

    /**
     * AI 的回复
     */
    private String aiMessage;

    /**
     * 系统提示（可选）
     */
    private String systemMessage;

    /**
     * 会话ID
     */
    private Long chatId;

    /**
     * 时间戳
     */
    private Long timestamp;

    public ChatHistory(String userMessage, String aiMessage, Long chatId) {
        this.userMessage = userMessage;
        this.aiMessage = aiMessage;
        this.chatId = chatId;
        this.timestamp = System.currentTimeMillis();
    }

    public ChatHistory(String userMessage, String aiMessage, String systemMessage, Long chatId) {
        this.userMessage = userMessage;
        this.aiMessage = aiMessage;
        this.systemMessage = systemMessage;
        this.chatId = chatId;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 转换为 LangChain4j ChatMessage 列表
     */
    public List<ChatMessage> toChatMessages() {
        List<ChatMessage> messages = new ArrayList<>();
        if (systemMessage != null && !systemMessage.isBlank()) {
            messages.add(new SystemMessage(systemMessage));
        }
        if (userMessage != null && !userMessage.isBlank()) {
            messages.add(new UserMessage(userMessage));
        }
        if (aiMessage != null && !aiMessage.isBlank()) {
            messages.add(new AiMessage(aiMessage));
        }
        return messages;
    }
}
