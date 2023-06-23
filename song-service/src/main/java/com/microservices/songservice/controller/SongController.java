package com.microservices.songservice.controller;

import com.microservices.songservice.dto.SongDto;
import com.microservices.songservice.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
@Slf4j
public class SongController {
    private record SongCreatedResponse(long id) {}
    private record SongDeletedResponse(List<Long> ids) {}

    private final SongService songService;

    @PostMapping
    public SongCreatedResponse create(@Valid @RequestBody SongDto songDto) {
        log.info("Song creation");
        return new SongCreatedResponse(songService.create(songDto));
    }

    @GetMapping("{id}")
    public SongDto findById(@PathVariable long id) {
        log.info("Song find by id {}", id);
        return songService.findById(id);
    }

    @DeleteMapping
    public SongDeletedResponse deleteByIds(@RequestParam(name = "id") List<Long> ids) {
        log.info("Song deletion by ids {}", ids);
        List<Long> deletedByIds = songService.deleteByIds(ids);
        return new SongDeletedResponse(deletedByIds);
    }
}
