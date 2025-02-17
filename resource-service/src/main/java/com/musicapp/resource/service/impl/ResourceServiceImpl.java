package com.musicapp.resource.service.impl;

import com.musicapp.resource.dto.DeleteResourceResponse;
import com.musicapp.resource.dto.SongRequest;
import com.musicapp.resource.dto.UploadResourceResponse;
import com.musicapp.resource.entity.Resource;
import com.musicapp.resource.exception.NotFoundException;
import com.musicapp.resource.exception.ServiceException;
import com.musicapp.resource.exception.ValidationException;
import com.musicapp.resource.feign.SongServiceClient;
import com.musicapp.resource.repository.ResourceRepository;
import com.musicapp.resource.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
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

        Metadata metadata = extractMetadata(file);

        Resource resource = new Resource();
        resource.setData(file);
        Resource savedResource = resourceRepository.save(resource);

        String title = Optional.ofNullable(metadata.get("title")).orElse("Unknown Title");
        String artist = Optional.ofNullable(metadata.get("xmpDM:artist")).orElse("Unknown Artist");
        String album = Optional.ofNullable(metadata.get("xmpDM:album")).orElse("Unknown Album");
        String duration = metadata.get("xmpDM:duration");
        String formattedDuration = formatDuration(duration);
        String yearStr = metadata.get("xmpDM:releaseDate");

        int year = (yearStr != null && !yearStr.isEmpty()) ? Integer.parseInt(yearStr) : 0;

        SongRequest request = new SongRequest(savedResource.getId(), title, artist, album, formattedDuration, year);
        songServiceClient.saveSongMetadata(request);

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
        resourceRepository.deleteAll(resourcesToDelete);

        songServiceClient.deleteSongMetadata(csvIds);

        return new DeleteResourceResponse(resourcesToDelete.stream().map(Resource::getId).toList());
    }


    private Metadata extractMetadata(byte[] fileData) {
        try (InputStream inputStream = new ByteArrayInputStream(fileData)) {
            Metadata metadata = new Metadata();
            BodyContentHandler handler = new BodyContentHandler();
            Mp3Parser mp3Parser = new Mp3Parser();
            mp3Parser.parse(inputStream, handler, metadata, new ParseContext());
            return metadata;
        } catch (Exception e) {
            throw new ServiceException("Failed to extract MP3 metadata", e);
        }
    }

    private String formatDuration(String duration) {
        if (duration == null || duration.isEmpty()) {
            return "00:00";
        }

        try {
            double seconds = Double.parseDouble(duration);
            int minutes = (int) (seconds / 60);
            int remainingSeconds = (int) (seconds % 60);

            return String.format("%02d:%02d", minutes, remainingSeconds);
        } catch (NumberFormatException e) {
            return "00:00";
        }
    }
}
