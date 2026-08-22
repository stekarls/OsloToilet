package com.app.oslotoilet.exception;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ErrorResponse{
    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
