package com.musicapp.resource.feign;

import com.musicapp.resource.dto.SongRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "SONG-SERVICE")
public interface SongServiceClient {
    @PostMapping("/api/v1/songs")
    void saveSongMetadata(@Valid @RequestBody SongRequest request);

    @DeleteMapping("/api/v1/songs")
    void deleteSongMetadata(@RequestParam("id") String csvIds);
}
