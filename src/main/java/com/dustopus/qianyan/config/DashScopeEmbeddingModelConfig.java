package com.dustopus.qianyan.config;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DashScope 嵌入模型配置
 * 配置 text-embedding-v4 用于 RAG 向量化
 */
@Configuration
public class DashScopeEmbeddingModelConfig {

    @Value("${langchain4j.community.dashscope.embedding-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.community.dashscope.embedding-model.model-name}")
    private String modelName;

    @Bean
    public EmbeddingModel embeddingModel() {
        return QwenEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }
}
