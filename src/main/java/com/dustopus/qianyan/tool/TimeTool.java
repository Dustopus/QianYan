package com.dustopus.qianyan.tool;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import dev.langchain4j.agent.tool.Tool;

/**
 * 时间查询工具
 * 通过 Function Calling 为 AI Agent 提供当前时间查询能力
 */
public class TimeTool {

    /**
     * 获取当前北京时间（上海时区）
     *
     * @return 格式化的时间字符串
     */
    @Tool("获取当前北京时间，包含日期、时间和星期几信息。不需要任何参数。")
    public String getCurrentTimeInShanghai() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEEE (中国标准时间)"));
    }
}
