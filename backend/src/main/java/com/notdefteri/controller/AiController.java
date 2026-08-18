package com.notdefteri.controller;

import com.notdefteri.dto.AiTextRequest;
import com.notdefteri.dto.AiTextResponse;
import com.notdefteri.dto.AskRequest;
import com.notdefteri.dto.AskResponse;
import com.notdefteri.service.AiTextService;
import com.notdefteri.service.RagService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Seçili metin aksiyonları (özetle/genişlet/düzelt) ve sayfa bağlamlı soru-cevap (RAG). */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiTextService aiTextService;
    private final RagService ragService;

    public AiController(AiTextService aiTextService, RagService ragService) {
        this.aiTextService = aiTextService;
        this.ragService = ragService;
    }

    @PostMapping("/summarize")
    public AiTextResponse summarize(@Valid @RequestBody AiTextRequest request) {
        return new AiTextResponse(aiTextService.summarize(request.text()));
    }

    @PostMapping("/expand")
    public AiTextResponse expand(@Valid @RequestBody AiTextRequest request) {
        return new AiTextResponse(aiTextService.expand(request.text()));
    }

    @PostMapping("/fix")
    public AiTextResponse fix(@Valid @RequestBody AiTextRequest request) {
        return new AiTextResponse(aiTextService.fix(request.text()));
    }

    @PostMapping("/ask")
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return ragService.ask(request.question());
    }
}
