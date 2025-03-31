package com.musicapp.resource.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FeignClientException extends RuntimeException {

    private final HttpStatus status;

    public FeignClientException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
