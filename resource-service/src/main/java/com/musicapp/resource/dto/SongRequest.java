package com.musicapp.resource.dto;

import jakarta.validation.constraints.*;

public record SongRequest(
        @NotNull(message = "ID is required")
        Long id,

        @NotBlank(message = "Name is required")
        @Size(min = 1, max = 100, message = "Name must be 1-100 characters")
        String name,

        @NotBlank(message = "Artist is required")
        @Size(min = 1, max = 100, message = "Artist must be 1-100 characters")
        String artist,

        @NotBlank(message = "Album is required")
        @Size(min = 1, max = 100, message = "Album must be 1-100 characters")
        String album,

        @Pattern(regexp = "^(0[0-9]|[1-5][0-9]):[0-5][0-9]$", message = "Duration must be in MM:SS format")
        String duration,

        @Min(value = 1900, message = "Year must be between 1900 and 2099")
        @Max(value = 2099, message = "Year must be between 1900 and 2099")
        Integer year
){
}
