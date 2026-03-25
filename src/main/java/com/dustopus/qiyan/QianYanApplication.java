package com.dustopus.qiyan;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
public class QianYanApplication {

    public static void main(String[] args) {
        SpringApplication.run(QianYanApplication.class, args);
    }
}
