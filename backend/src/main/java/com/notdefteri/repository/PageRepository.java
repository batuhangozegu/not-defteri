package com.notdefteri.repository;

import com.notdefteri.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PageRepository extends JpaRepository<Page, UUID> {

    Optional<Page> findByIdAndOwnerId(UUID id, UUID ownerId);

    List<Page> findByOwnerIdAndParentIsNullOrderByCreatedAtAsc(UUID ownerId);

    List<Page> findByOwnerIdAndParentIdOrderByCreatedAtAsc(UUID ownerId, UUID parentId);

    List<Page> findByOwnerIdAndTitleContainingIgnoreCaseOrderByTitleAsc(UUID ownerId, String title);
}
