package service;

//This is thrown when the request is missing required info
public class BadRequest extends RuntimeException {
    public BadRequest(String message) {
        super(message);
    }
}