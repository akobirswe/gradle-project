package com.musicapp.resource.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String errorMessage,
        String errorCode,
        Map<String, String> details
) {
}
