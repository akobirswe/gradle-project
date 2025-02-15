package com.musicapp.resource.service;

import com.musicapp.resource.dto.DeleteResourceResponse;
import com.musicapp.resource.dto.UploadResourceResponse;

public interface ResourceService {
    UploadResourceResponse uploadResource(byte[] file);

    byte[] getResource(Long id);

    DeleteResourceResponse deleteResources(String csvIds);
}
