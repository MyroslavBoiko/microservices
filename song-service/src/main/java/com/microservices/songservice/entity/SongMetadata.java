package com.microservices.songservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "song_metadata")
public class SongMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private Long resourceId;
    private String name;
    private String artist;
    private String album;
    private String length;
    @Column(name = "release_year")
    private short year;

}

