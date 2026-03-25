package com.dustopus.qianyan.constant;

/**
 * AI 相关常量
 */
public class AiConstant {

    /**
     * 默认模型名称
     */
    public static final String DEFAULT_MODEL_NAME = "qwen-max";

    /**
     * 默认嵌入模型名称
     */
    public static final String DEFAULT_EMBEDDING_MODEL_NAME = "text-embedding-v4";

    /**
     * 默认嵌入维度
     */
    public static final int DEFAULT_EMBEDDING_DIMENSION = 1024;

    /**
     * 聊天记忆窗口大小
     */
    public static final int CHAT_MEMORY_WINDOW_SIZE = 20;

    /**
     * RAG 检索结果数量
     */
    public static final int RAG_MAX_RESULTS = 5;

    /**
     * RAG 最小相似度分数
     */
    public static final double RAG_MIN_SCORE = 0.75;

    /**
     * SSE 事件分隔符
     */
    public static final String SSE_EVENT_DELIMITER = "\n\n";

    /**
     * 系统提示资源路径
     */
    public static final String SYSTEM_PROMPT_PATH = "system-prompt/chat-bot.txt";

    private AiConstant() {
    }
}
