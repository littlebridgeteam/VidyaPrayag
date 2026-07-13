-- ============================================================================
-- V3__enable_pgvector.sql — BFS-051: Enable pgvector for RAG vector search
-- ============================================================================
-- Enables the pgvector extension and converts the embedding column on
-- tutor_knowledge_chunks from text to vector(768) for cosine similarity search.
-- Idempotent: uses DO blocks to check preconditions.
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS vector;

DO $$ BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'tutor_knowledge_chunks'
          AND column_name = 'embedding'
          AND data_type = 'text'
    ) THEN
        ALTER TABLE tutor_knowledge_chunks
            ALTER COLUMN embedding TYPE vector(768) USING NULLIF(embedding, '')::vector(768);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE indexname = 'idx_tutor_kc_embedding_ivfflat'
    ) THEN
        CREATE INDEX idx_tutor_kc_embedding_ivfflat
            ON tutor_knowledge_chunks
            USING ivfflat (embedding vector_cosine_ops)
            WITH (lists = 100);
    END IF;
END $$;
