package com.dustopus.qianyan.controller;

import com.dustopus.qianyan.common.BaseResponse;
import com.dustopus.qianyan.common.ResultUtils;
import com.dustopus.qianyan.model.dto.KnowledgeRequest;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识库管理控制器
 * 支持动态知识注入：写入本地文档 + 向量数据库
 */
@RestController
@RequestMapping("/knowledge")
@Slf4j
public class KnowledgeController {

    @Resource
    private EmbeddingStoreIngestor embeddingStoreIngestor;

    @Value("${rag.docs-path}")
    private String docsPath;

    private static final String DEFAULT_FILENAME = "QianYan.md";

    /**
     * 列出知识库文档
     */
    @GetMapping("/list")
    public BaseResponse<List<String>> listDocuments() {
        try {
            Path dirPath = Paths.get(docsPath);
            if (!Files.exists(dirPath)) {
                return ResultUtils.success(List.of());
            }
            List<String> files = Files.list(dirPath)
                    .filter(p -> p.toString().endsWith(".md"))
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList());
            return ResultUtils.success(files);
        } catch (IOException e) {
            log.error("列出文档失败", e);
            return ResultUtils.error(50001, "列出文档失败: " + e.getMessage());
        }
    }

    /**
     * 插入知识点（同时写入文件和向量库）
     */
    @PostMapping("/insert")
    public BaseResponse<String> insertKnowledge(@RequestBody KnowledgeRequest knowledgeRequest) {
        String sourceName = (knowledgeRequest.getSourceName() != null && !knowledgeRequest.getSourceName().isBlank())
                ? knowledgeRequest.getSourceName() : DEFAULT_FILENAME;

        // 1. 格式化内容
        String formattedContent = String.format("### Q：%s\n\nA：%s",
                knowledgeRequest.getQuestion(), knowledgeRequest.getAnswer());

        // 2. 写入物理文件
        boolean writeSuccess = appendToFile(formattedContent, sourceName);
        if (!writeSuccess) {
            return ResultUtils.error(50001, "插入失败：无法写入本地文件");
        }

        // 3. 存入向量数据库
        try {
            Metadata metadata = Metadata.from("file_name", sourceName);
            Document document = Document.from(formattedContent, metadata);
            embeddingStoreIngestor.ingest(document);

            log.info("RAG - 新增知识点成功: {}", knowledgeRequest.getQuestion());
            return ResultUtils.success("插入成功：已同步至 " + sourceName + " 及向量数据库");
        } catch (Exception e) {
            log.error("RAG - 向量化失败", e);
            return ResultUtils.error(50001, "文件已写入，但向量库更新失败: " + e.getMessage());
        }
    }

    private synchronized boolean appendToFile(String content, String fileName) {
        try {
            Path filePath = Paths.get(docsPath, fileName);
            log.info("文件写入位置: {}", filePath.toAbsolutePath());

            if (!Files.exists(filePath)) {
                if (filePath.getParent() != null) {
                    Files.createDirectories(filePath.getParent());
                }
                Files.createFile(filePath);
            }

            String textToAppend = "\n\n" + content;
            Files.writeString(filePath, textToAppend,
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            return true;
        } catch (IOException e) {
            log.error("写入本地文件失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
