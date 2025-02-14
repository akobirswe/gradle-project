package com.musicapp.resource.service;

import com.musicapp.resource.dto.DeleteResourceResponse;
import com.musicapp.resource.dto.UploadResourceResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
    UploadResourceResponse uploadResource(MultipartFile file);

    byte[] getResource(Long id);

    DeleteResourceResponse deleteResources(String csvIds);
}
