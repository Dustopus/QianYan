package com.dustopus.qianyan.controller;

import com.dustopus.qianyan.Monitor.MonitorContext;
import com.dustopus.qianyan.Monitor.MonitorContextHolder;
import com.dustopus.qianyan.agent.Agent;
import com.dustopus.qianyan.agent.AgentFactory;
import com.dustopus.qianyan.common.BaseResponse;
import com.dustopus.qianyan.common.ResultUtils;
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
 * 同时提供同步对话接口
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class StreamFluxChatController {

    @Resource
    private AgentFactory agentFactory;

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

        if (memoryId == null || message == null || message.isBlank()) {
            return Flux.just("参数错误：memoryId 和 message 不能为空");
        }

        MonitorContext context = MonitorContext.builder()
                .userId(chatRequest.getUserId())
                .sessionId(chatRequest.getSessionId() != null ? chatRequest.getSessionId() : memoryId)
                .build();

        log.info("SSE 流式聊天请求 - memoryId: {}, message: {}", memoryId, message);

        return Flux.defer(() -> {
            MonitorContextHolder.setContext(context);
            Agent agent = agentFactory.getAgent();
            return agent.streamChat(memoryId, message)
                    .doFinally(signal -> MonitorContextHolder.clearContext());
        });
    }

    /**
     * 同步聊天接口
     *
     * @param chatRequest 聊天请求
     * @return AI 回复
     */
    @PostMapping("/chat")
    public BaseResponse<String> chat(@RequestBody ChatRequest chatRequest) {
        Long memoryId = chatRequest.getActualMemoryId();
        String message = chatRequest.getActualMessage();

        if (memoryId == null || message == null || message.isBlank()) {
            return ResultUtils.error(40000, "参数错误：memoryId 和 message 不能为空");
        }

        MonitorContext context = MonitorContext.builder()
                .userId(chatRequest.getUserId())
                .sessionId(chatRequest.getSessionId() != null ? chatRequest.getSessionId() : memoryId)
                .build();

        log.info("同步聊天请求 - memoryId: {}, message: {}", memoryId, message);

        MonitorContextHolder.setContext(context);
        try {
            Agent agent = agentFactory.getAgent();
            String reply = agent.chat(memoryId, message);
            return ResultUtils.success(reply);
        } catch (Exception e) {
            log.error("聊天请求处理失败", e);
            return ResultUtils.error(50000, "聊天请求处理失败: " + e.getMessage());
        } finally {
            MonitorContextHolder.clearContext();
        }
    }
}
