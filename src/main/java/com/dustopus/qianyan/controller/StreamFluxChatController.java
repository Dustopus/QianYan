package com.dustopus.qianyan.controller;

import com.dustopus.qianyan.agent.Agent;
import com.dustopus.qianyan.model.chat.ChatRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 流式对话控制器
 * 基于 SSE（Server-Sent Events）实现流式 AI 响应
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class StreamFluxChatController {

    @Resource
    private Agent agent;

    /**
     * 流式聊天接口（SSE）
     *
     * @param chatRequest 聊天请求
     * @return SSE 流式响应
     */
    @PostMapping(value = "/streamChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody ChatRequest chatRequest) {
        Long memoryId = chatRequest.getActualMemoryId();
        String message = chatRequest.getActualMessage();
        log.info("SSE 流式聊天请求 - memoryId: {}, message: {}", memoryId, message);
        return agent.streamChat(memoryId, message);
    }

    /**
     * 同步聊天接口
     *
     * @param chatRequest 聊天请求
     * @return AI 回复
     */
    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest chatRequest) {
        Long memoryId = chatRequest.getActualMemoryId();
        String message = chatRequest.getActualMessage();
        log.info("同步聊天请求 - memoryId: {}, message: {}", memoryId, message);
        return agent.chat(memoryId, message);
    }
}
