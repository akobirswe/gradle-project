package com.musicapp.resource.dto;

import java.util.List;

public record DeleteResourceResponse(
        List<Long> ids
) {
}
