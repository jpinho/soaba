package soaba.core.exception;

public class DatapointInvalidValueTypeException extends
        Exception {

    private static final long serialVersionUID = 5236394719159908137L;

    public DatapointInvalidValueTypeException() {
    }

    public DatapointInvalidValueTypeException(String message) {
        super(message);
    }

    public DatapointInvalidValueTypeException(Throwable cause) {
        super(cause);
    }

    public DatapointInvalidValueTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public static long getSerialversionUID() {
        return serialVersionUID;
    }
}
