package com.notdefteri.service;

import com.notdefteri.domain.Block;
import com.notdefteri.domain.Page;
import com.notdefteri.dto.BlockDto;
import com.notdefteri.exception.NotFoundException;
import com.notdefteri.repository.BlockRepository;
import com.notdefteri.repository.PageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class BlockService {

    private final BlockRepository blockRepository;
    private final PageRepository pageRepository;
    private final EmbeddingService embeddingService;

    public BlockService(BlockRepository blockRepository, PageRepository pageRepository,
                         EmbeddingService embeddingService) {
        this.blockRepository = blockRepository;
        this.pageRepository = pageRepository;
        this.embeddingService = embeddingService;
    }

    @Transactional(readOnly = true)
    public List<BlockDto> getBlocks(UUID pageId) {
        return blockRepository.findByPageIdOrderByOrderIndexAsc(pageId).stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Sayfanın tüm bloklarını verilen sırayla değiştirir: id'si gelenler güncellenir,
     * id'si olmayanlar oluşturulur, listede artık bulunmayan mevcut bloklar silinir.
     * İşlem bitince embedding'i arka planda yeniden üretir.
     */
    @Transactional
    public List<BlockDto> replaceBlocks(UUID pageId, List<BlockDto> incoming) {
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new NotFoundException("Sayfa bulunamadı: " + pageId));

        List<Block> existing = blockRepository.findByPageIdOrderByOrderIndexAsc(pageId);
        Set<UUID> keepIds = new HashSet<>();

        List<Block> saved = incoming.stream().map(dto -> {
            Block block = (dto.id() != null)
                    ? existing.stream().filter(b -> b.getId().equals(dto.id())).findFirst()
                        .orElseThrow(() -> new NotFoundException("Blok bulunamadı: " + dto.id()))
                    : new Block();
            block.setPage(page);
            block.setType(dto.type());
            block.setContent(dto.content());
            block.setOrderIndex(dto.orderIndex());
            block.setChecked(dto.type() == com.notdefteri.domain.BlockType.TODO ? Boolean.TRUE.equals(dto.checked()) : null);
            Block result = blockRepository.save(block);
            keepIds.add(result.getId());
            return result;
        }).toList();

        existing.stream()
                .filter(b -> !keepIds.contains(b.getId()))
                .forEach(blockRepository::delete);

        embeddingService.reindexPage(pageId);
        return saved.stream().map(this::toDto).toList();
    }

    @Transactional
    public void deleteBlock(UUID pageId, UUID blockId) {
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new NotFoundException("Blok bulunamadı: " + blockId));
        if (!block.getPage().getId().equals(pageId)) {
            throw new NotFoundException("Blok bu sayfaya ait değil: " + blockId);
        }
        blockRepository.delete(block);
        embeddingService.reindexPage(pageId);
    }

    private BlockDto toDto(Block block) {
        return new BlockDto(block.getId(), block.getType(), block.getContent(), block.getOrderIndex(), block.getChecked());
    }
}
