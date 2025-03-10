package com.musicapp.resource.dto;

import jakarta.validation.constraints.*;

public record SongRequest(
        @NotNull(message = "ID is required")
        @Min(value = 0, message = "ID must be a numeric value greater than 0")
        Long id,

        @NotBlank(message = "Song name is required")
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

        @Pattern(regexp = "^(19|20)\\d{2}$", message = "Year must be in YYYY format (1900-2099)")
        @NotBlank(message = "Year is required")
        String year
) {
}
