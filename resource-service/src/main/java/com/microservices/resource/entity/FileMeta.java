package com.microservices.resource.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "file_meta")
public class FileMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;


    @Column(name = "file_key")
    private String key;

    @Column(name = "size")
    private Long size;

    @Enumerated(EnumType.STRING)
    private StorageStatus status;

    public FileMeta(String fileName, String key, Long size, StorageStatus status) {
        this.fileName = fileName;
        this.key = key;
        this.size = size;
        this.status = status;
    }
}
