package com.musicapp.song.dto;

import java.util.List;

public record DeleteSongResponse(
        List<Long> ids
) {
}
