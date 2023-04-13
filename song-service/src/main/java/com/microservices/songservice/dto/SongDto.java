package com.microservices.songservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongDto {
    @NotBlank(message = "Please provide song name")
    @Size(max = 1000, message = "Song name can be max 1000 characters long")
    private String name;

    @NotBlank(message = "Please provide artist")
    @Size(max = 500, message = "Song artist can be max 500 characters long")
    private String artist;

    @Size(max = 500, message = "Album can be max 500 characters long")
    private String album;

    @NotBlank(message = "Please provide song length")
    private String length;

    @NotNull(message = "Please provide resource id")
    private Long resourceId;

    @Max(value = 2500, message = "Please provide valid year")
    @Min(value = 1500, message = "Please provide valid year")
    private short year;

}


