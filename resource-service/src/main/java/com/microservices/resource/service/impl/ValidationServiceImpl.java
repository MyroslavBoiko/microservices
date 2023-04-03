package com.microservices.resource.service.impl;

import com.microservices.resource.exception.FileValidationException;
import com.microservices.resource.exception.RangeValidationException;
import com.microservices.resource.service.ValidationService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ValidationServiceImpl implements ValidationService {

    private static final String ALLOWED_MEDIA_TYPE = "audio/mpeg";
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 20L;

    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileValidationException("Please provide file");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("File is too large");
        }
        if (!ALLOWED_MEDIA_TYPE.equalsIgnoreCase(file.getContentType())) {
            throw new FileValidationException("Invalid file format. Only "
                    + ALLOWED_MEDIA_TYPE + " files are allowed");
        }
    }

    public void validateRange(String range) {
        if (!ObjectUtils.isEmpty(range)) {
            if (!range.matches("^bytes=\\d*-\\d*")) {
                throw new RangeValidationException("Please provide valid range header");
            }
        }
    }
}
