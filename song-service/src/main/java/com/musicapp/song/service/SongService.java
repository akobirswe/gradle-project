package com.musicapp.song.service;

import com.musicapp.song.dto.CreateSongResponse;
import com.musicapp.song.dto.DeleteSongResponse;
import com.musicapp.song.dto.SongRequest;
import com.musicapp.song.dto.SongResponse;

public interface SongService {
    CreateSongResponse createSong(SongRequest songRequest);

    SongResponse getSong(String id);

    DeleteSongResponse deleteSongs(String csvIds);
}
