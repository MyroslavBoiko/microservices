package com.microservices.resourceprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongDto {

    private String name;
    private String artist;
    private String album;
    private String length;
    private Long resourceId;
    private short year;

}
