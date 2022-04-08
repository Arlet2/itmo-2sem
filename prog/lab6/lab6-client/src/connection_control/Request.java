package connection_control;

import java.io.Serializable;

public class Request implements Serializable {
    private final RequestCode requestCode;
    private final String msg;
    public Request (final RequestCode requestCode, final String msg) {
        this.requestCode = requestCode;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public RequestCode getRequestCode() {
        return requestCode;
    }

    public enum RequestCode {
        REPLY,
        COMMAND,
        ERROR,
        NEXT_REQUEST_CITY,
        OK
    }
}