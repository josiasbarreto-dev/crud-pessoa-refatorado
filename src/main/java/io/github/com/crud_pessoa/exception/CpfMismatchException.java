package io.github.com.crud_pessoa.exception;

public class CpfMismatchException extends RuntimeException {
    public CpfMismatchException(String message) {
        super(message);
    }

    public CpfMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
