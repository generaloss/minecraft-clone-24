package generaloss.mc24.accountservice.network;

public enum RequestType {

    CREATE_ACCOUNT  , // (nickname, password) -> (bool)
    DELETE_ACCOUNT  , // (nickname, password) -> (bool)

    LOG_IN          , // (nickname, password) -> (sessionID)
    LOG_OUT         , // (sessionID) -> (bool)

    HAS_SESSION     , // (sessionID) -> (bool)
    GET_SESSION_INFO, // (sessionID) -> (nickname, creation_date)

}
