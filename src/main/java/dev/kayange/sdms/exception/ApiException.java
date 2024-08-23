package dev.kayange.sdms.exception;

public class ApiException extends RuntimeException {
    public ApiException(String s) {super(s);}

    public ApiException(){super("An Error Occurred!");}
}
