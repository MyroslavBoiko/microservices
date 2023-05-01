package com.microservices.songservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.songservice.dto.SongDto;
import com.microservices.songservice.exception.DuplicateResourceIdException;
import com.microservices.songservice.service.SongService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SongController.class)
class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SongService songService;

    @Test
    void createResourceReturnId() throws Exception {
        SongDto songDto = new SongDto("Name", "Artist", "Album", "4:22", 1L, (short) 1999);
        when(songService.create(songDto)).thenReturn(1L);
        this.mockMvc.perform(post("/songs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(songDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"id\":1}")));
    }

    @Test
    void shouldReturnSongDataById() throws Exception {
        SongDto songDto = new SongDto("Name", "Artist", "Album", "4:22", 1L, (short) 1999);
        when(songService.findById(1L)).thenReturn(songDto);
        this.mockMvc.perform(get("/songs/{id}", 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(objectMapper.writeValueAsString(songDto))));
    }

    @Test
    void shouldReturnDeletedIds() throws Exception {
        when(songService.deleteByIds(List.of(1L))).thenReturn(List.of(1L));
        this.mockMvc.perform(delete("/songs")
                        .queryParam("id", "1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"ids\":[1]}"));
    }

    @Test
    void whenDataNotValid_thenReturns400() throws Exception {
        SongDto songDto = new SongDto();

        when(songService.create(songDto)).thenReturn(1L);
        this.mockMvc.perform(post("/songs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(songDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenValidInput_thenMapsToBusinessModel() throws Exception {
        SongDto songDto = new SongDto("Name", "Artist", "Album", "4:22", 1L, (short) 1999);
        when(songService.create(songDto)).thenReturn(1L);
        this.mockMvc.perform(post("/songs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(songDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"id\":1}")));

        ArgumentCaptor<SongDto> argumentCaptor = ArgumentCaptor.forClass(SongDto.class);
        verify(songService, times(1)).create(argumentCaptor.capture());
        assertEquals("Song name", argumentCaptor.getValue().getName(), "Name");
        assertEquals("Song artist", argumentCaptor.getValue().getArtist(), "Artist");
    }

    @Test
    void whenDuplicate_thenReturns400AndErrorResult() throws Exception {
        SongDto songDto = new SongDto("Name", "Artist", "Album", "4:22", 1L, (short) 1999);

        when(songService.create(songDto)).thenThrow(new DuplicateResourceIdException("Duplicate id"));
        MvcResult mvcResult = this.mockMvc.perform(post("/songs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(songDto)))
                .andExpect(status().isBadRequest()).andReturn();

        assertEquals("Assert duplicate", "{\"error\":\"Duplicate id\"}", mvcResult.getResponse().getContentAsString());
    }


}