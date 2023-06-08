package com.microservices.storage.payload;

import java.util.List;

public record DeleteStoragesResponse(List<Integer> ids) {
}
