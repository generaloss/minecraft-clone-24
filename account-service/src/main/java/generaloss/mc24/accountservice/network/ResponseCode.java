package generaloss.mc24.accountservice.network;

public enum ResponseCode {

    NO_RESPONSE,
    NO_ERROR,
    ERROR;

    public boolean hasResponse() {
        return this != NO_RESPONSE;
    }

}
