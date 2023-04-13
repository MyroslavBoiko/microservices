package com.microservices.resourceprocessor.service.impl;

import com.microservices.resourceprocessor.exception.MetadataExtractException;
import com.microservices.resourceprocessor.dto.SongDto;
import com.microservices.resourceprocessor.service.MetadataExtractor;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class MetadataExtractorImpl implements MetadataExtractor {

    @Override
    public SongDto extractSongMetadata(byte[] file, Long id) {
        SongDto songMetadataDto = new SongDto();
        try {
            InputStream input = new ByteArrayInputStream(file);
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);
            input.close();

            songMetadataDto.setName(metadata.get("dc:title"));
            songMetadataDto.setArtist(metadata.get("xmpDM:artist"));
            songMetadataDto.setAlbum(metadata.get("xmpDM:album"));
            songMetadataDto.setLength(metadata.get("xmpDM:duration"));
            songMetadataDto.setYear(Short.parseShort(metadata.get("xmpDM:releaseDate")));
            songMetadataDto.setResourceId(id);
        } catch (IOException | TikaException | SAXException e) {
            throw new MetadataExtractException(e.getMessage());
        }
        return songMetadataDto;
    }
}
