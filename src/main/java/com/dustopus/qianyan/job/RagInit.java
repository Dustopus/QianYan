package com.dustopus.qianyan.job;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * RAG 数据初始化加载器
 * 在应用启动时自动扫描并加载指定目录下的文档到向量数据库
 */
@Component
@Slf4j
public class RagInit implements CommandLineRunner {

    @Resource
    private EmbeddingStoreIngestor embeddingStoreIngestor;

    @Value("${rag.docs-path}")
    private String docsPath;

    @Override
    public void run(String... args) {
        log.info("=== RAG 数据初始化开始 ===");
        log.info("文档目录: {}", docsPath);

        Path docDir = Paths.get(docsPath);
        if (!Files.exists(docDir)) {
            log.warn("文档目录不存在，跳过初始化: {}", docsPath);
            return;
        }

        int loadedCount = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(docDir, "*.md")) {
            for (Path filePath : stream) {
                try {
                    String content = Files.readString(filePath);
                    String fileName = filePath.getFileName().toString();

                    if (content.isBlank()) {
                        log.warn("跳过空文件: {}", fileName);
                        continue;
                    }

                    Metadata metadata = Metadata.from("file_name", fileName);
                    Document document = Document.from(content, metadata);
                    embeddingStoreIngestor.ingest(document);

                    loadedCount++;
                    log.info("加载文档成功: {} ({} bytes)", fileName, content.length());
                } catch (Exception e) {
                    log.error("加载文档失败: {}", filePath.getFileName(), e);
                }
            }
        } catch (IOException e) {
            log.error("扫描文档目录失败", e);
        }

        log.info("=== RAG 数据初始化完成，共加载 {} 个文档 ===", loadedCount);
    }
}
