package com.musicapp.song.dto;

public record SongResponse(
        Long id,
        String name,
        String artist,
        String album,
        String duration,
        Integer year
) {
}
