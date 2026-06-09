package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataException extends Exception{
    public DataException(String message) {
        super(message);
    }
    public DataException(String message, Throwable ex) {
        super(message, ex);
    }
}
