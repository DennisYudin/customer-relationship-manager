package dev.yudin.exceptions;

public class AppConfigurationException extends RuntimeException {

    public AppConfigurationException() {
        super();
    }

    public AppConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppConfigurationException(String message) {
        super(message);
    }
}
