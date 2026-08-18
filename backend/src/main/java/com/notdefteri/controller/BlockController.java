package com.notdefteri.controller;

import com.notdefteri.dto.BlockDto;
import com.notdefteri.dto.BlocksReplaceRequest;
import com.notdefteri.security.CurrentUser;
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
    private final CurrentUser currentUser;

    public BlockController(BlockService blockService, CurrentUser currentUser) {
        this.blockService = blockService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<BlockDto> list(@PathVariable UUID pageId) {
        return blockService.getBlocks(currentUser.id(), pageId);
    }

    @PutMapping
    public List<BlockDto> replace(@PathVariable UUID pageId, @Valid @RequestBody BlocksReplaceRequest request) {
        return blockService.replaceBlocks(currentUser.id(), pageId, request.blocks());
    }

    @DeleteMapping("/{blockId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID pageId, @PathVariable UUID blockId) {
        blockService.deleteBlock(currentUser.id(), pageId, blockId);
    }
}
