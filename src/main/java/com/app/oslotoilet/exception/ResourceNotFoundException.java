package com.app.oslotoilet.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, UUID uuid) {
        super(resourceName + " not found with id: " + uuid);
    }
}
