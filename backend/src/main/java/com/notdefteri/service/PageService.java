package com.notdefteri.service;

import com.notdefteri.domain.Page;
import com.notdefteri.dto.PageResponseDto;
import com.notdefteri.dto.PageTreeDto;
import com.notdefteri.dto.PageUpsertRequest;
import com.notdefteri.exception.NotFoundException;
import com.notdefteri.repository.BlockRepository;
import com.notdefteri.repository.PageEmbeddingRepository;
import com.notdefteri.repository.PageRepository;
import com.notdefteri.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Sayfa CRUD + hiyerarşi. Her metod {@code ownerId} alır ve sadece o kullanıcıya ait
 * sayfaları görür/değiştirir — {@link PageRepository#findByIdAndOwnerId} bulunamazsa
 * (ya da sayfa başka bir kullanıcıya aitse) {@link NotFoundException} fırlatır, böylece
 * bir kullanıcı başka bir kullanıcının sayfasının var olup olmadığını bile öğrenemez.
 */
@Service
public class PageService {

    private final PageRepository pageRepository;
    private final BlockRepository blockRepository;
    private final PageEmbeddingRepository pageEmbeddingRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PageService(PageRepository pageRepository,
                        BlockRepository blockRepository,
                        PageEmbeddingRepository pageEmbeddingRepository,
                        UserRepository userRepository,
                        ApplicationEventPublisher eventPublisher) {
        this.pageRepository = pageRepository;
        this.blockRepository = blockRepository;
        this.pageEmbeddingRepository = pageEmbeddingRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<PageTreeDto> getTree(UUID ownerId) {
        return pageRepository.findByOwnerIdAndParentIsNullOrderByCreatedAtAsc(ownerId).stream()
                .map(p -> toTree(ownerId, p))
                .toList();
    }

    private PageTreeDto toTree(UUID ownerId, Page page) {
        List<PageTreeDto> children = pageRepository.findByOwnerIdAndParentIdOrderByCreatedAtAsc(ownerId, page.getId()).stream()
                .map(p -> toTree(ownerId, p))
                .toList();
        return new PageTreeDto(page.getId(), page.getTitle(), page.getIcon(), page.getUpdatedAt(), children);
    }

    @Transactional(readOnly = true)
    public PageResponseDto getById(UUID ownerId, UUID id) {
        return toResponse(findOrThrow(ownerId, id));
    }

    @Transactional
    public PageResponseDto create(UUID ownerId, PageUpsertRequest request) {
        Page page = new Page();
        page.setTitle(request.title());
        page.setIcon(request.icon());
        page.setOwner(userRepository.getReferenceById(ownerId));
        if (request.parentId() != null) {
            page.setParent(findOrThrow(ownerId, request.parentId()));
        }
        page = pageRepository.saveAndFlush(page);
        eventPublisher.publishEvent(new PageChangedEvent(page.getId()));
        return toResponse(page);
    }

    @Transactional
    public PageResponseDto update(UUID ownerId, UUID id, PageUpsertRequest request) {
        Page page = findOrThrow(ownerId, id);
        page.setTitle(request.title());
        page.setIcon(request.icon());
        if (request.parentId() == null) {
            page.setParent(null);
        } else if (!request.parentId().equals(id)) {
            page.setParent(findOrThrow(ownerId, request.parentId()));
        }
        page = pageRepository.saveAndFlush(page);
        eventPublisher.publishEvent(new PageChangedEvent(page.getId()));
        return toResponse(page);
    }

    @Transactional
    public void delete(UUID ownerId, UUID id) {
        Page page = findOrThrow(ownerId, id);
        pageRepository.findByOwnerIdAndParentIdOrderByCreatedAtAsc(ownerId, id)
                .forEach(child -> delete(ownerId, child.getId()));
        blockRepository.deleteByPageId(id);
        pageEmbeddingRepository.deleteByPageId(id);
        pageRepository.delete(page);
    }

    @Transactional(readOnly = true)
    public List<PageResponseDto> search(UUID ownerId, String query) {
        return pageRepository.findByOwnerIdAndTitleContainingIgnoreCaseOrderByTitleAsc(ownerId, query).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Page findOrThrow(UUID ownerId, UUID id) {
        return pageRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException("Sayfa bulunamadı: " + id));
    }

    private PageResponseDto toResponse(Page page) {
        return new PageResponseDto(
                page.getId(),
                page.getTitle(),
                page.getIcon(),
                page.getParent() != null ? page.getParent().getId() : null,
                page.getCreatedAt(),
                page.getUpdatedAt()
        );
    }
}
