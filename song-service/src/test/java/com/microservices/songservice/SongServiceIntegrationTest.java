package com.microservices.songservice;

import com.microservices.songservice.dto.SongDto;
import com.microservices.songservice.service.SongService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SongServiceIntegrationTest {

    @Autowired
    private SongService songService;

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres")
            .withDatabaseName("songs_it")
            .withUsername("sa")
            .withUsername("sa");

    @BeforeAll
    public static void setUp() {
        postgreSQLContainer.withReuse(true);
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
    }

    @Test
    public void testCreateSong() {
        long id = songService.create(new SongDto());

        assertNotEquals(1, id);
    }

    @Test
    public void testFindById() {
        SongDto songDto = new SongDto();
        songDto.setName("Name");
        songDto.setResourceId(1L);
        songDto.setLength("1:44");
        songDto.setYear((short)1999);
        songDto.setArtist("Artist");
        songDto.setAlbum("Album");

        long id = songService.create(songDto);

        SongDto actual = songService.findById(id);

        assertNotNull(actual);
        assertEquals(songDto, actual);
    }

    @Test
    public void testDeleteByIds() {
        SongDto songDto = new SongDto();
        songDto.setName("Name");
        songDto.setResourceId(1L);
        songDto.setLength("1:44");
        songDto.setYear((short)1999);
        songDto.setArtist("Artist");
        songDto.setAlbum("Album");

        long id = songService.create(songDto);

        List<Long> deletedByIds = songService.deleteByIds(List.of(id));

        assertNotNull(deletedByIds);
        assertEquals(1L, deletedByIds.get(0));
    }

    @AfterAll
    public static void tearDown(){
        postgreSQLContainer.stop();
    }
}
