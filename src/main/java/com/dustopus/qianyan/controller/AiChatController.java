package com.dustopus.qianyan.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 聊天控制器
 * 知识注入功能已迁移至 KnowledgeController (/api/knowledge/*)
 * 聊天功能由 StreamFluxChatController 统一提供 (/api/ai/*)
 *
 * @see KnowledgeController
 * @see StreamFluxChatController
 */
@RestController
@Slf4j
public class AiChatController {

}
