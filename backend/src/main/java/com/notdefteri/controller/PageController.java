package com.notdefteri.controller;

import com.notdefteri.dto.PageResponseDto;
import com.notdefteri.dto.PageTreeDto;
import com.notdefteri.dto.PageUpsertRequest;
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

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping
    public List<PageTreeDto> tree() {
        return pageService.getTree();
    }

    @GetMapping("/search")
    public List<PageResponseDto> search(@RequestParam("q") String query) {
        return pageService.search(query);
    }

    @GetMapping("/{id}")
    public PageResponseDto get(@PathVariable UUID id) {
        return pageService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PageResponseDto create(@Valid @RequestBody PageUpsertRequest request) {
        return pageService.create(request);
    }

    @PutMapping("/{id}")
    public PageResponseDto update(@PathVariable UUID id, @Valid @RequestBody PageUpsertRequest request) {
        return pageService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        pageService.delete(id);
    }
}
