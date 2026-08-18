package com.notdefteri.controller;

import com.notdefteri.dto.BlockDto;
import com.notdefteri.dto.BlocksReplaceRequest;
import com.notdefteri.service.BlockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pages/{pageId}/blocks")
public class BlockController {

    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @GetMapping
    public List<BlockDto> list(@PathVariable UUID pageId) {
        return blockService.getBlocks(pageId);
    }

    @PutMapping
    public List<BlockDto> replace(@PathVariable UUID pageId, @Valid @RequestBody BlocksReplaceRequest request) {
        return blockService.replaceBlocks(pageId, request.blocks());
    }

    @DeleteMapping("/{blockId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID pageId, @PathVariable UUID blockId) {
        blockService.deleteBlock(pageId, blockId);
    }
}
