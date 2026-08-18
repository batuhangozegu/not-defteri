package com.notdefteri.repository;

import com.notdefteri.dto.SimilarChunkDto;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * page_embedding tablosu için pgvector'a özel okuma/yazma işlemleri.
 *
 * Hibernate yerine düz JDBC kullanılır çünkü "vector" JDBC/PostgreSQL'in yerleşik bir
 * tipi değildir; pgvector eklentisi PGobject üzerinden metinsel "[0.1,0.2,...]" temsilini
 * kabul eder.
 */
@Repository
public class PageEmbeddingRepository {

    private final JdbcTemplate jdbcTemplate;

    public PageEmbeddingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteByPageId(UUID pageId) {
        jdbcTemplate.update("DELETE FROM page_embedding WHERE page_id = ?", pageId);
    }

    public void insert(UUID pageId, String chunkText, float[] embedding) {
        jdbcTemplate.update(
                "INSERT INTO page_embedding (id, page_id, chunk_text, embedding, created_at) " +
                        "VALUES (gen_random_uuid(), ?, ?, ?, now())",
                pageId, chunkText, toPgVector(embedding));
    }

    /**
     * Cosine mesafesine (pgvector "<=>" operatörü) göre, SADECE {@code ownerId} kullanıcısına
     * ait sayfalar arasında en yakın {@code limit} parçayı döner. Bu filtre olmadan bir
     * kullanıcının sorusu başka bir kullanıcının notlarından bağlam sızdırabilir.
     */
    public List<SimilarChunkDto> findMostSimilar(UUID ownerId, float[] questionEmbedding, int limit) {
        return jdbcTemplate.query(
                "SELECT pe.page_id, p.title, pe.chunk_text, (pe.embedding <=> ?) AS distance " +
                        "FROM page_embedding pe JOIN page p ON p.id = pe.page_id " +
                        "WHERE p.owner_id = ? " +
                        "ORDER BY pe.embedding <=> ? " +
                        "LIMIT ?",
                (rs, rowNum) -> new SimilarChunkDto(
                        (UUID) rs.getObject("page_id"),
                        rs.getString("title"),
                        rs.getString("chunk_text"),
                        rs.getDouble("distance")),
                toPgVector(questionEmbedding), ownerId, toPgVector(questionEmbedding), limit);
    }

    private PGobject toPgVector(float[] embedding) {
        PGobject pgObject = new PGobject();
        pgObject.setType("vector");
        StringBuilder sb = new StringBuilder(embedding.length * 8 + 2);
        sb.append('[');
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(embedding[i]);
        }
        sb.append(']');
        try {
            pgObject.setValue(sb.toString());
        } catch (SQLException e) {
            throw new IllegalStateException("Vektör pgvector formatına çevrilemedi", e);
        }
        return pgObject;
    }
}
