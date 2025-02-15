package com.musicapp.resource.controller;

import com.musicapp.resource.dto.DeleteResourceResponse;
import com.musicapp.resource.dto.UploadResourceResponse;
import com.musicapp.resource.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService service;

    @PostMapping
    public ResponseEntity<UploadResourceResponse> uploadResource(@RequestBody byte[] fileData) {
        return ResponseEntity.ok().body(service.uploadResource(fileData));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getResource(@PathVariable Long id) {
        byte[] fileData = service.getResource(id);
        return ResponseEntity.ok(fileData);
    }

    @DeleteMapping
    public ResponseEntity<DeleteResourceResponse> deleteResources(@RequestParam("id") String ids) {
        return ResponseEntity.ok().body(service.deleteResources(ids));
    }
}
