package com.musicapp.resource.feign;

import com.musicapp.resource.dto.SongRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "song-service", url = "${song-service.url}")
public interface SongServiceClient {
    @PostMapping
    void saveSongMetadata(@Valid @RequestBody SongRequest request);

    @DeleteMapping
    void deleteSongMetadata(@RequestParam("id") String csvIds);
}
