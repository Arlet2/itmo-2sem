package connection_control;

import java.io.Serializable;

/**
 * Object that server always send/receive to/from client
 */
public class Request implements Serializable {

    /**
     * code of this request
     */
    private final RequestCode requestCode;

    /**
     * byte array of msg
     */
    private final byte[] msgBytes;

    /**
     * Create data object for sending
     *
     * @param requestCode of this request
     * @param msg         of this request
     */
    public Request(final RequestCode requestCode, final String msg) {
        this.requestCode = requestCode;
        msgBytes = msg.getBytes();
    }

    public Request(final RequestCode requestCode, final byte[] bytes) {
        this.requestCode = requestCode;
        msgBytes = bytes;
    }

    public String getMsg() {
        return new String(msgBytes);
    }

    public RequestCode getRequestCode() {
        return requestCode;
    }

    public byte[] getMsgBytes() {
        return msgBytes;
    }

    /**
     * Some codes for init request
     * REPLY - result of command that prints on client's screen
     * COMMAND - command with arguments that need to execute on server
     * ERROR - explanation of error with arguments or execution
     * NEXT_REQUEST_CITY - next object for receiving is City
     * OK - all arguments that need to check on server is ok OR server is ready to continue processing of command
     */
    public enum RequestCode {
        REPLY,
        COMMAND,
        ERROR,
        NEXT_REQUEST_CITY,
        OK,
        PART_OF_DATE
    }
}