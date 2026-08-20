package com.app.oslotoilet.exception;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ErrorResponse{
    OffsetDateTime timestamp;
    int status;
    String error;
    String message;
}
