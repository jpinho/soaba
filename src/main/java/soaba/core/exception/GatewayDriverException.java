package soaba.core.exception;

public class GatewayDriverException extends
        Exception {

    private static final long serialVersionUID = 3434637719382826333L;

    public GatewayDriverException() {
    }

    public GatewayDriverException(String message) {
        super(message);
    }

    public GatewayDriverException(Throwable cause) {
        super(cause);
    }

    public GatewayDriverException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static long getSerialversionUID() {
        return serialVersionUID;
    }
}
