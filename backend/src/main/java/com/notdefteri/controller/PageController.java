package com.notdefteri.controller;

import com.notdefteri.dto.PageResponseDto;
import com.notdefteri.dto.PageTreeDto;
import com.notdefteri.dto.PageUpsertRequest;
import com.notdefteri.security.CurrentUser;
import com.notdefteri.service.PageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pages")
public class PageController {

    private final PageService pageService;
    private final CurrentUser currentUser;

    public PageController(PageService pageService, CurrentUser currentUser) {
        this.pageService = pageService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<PageTreeDto> tree() {
        return pageService.getTree(currentUser.id());
    }

    @GetMapping("/search")
    public List<PageResponseDto> search(@RequestParam("q") String query) {
        return pageService.search(currentUser.id(), query);
    }

    @GetMapping("/{id}")
    public PageResponseDto get(@PathVariable UUID id) {
        return pageService.getById(currentUser.id(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PageResponseDto create(@Valid @RequestBody PageUpsertRequest request) {
        return pageService.create(currentUser.id(), request);
    }

    @PutMapping("/{id}")
    public PageResponseDto update(@PathVariable UUID id, @Valid @RequestBody PageUpsertRequest request) {
        return pageService.update(currentUser.id(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        pageService.delete(currentUser.id(), id);
    }
}
