package com.musicapp.resource.service.impl;

import com.musicapp.resource.dto.DeleteResourceResponse;
import com.musicapp.resource.dto.UploadResourceResponse;
import com.musicapp.resource.entity.Resource;
import com.musicapp.resource.exception.NotFoundException;
import com.musicapp.resource.exception.ServiceException;
import com.musicapp.resource.exception.ValidationException;
import com.musicapp.resource.repository.ResourceRepository;
import com.musicapp.resource.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    @Override
    public UploadResourceResponse uploadResource(MultipartFile file) {
        validateFile(file);

        Metadata metadata = extractMetadata(file);

        Resource resource = new Resource();
        try {
            resource.setData(file.getBytes());
        } catch (IOException e) {
            throw new ServiceException("Failed to read file data", e);
        }

        Resource savedResource = resourceRepository.save(resource);

        // TODO: Call Song Service to store metadata
        metadata.add("id", String.valueOf(savedResource.getId()));
        // will be call to song-service here

        return new UploadResourceResponse(savedResource.getId());
    }

    @Override
    public byte[] getResource(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resource not found with id: " + id))
                .getData();
    }

    @Override
    public DeleteResourceResponse deleteResources(String csvIds) {
        if (csvIds.length() > 200) {
            throw new ValidationException("CSV length exceeds limit");
        }

        List<Long> ids = Stream.of(csvIds.split(","))
                .map(Long::parseLong)
                .toList();

        List<Resource> resourcesToDelete = resourceRepository.findAllById(ids);
        if (resourcesToDelete.isEmpty()) {
            throw new NotFoundException("No resources found for the given IDs.");
        }

        resourceRepository.deleteAll(resourcesToDelete);
        return new DeleteResourceResponse(resourcesToDelete.stream().map(Resource::getId).toList());
    }


    private Metadata extractMetadata(MultipartFile file) {
        try (InputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            Metadata metadata = new Metadata();
            BodyContentHandler handler = new BodyContentHandler();
            Mp3Parser mp3Parser = new Mp3Parser();
            mp3Parser.parse(inputStream, handler, metadata, new ParseContext());
            return metadata;
        } catch (Exception e) {
            throw new ServiceException("Failed to extract MP3 metadata", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException("File is empty");
        }
        if (!"audio/mpeg".equals(file.getContentType())) {
            throw new ValidationException("Invalid MP3 file");
        }
    }
}
