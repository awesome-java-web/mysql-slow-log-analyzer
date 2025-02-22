package com.github.awesome.mysql.slowlog.exception;

public class HttpRemoteFileDownloadException extends RuntimeException {

    public HttpRemoteFileDownloadException(String message) {
        super(message);
    }

    public HttpRemoteFileDownloadException(String message, Throwable cause) {
        super(message, cause);
    }

}
