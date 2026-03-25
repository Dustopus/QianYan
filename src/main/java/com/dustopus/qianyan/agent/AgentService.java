package com.dustopus.qianyan.agent;

import com.dustopus.qianyan.tool.EmailTool;
import com.dustopus.qianyan.tool.RagTool;
import com.dustopus.qianyan.tool.TimeTool;
import com.dustopus.qianyan.tool.WeatherTool;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Agent 服务配置类
 * 负责构建 LangChain4j AiServices 实例，集成工具、记忆、RAG、MCP 等能力
 */
@Configuration
public class AgentService {

    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

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

    @Bean
    public Agent agent() {
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
