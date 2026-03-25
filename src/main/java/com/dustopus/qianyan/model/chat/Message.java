package com.dustopus.qianyan.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * SSE 消息模型
 * 用于 Server-Sent Events 流式响应的数据封装
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息类型 (data / error / done)
     */
    private String type;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 会话ID
     */
    private Long chatId;

    /**
     * 创建一条数据消息
     */
    public static Message data(String content, Long chatId) {
        return Message.builder()
                .type("data")
                .content(content)
                .chatId(chatId)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建一条错误消息
     */
    public static Message error(String content, Long chatId) {
        return Message.builder()
                .type("error")
                .content(content)
                .chatId(chatId)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建一条结束消息
     */
    public static Message done(Long chatId) {
        return Message.builder()
                .type("done")
                .content("")
                .chatId(chatId)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
