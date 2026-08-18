package com.notdefteri.dto;

import java.util.List;

public record AskResponse(String answer, List<SourceDto> sources) {

    public record SourceDto(String pageId, String pageTitle, String snippet) {
    }
}
