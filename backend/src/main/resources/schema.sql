-- pgvector eklentisi Hibernate şema oluşturmadan önce hazır olmalı,
-- çünkü page_embedding.embedding kolonu "vector(768)" tipindedir.
CREATE EXTENSION IF NOT EXISTS vector;
