package com.dustopus.qianyan.guardrail;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 输入安全护栏
 * 实现敏感词过滤与 Prompt 注入检测，保护 AI 系统安全
 */
public class SafeInputGuardrail implements InputGuardrail {

    /**
     * 敏感词列表
     */
    private static final List<String> SENSITIVE_WORDS = List.of(
            "死", "杀", "暴力", "色情", "赌博", "毒品"
    );

    /**
     * Prompt 注入攻击模式
     */
    private static final List<Pattern> INJECTION_PATTERNS = List.of(
            Pattern.compile("(?i)ignore\\s+(all\\s+)?previous\\s+instructions"),
            Pattern.compile("(?i)ignore\\s+above"),
            Pattern.compile("(?i)you\\s+are\\s+now\\s+DAN"),
            Pattern.compile("(?i)disregard\\s+(all\\s+)?prior"),
            Pattern.compile("(?i)forget\\s+(all\\s+)?instructions"),
            Pattern.compile("(?i)act\\s+as\\s+(a\\s+)?(?:unrestricted|unfiltered)"),
            Pattern.compile("(?i)system\\s*:\\s*"),
            Pattern.compile("(?i)\\[system\\]"),
            Pattern.compile("(?i)override\\s+safety"),
            Pattern.compile("(?i)jailbreak"),
            Pattern.compile("(?i)prompt\\s*injection"),
            // 角色扮演越狱模式
            Pattern.compile("(?i)pretend\\s+you\\s+(are|have)\\s+no\\s+(restrictions|rules|limits)"),
            Pattern.compile("(?i)do\\s+not\\s+follow\\s+(any\\s+)?rules")
    );

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String inputText = userMessage.singleText();

        // 1. 敏感词检测
        for (String keyword : SENSITIVE_WORDS) {
            if (!keyword.isEmpty() && inputText.contains(keyword)) {
                return fatal("提问不能包含敏感词！！！！！");
            }
        }

        // 2. Prompt 注入检测
        for (Pattern pattern : INJECTION_PATTERNS) {
            if (pattern.matcher(inputText).find()) {
                return fatal("检测到异常请求，已被安全系统拦截！");
            }
        }

        // 3. 输入长度检查（防止超长攻击）
        if (inputText.length() > 10000) {
            return fatal("输入内容过长，请精简后重新发送！");
        }

        return success();
    }
}
