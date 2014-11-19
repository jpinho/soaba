package soaba.core.exception;

public class GatewayMaxConnectionsReachedException extends
        GatewayDriverException {

    private static final long serialVersionUID = 1L;

    public GatewayMaxConnectionsReachedException() {
    }

    public GatewayMaxConnectionsReachedException(String message) {
        super(message);
    }

    public GatewayMaxConnectionsReachedException(Throwable cause) {
        super(cause);
    }

    public GatewayMaxConnectionsReachedException(String message, Throwable cause) {
        super(message, cause);
    }
}
