package soaba.core.exception;

public class ServiceResourceErrorException extends
        Exception {

    private static final long serialVersionUID = -2784672157389773192L;

    public ServiceResourceErrorException() {
    }

    public ServiceResourceErrorException(String message) {
        super(message);
    }

    public ServiceResourceErrorException(Throwable cause) {
        super(cause);
    }

    public ServiceResourceErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceResourceErrorException(String message,
                                         Throwable cause,
                                         boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static long getSerialversionUID() {
        return serialVersionUID;
    }
}