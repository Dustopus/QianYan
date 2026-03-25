package com.dustopus.qianyan.controller;

import com.dustopus.qianyan.model.dto.KnowledgeRequest;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * AI 聊天控制器（知识注入入口）
 * 聊天接口由 StreamFluxChatController 统一提供 (/api/ai/*)
 */
@RestController
@Slf4j
public class AiChatController {

    @Resource
    private EmbeddingStoreIngestor embeddingStoreIngestor;

    @Value("${rag.docs-path}")
    private String docsPath;

    private final String TARGET_FILENAME = "QianYan.md";

    /**
     * 知识注入接口（写入文件 + 向量库）
     * POST /api/insert
     */
    @PostMapping("/insert")
    public String insertKnowledge(@RequestBody KnowledgeRequest knowledgeRequest) {
        // 1. 格式化内容
        String formattedContent = String.format("### Q：%s\n\nA：%s",
                knowledgeRequest.getQuestion(), knowledgeRequest.getAnswer());

        // 2. 写入物理文件
        boolean writeSuccess = appendToFile(formattedContent, knowledgeRequest.getSourceName());
        if (!writeSuccess) {
            return "插入失败：无法写入本地文件";
        }

        // 3. 存入向量数据库 (RAG)
        try {
            String sourceName = (knowledgeRequest.getSourceName() != null)
                    ? knowledgeRequest.getSourceName() : TARGET_FILENAME;
            Metadata metadata = Metadata.from("file_name", sourceName);
            Document document = Document.from(formattedContent, metadata);
            embeddingStoreIngestor.ingest(document);

            log.info("RAG - 新增知识点成功: {}", knowledgeRequest.getQuestion());
            return "插入成功：已同步至 " + knowledgeRequest.getSourceName() + " 及向量数据库";
        } catch (Exception e) {
            log.error("RAG - 向量化失败", e);
            return "插入部分成功：文件已写入，但向量库更新失败";
        }
    }

    private synchronized boolean appendToFile(String content, String sourceName) {
        try {
            Path filePath = Paths.get(docsPath, sourceName);
            log.info("文件实际写入位置: {}", filePath.toAbsolutePath());

            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }

            String textToAppend = "\n\n" + content;

            Files.writeString(filePath, textToAppend,
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            return true;
        } catch (IOException e) {
            log.error("RAG - 写入本地文件失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
