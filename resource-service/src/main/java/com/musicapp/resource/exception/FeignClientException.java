package com.musicapp.resource.exception;

import org.springframework.http.HttpStatus;

public class FeignClientException extends RuntimeException {
    private final HttpStatus status;

    public FeignClientException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
