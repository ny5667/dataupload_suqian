package com.supcon.ses.dataupload.exceptions;

public class AesErrorException extends RuntimeException {

    public AesErrorException(String message) {
        super(message);
    }

    public AesErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
