package com.microservices.resource.controller;

import com.microservices.resource.service.ResourceProcessingService;
import com.microservices.resource.service.ValidationService;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("resources")
@AllArgsConstructor
public class ResourceController {
    private record ResourceUploadResponse(long id) {}
    private record DeletedIdsResponse(List<Long> ids){}

    private final ResourceProcessingService resourceProcessingService;
    private final ValidationService validationService;

    @PostMapping
    public ResourceUploadResponse uploadResource(@RequestParam MultipartFile file) {
        validationService.validateFile(file);
        return new ResourceUploadResponse(resourceProcessingService.upload(file));
    }

    @PostMapping("/permanent")
    public ResponseEntity<?> moveToPermanent(@RequestBody Long id) {
        resourceProcessingService.moveToPermanent(id);
        return ResponseEntity.ok(true);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> findById(@PathVariable long id,
                                           @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        validationService.validateRange(range);

        HttpRange httpRange = ObjectUtils.isEmpty(range)
                ? HttpRange.createByteRange(0)
                : HttpRange.parseRanges(range).get(0);

        byte[] content = resourceProcessingService.download(id, httpRange);
        return ResponseEntity
                .status(ObjectUtils.isEmpty(range) ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT)
                .contentLength(content.length)
                .body(content);
    }

    @DeleteMapping
    public DeletedIdsResponse deleteByIds(@RequestParam(name = "id") List<Long> ids) {
        return new DeletedIdsResponse(resourceProcessingService.deleteByIds(ids));
    }

}
