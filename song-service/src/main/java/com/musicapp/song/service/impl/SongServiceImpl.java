package com.musicapp.song.service.impl;

import com.musicapp.song.dto.CreateSongResponse;
import com.musicapp.song.dto.DeleteSongResponse;
import com.musicapp.song.dto.SongRequest;
import com.musicapp.song.dto.SongResponse;
import com.musicapp.song.entity.Song;
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
            throw new ValidationException("Metadata for this ID already exists");
        }

        Song song = songMapper.toEntity(songRequest);
        Song savedSong = songRepository.save(song);
        return new CreateSongResponse(savedSong.getId());
    }

    @Override
    public SongResponse getSong(Long id) {
        return songRepository.findById(id)
                .map(songMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Song metadata not found with ID: " + id));
    }

    @Override
    public DeleteSongResponse deleteSongs(String csvIds) {
        if (csvIds.length() > 200) {
            throw new ValidationException("CSV length exceeds limit");
        }

        List<Long> ids = Stream.of(csvIds.split(","))
                .map(Long::parseLong)
                .toList();

        List<Song> songsToDelete = songRepository.findAllById(ids);
        songRepository.deleteAll(songsToDelete);

        return new DeleteSongResponse(songsToDelete.stream().map(Song::getId).toList());
    }
}
