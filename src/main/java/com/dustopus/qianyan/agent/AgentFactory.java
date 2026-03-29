package com.dustopus.qianyan.agent;

import com.dustopus.qianyan.Monitor.AiModelMonitorListener;
import com.dustopus.qianyan.config.ApiKeyProvider;
import com.dustopus.qianyan.tool.EmailTool;
import com.dustopus.qianyan.tool.RagTool;
import com.dustopus.qianyan.tool.TimeTool;
import com.dustopus.qianyan.tool.WeatherTool;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Agent 工厂
 * 负责根据当前 API Key 动态构建 Agent 实例，支持运行时 Key 切换
 */
@Component
@Slf4j
public class AgentFactory {

    @Value("${langchain4j.community.dashscope.chat-model.model-name}")
    private String modelName;

    @Resource
    private ApiKeyProvider apiKeyProvider;

    @Resource
    private McpToolProvider mcpToolProvider;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    private ContentRetriever contentRetriever;

    @Resource
    private RagTool ragTool;

    @Resource
    private EmailTool emailTool;

    @Resource
    private WeatherTool weatherTool;

    @Resource
    private AiModelMonitorListener aiModelMonitorListener;

    /**
     * 缓存的 Agent 实例
     */
    private volatile Agent cachedAgent;

    /**
     * 上次使用的 API Key（用于判断是否需要重建）
     */
    private volatile String lastApiKey;

    /**
     * 获取 Agent 实例
     * 如果 API Key 变更，自动重建 Agent
     */
    public synchronized Agent getAgent() {
        String currentKey = apiKeyProvider.getKey();

        if (cachedAgent == null || !currentKey.equals(lastApiKey)) {
            log.info("AgentFactory - 正在构建 Agent（Key 变更检测: {}）", !currentKey.equals(lastApiKey));
            cachedAgent = buildAgent(currentKey);
            lastApiKey = currentKey;
            log.info("AgentFactory - Agent 构建完成，模型: {}", modelName);
        }

        return cachedAgent;
    }

    /**
     * 强制重建 Agent（用户更新 Key 后调用）
     */
    public synchronized Agent rebuildAgent() {
        String currentKey = apiKeyProvider.getKey();
        log.info("AgentFactory - 强制重建 Agent");
        cachedAgent = buildAgent(currentKey);
        lastApiKey = currentKey;
        return cachedAgent;
    }

    /**
     * 构建 Agent 实例
     */
    private Agent buildAgent(String apiKey) {
        // 1. 创建 ChatModel
        ChatModel chatModel = QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .listeners(List.of(aiModelMonitorListener))
                .build();

        // 2. 创建 StreamingChatModel
        StreamingChatModel streamingChatModel = QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .listeners(List.of(aiModelMonitorListener))
                .build();

        // 3. 通过 AiServices 构建 Agent
        return AiServices.builder(Agent.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .contentRetriever(contentRetriever)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory
                        .builder()
                        .id(memoryId)
                        .chatMemoryStore(redisChatMemoryStore)
                        .maxMessages(20)
                        .build())
                .tools(new TimeTool(), ragTool, emailTool, weatherTool)
                .toolProvider(mcpToolProvider)
                .build();
    }
}
