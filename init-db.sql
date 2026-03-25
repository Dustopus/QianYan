-- QianYan 数据库初始化脚本
-- 启用 pgvector 扩展（用于向量存储与检索）
CREATE EXTENSION IF NOT EXISTS vector;

-- 创建向量存储表（PgVectorEmbeddingStore 会自动管理表结构，此表仅供参考）
-- 实际表结构由 LangChain4j PgVectorEmbeddingStore 在首次启动时自动创建
-- DROP TABLE IF EXISTS agent_embedding;
-- CREATE TABLE agent_embedding (
--     id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
--     embedding vector(1024),
--     text TEXT,
--     metadata JSONB
-- );

-- 为向量列创建 HNSW 索引以加速相似度搜索（数据量大时启用）
-- CREATE INDEX IF NOT EXISTS idx_agent_embedding_hnsw
--     ON agent_embedding USING hnsw (embedding vector_cosine_ops);

-- 提示：首次启动时请确保 pgvector 扩展已安装
-- 如需手动建表，请取消上方注释
