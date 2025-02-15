package com.musicapp.song.mapper;

import com.musicapp.song.dto.SongRequest;
import com.musicapp.song.dto.SongResponse;
import com.musicapp.song.entity.Song;
import org.springframework.stereotype.Component;

@Component
public class SongMapper {
    public Song toEntity(SongRequest dto) {
        return Song.builder()
                .id(dto.id())
                .name(dto.name())
                .artist(dto.artist())
                .album(dto.album())
                .duration(dto.duration())
                .year(dto.year())
                .build();
    }

    public SongResponse toDto(Song song) {
        return new SongResponse(
                song.getId(),
                song.getName(),
                song.getArtist(),
                song.getAlbum(),
                song.getDuration(),
                song.getYear()
        );
    }
}
