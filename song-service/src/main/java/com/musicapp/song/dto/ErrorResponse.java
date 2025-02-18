package com.musicapp.song.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String errorMessage,
        String errorCode,
        Map<String, String> details
) {
}
