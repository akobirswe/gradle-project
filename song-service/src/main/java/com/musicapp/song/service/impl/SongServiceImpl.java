package com.musicapp.song.service.impl;

import com.musicapp.song.dto.CreateSongResponse;
import com.musicapp.song.dto.DeleteSongResponse;
import com.musicapp.song.dto.SongRequest;
import com.musicapp.song.dto.SongResponse;
import com.musicapp.song.entity.Song;
import com.musicapp.song.exception.ConflictException;
import com.musicapp.song.exception.NotFoundException;
import com.musicapp.song.exception.ValidationException;
import com.musicapp.song.mapper.SongMapper;
import com.musicapp.song.repository.SongRepository;
import com.musicapp.song.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final SongMapper songMapper;

    @Override
    public CreateSongResponse createSong(SongRequest songRequest) {
        if (songRepository.existsById(songRequest.id())) {
            throw new ConflictException("Metadata for this ID already exists: " + songRequest.id());
        }

        Song song = songMapper.toEntity(songRequest);
        Song savedSong = songRepository.save(song);
        return new CreateSongResponse(savedSong.getId());
    }

    @Override
    public SongResponse getSong(String id) {
        if (!id.matches("\\d+")) {
            throw new ValidationException("Invalid resource ID: " + id);
        }

        long resourceId = Long.parseLong(id);
        if (resourceId <= 0) {
            throw new ValidationException("Invalid resource ID: " + id);
        }
        return songRepository.findById(resourceId)
                .map(songMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Song metadata not found with ID: " + id));
    }

    @Override
    public DeleteSongResponse deleteSongs(String csvIds) {
        if (csvIds.length() > 200) {
            throw new ValidationException("CSV string is too long: received %s characters. Maximum allowed length is 200 characters.".formatted(csvIds.length()));
        }

        if (!csvIds.matches("^(\\d+,)*\\d+$")) {
            throw new ValidationException("Invalid CSV format: " + csvIds);
        }

        List<Long> ids = Stream.of(csvIds.split(","))
                .map(Long::parseLong)
                .toList();

        List<Song> songsToDelete = songRepository.findAllById(ids);
        songRepository.deleteAll(songsToDelete);

        return new DeleteSongResponse(songsToDelete.stream().map(Song::getId).toList());
    }
}
