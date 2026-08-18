package com.notdefteri.service;

import com.notdefteri.domain.Page;
import com.notdefteri.dto.PageResponseDto;
import com.notdefteri.dto.PageTreeDto;
import com.notdefteri.dto.PageUpsertRequest;
import com.notdefteri.exception.NotFoundException;
import com.notdefteri.repository.BlockRepository;
import com.notdefteri.repository.PageEmbeddingRepository;
import com.notdefteri.repository.PageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PageService {

    private final PageRepository pageRepository;
    private final BlockRepository blockRepository;
    private final PageEmbeddingRepository pageEmbeddingRepository;
    private final EmbeddingService embeddingService;

    public PageService(PageRepository pageRepository,
                        BlockRepository blockRepository,
                        PageEmbeddingRepository pageEmbeddingRepository,
                        EmbeddingService embeddingService) {
        this.pageRepository = pageRepository;
        this.blockRepository = blockRepository;
        this.pageEmbeddingRepository = pageEmbeddingRepository;
        this.embeddingService = embeddingService;
    }

    @Transactional(readOnly = true)
    public List<PageTreeDto> getTree() {
        return pageRepository.findByParentIsNullOrderByCreatedAtAsc().stream()
                .map(this::toTree)
                .toList();
    }

    private PageTreeDto toTree(Page page) {
        List<PageTreeDto> children = pageRepository.findByParentIdOrderByCreatedAtAsc(page.getId()).stream()
                .map(this::toTree)
                .toList();
        return new PageTreeDto(page.getId(), page.getTitle(), page.getIcon(), page.getUpdatedAt(), children);
    }

    @Transactional(readOnly = true)
    public PageResponseDto getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public PageResponseDto create(PageUpsertRequest request) {
        Page page = new Page();
        page.setTitle(request.title());
        page.setIcon(request.icon());
        if (request.parentId() != null) {
            page.setParent(findOrThrow(request.parentId()));
        }
        page = pageRepository.save(page);
        embeddingService.reindexPage(page.getId());
        return toResponse(page);
    }

    @Transactional
    public PageResponseDto update(UUID id, PageUpsertRequest request) {
        Page page = findOrThrow(id);
        page.setTitle(request.title());
        page.setIcon(request.icon());
        if (request.parentId() == null) {
            page.setParent(null);
        } else if (!request.parentId().equals(id)) {
            page.setParent(findOrThrow(request.parentId()));
        }
        page = pageRepository.save(page);
        embeddingService.reindexPage(page.getId());
        return toResponse(page);
    }

    @Transactional
    public void delete(UUID id) {
        Page page = findOrThrow(id);
        pageRepository.findByParentIdOrderByCreatedAtAsc(id).forEach(child -> delete(child.getId()));
        blockRepository.deleteByPageId(id);
        pageEmbeddingRepository.deleteByPageId(id);
        pageRepository.delete(page);
    }

    @Transactional(readOnly = true)
    public List<PageResponseDto> search(String query) {
        return pageRepository.findByTitleContainingIgnoreCaseOrderByTitleAsc(query).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Page findOrThrow(UUID id) {
        return pageRepository.findById(id)
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
