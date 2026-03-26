package com.dustopus.qianyan.tool;

import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 天气查询工具
 * 通过 Function Calling 为 AI Agent 提供天气查询能力
 */
@Component
@Slf4j
public class WeatherTool {

    @Tool("查询指定城市的当前天气情况。传入城市名称，返回天气描述。")
    public String getWeather(String city) {
        log.info("Tool 调用: 查询天气 - 城市: {}", city);
        // 简单实现，实际可接入天气 API
        return "天气功能暂未接入外部 API，请稍后配置天气服务后使用。当前查询城市：" + city;
    }
}
