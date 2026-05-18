package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;


public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    //This wipes everything out for testing
    public void clear() throws DataAccessException {
        dataAccess.clear();
    }
}
