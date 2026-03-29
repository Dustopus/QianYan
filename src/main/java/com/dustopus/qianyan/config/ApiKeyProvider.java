package com.dustopus.qianyan.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

/**
 * API Key 管理器
 * 支持运行时动态切换 Qwen API Key，实现用户自定义额度管理
 */
@Component
@Slf4j
public class ApiKeyProvider {

    /**
     * 当前生效的 API Key（线程安全）
     */
    private final AtomicReference<String> currentKey = new AtomicReference<>();

    /**
     * 默认 API Key（来自配置文件）
     */
    private volatile String defaultKey;

    /**
     * 设置默认 Key（由配置类在启动时调用）
     */
    public void setDefaultKey(String key) {
        if (this.defaultKey == null) {
            this.defaultKey = key;
            this.currentKey.compareAndSet(null, key);
            log.info("ApiKeyProvider - 默认 API Key 已注册");
        }
    }

    /**
     * 更新为用户自定义 Key
     */
    public void setKey(String key) {
        this.currentKey.set(key);
        log.info("ApiKeyProvider - API Key 已更新（用户自定义）");
    }

    /**
     * 恢复为默认 Key
     */
    public void resetToDefault() {
        this.currentKey.set(defaultKey);
        log.info("ApiKeyProvider - 已恢复为默认 API Key");
    }

    /**
     * 获取当前生效的 API Key
     */
    public String getKey() {
        String key = currentKey.get();
        return (key != null && !key.isBlank()) ? key : defaultKey;
    }

    /**
     * 判断当前是否使用用户自定义 Key
     */
    public boolean isUsingCustomKey() {
        String key = currentKey.get();
        return key != null && !key.equals(defaultKey);
    }

    /**
     * 判断是否已有有效 Key
     */
    public boolean hasValidKey() {
        String key = getKey();
        return key != null && !key.isBlank() && !key.startsWith("your-");
    }
}
