package service;

//This is thrown when someone tries to login with wrong credentials
public class UnauthorizedException extends Exception {
    public UnauthorizedException(String message) {
        super(message);
    }
}