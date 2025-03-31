package com.musicapp.resource.service.impl;

import com.musicapp.resource.dto.DeleteResourceResponse;
import com.musicapp.resource.dto.SongRequest;
import com.musicapp.resource.dto.UploadResourceResponse;
import com.musicapp.resource.entity.Resource;
import com.musicapp.resource.exception.FeignClientException;
import com.musicapp.resource.exception.NotFoundException;
import com.musicapp.resource.exception.ValidationException;
import com.musicapp.resource.feign.SongServiceClient;
import com.musicapp.resource.repository.ResourceRepository;
import com.musicapp.resource.service.ResourceService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final SongServiceClient songServiceClient;

    @Override
    public UploadResourceResponse uploadResource(byte[] file) {
        if (file == null || file.length == 0) {
            throw new ValidationException("File data is empty");
        }

        var metadata = extractMetadata(file);

        var resource = new Resource();
        resource.setData(file);
        var savedResource = resourceRepository.save(resource);

        var title = Optional.ofNullable(metadata.get("title")).orElse("Unknown Title");
        var artist = Optional.ofNullable(metadata.get("xmpDM:artist")).orElse("Unknown Artist");
        var album = Optional.ofNullable(metadata.get("xmpDM:album")).orElse("Unknown Album");
        var duration = metadata.get("xmpDM:duration");
        var formattedDuration = formatDuration(duration);
        var year = metadata.get("xmpDM:releaseDate");

        var request = new SongRequest(savedResource.getId(), title, artist, album, formattedDuration, year);

        try {
            songServiceClient.saveSongMetadata(request);
        } catch (FeignException.BadRequest ex) {
            var responseBody = ex.contentUTF8();
            throw new ValidationException("Song Service Validation Error: " + responseBody);
        } catch (FeignException ex) {
            throw new FeignClientException(HttpStatus.valueOf(ex.status()), extractFeignMessage(ex));
        }

        return new UploadResourceResponse(savedResource.getId());
    }

    private String extractFeignMessage(FeignException ex) {
        try {
            return ex.contentUTF8();
        } catch (Exception e) {
            return "Unexpected error from Song Service";
        }
    }


    @Override
    public byte[] getResource(String id) {
        if (!id.matches("\\d+")) {
            throw new ValidationException("Invalid value '%s' for ID. Must be a positive integer".formatted(id));
        }

        var resourceId = Long.parseLong(id);
        if (resourceId <= 0) {
            throw new ValidationException("Invalid value '%s' for ID. Must be a positive integer".formatted(id));
        }
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource not found with ID: " + id))
                .getData();
    }

    @Override
    public DeleteResourceResponse deleteResources(String csvIds) {
        if (csvIds.length() > 200) {
            throw new ValidationException("CSV string is too long: received %s characters. Maximum allowed length is 200 characters.".formatted(csvIds.length()));
        }

        if (!csvIds.matches("^(\\d+,)*\\d+$")) {
            throw new ValidationException("Invalid CSV format: " + csvIds);
        }

        var ids = Stream.of(csvIds.split(","))
                .map(Long::parseLong)
                .toList();

        var resourcesToDelete = resourceRepository.findAllById(ids);
        resourceRepository.deleteAll(resourcesToDelete);
        songServiceClient.deleteSongMetadata(csvIds);

        return new DeleteResourceResponse(resourcesToDelete.stream().map(Resource::getId).toList());
    }

    private Metadata extractMetadata(byte[] fileData) {
        try (var inputStream = new ByteArrayInputStream(fileData)) {
            var metadata = new Metadata();
            var handler = new BodyContentHandler();
            var mp3Parser = new Mp3Parser();
            mp3Parser.parse(inputStream, handler, metadata, new ParseContext());
            return metadata;
        } catch (Exception e) {
            throw new ValidationException("Invalid MP3 data");
        }
    }

    private String formatDuration(String duration) {
        if (duration == null || duration.isEmpty()) {
            return "00:00";
        }

        try {
            var seconds = Double.parseDouble(duration);
            var minutes = (int) (seconds / 60);
            var remainingSeconds = (int) (seconds % 60);

            return String.format("%02d:%02d", minutes, remainingSeconds);
        } catch (NumberFormatException e) {
            return "00:00";
        }
    }
}
