package com.musicapp.song.controller;

import com.musicapp.song.dto.CreateSongResponse;
import com.musicapp.song.dto.DeleteSongResponse;
import com.musicapp.song.dto.SongRequest;
import com.musicapp.song.dto.SongResponse;
import com.musicapp.song.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping
    public ResponseEntity<CreateSongResponse> createSong(@Valid @RequestBody SongRequest songRequest) {
        return ResponseEntity.ok(songService.createSong(songRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getSong(@PathVariable String id) {
        return ResponseEntity.ok(songService.getSong(id));
    }

    @DeleteMapping
    public ResponseEntity<DeleteSongResponse> deleteSongs(@RequestParam("id") String ids) {
        return ResponseEntity.ok(songService.deleteSongs(ids));
    }
}
