package com.notdefteri.repository;

import com.notdefteri.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PageRepository extends JpaRepository<Page, UUID> {

    List<Page> findByParentIsNullOrderByCreatedAtAsc();

    List<Page> findByParentIdOrderByCreatedAtAsc(UUID parentId);

    List<Page> findByTitleContainingIgnoreCaseOrderByTitleAsc(String title);
}
