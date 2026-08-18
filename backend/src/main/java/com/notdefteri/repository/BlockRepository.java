package com.notdefteri.repository;

import com.notdefteri.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BlockRepository extends JpaRepository<Block, UUID> {

    List<Block> findByPageIdOrderByOrderIndexAsc(UUID pageId);

    void deleteByPageId(UUID pageId);
}
