package service;

//This is thrown when someone tries to use a username that is taken
public class AlreadyTakenException extends RuntimeException {
    public AlreadyTakenException(String message) {
        super(message);
    }
}