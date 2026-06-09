package service;

//This is thrown when someone tries to use a username that is taken
public class BeenTakenException extends RuntimeException {
    public BeenTakenException(String message) {
        super(message);
    }
}