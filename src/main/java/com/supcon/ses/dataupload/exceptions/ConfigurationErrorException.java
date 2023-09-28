package com.supcon.ses.dataupload.exceptions;

public class ConfigurationErrorException extends RuntimeException {

    public ConfigurationErrorException(String message) {
        super(message);
    }

    public ConfigurationErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
