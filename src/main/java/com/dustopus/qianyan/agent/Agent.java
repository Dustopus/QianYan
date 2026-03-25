package com.dustopus.qianyan.agent;

import com.dustopus.qianyan.guardrail.SafeInputGuardrail;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import reactor.core.publisher.Flux;

/**
 * 千言 AI Agent 核心接口
 * 基于 LangChain4j AiServices 构建，支持同步对话与流式输出
 */
@InputGuardrails({SafeInputGuardrail.class})
public interface Agent {

    /**
     * 同步对话
     *
     * @param memoryId 会话记忆ID（用于多用户/多会话隔离）
     * @param message  用户消息
     * @return AI 回复
     */
    @SystemMessage(fromResource = "system-prompt/chat-bot.txt")
    String chat(@MemoryId Long memoryId, @UserMessage String message);

    /**
     * 流式对话（SSE）
     *
     * @param memoryId 会话记忆ID
     * @param message  用户消息
     * @return Flux 流式响应
     */
    @SystemMessage(fromResource = "system-prompt/chat-bot.txt")
    Flux<String> streamChat(@MemoryId Long memoryId, @UserMessage String message);
}
