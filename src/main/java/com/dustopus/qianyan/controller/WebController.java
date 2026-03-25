package com.dustopus.qianyan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 前端页面控制器
 * 提供静态 HTML 页面访问入口
 */
@Controller
public class WebController {

    /**
     * 默认首页 - 通义千问风格
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/front/qwen.html";
    }

    /**
     * GPT 风格聊天页面
     */
    @GetMapping("/gpt")
    public String gptPage() {
        return "redirect:/front/gpt.html";
    }

    /**
     * 通义千问风格聊天页面
     */
    @GetMapping("/qwen")
    public String qwenPage() {
        return "redirect:/front/qwen.html";
    }
}
