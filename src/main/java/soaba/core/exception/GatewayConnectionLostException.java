package soaba.core.exception;

public class GatewayConnectionLostException extends
        GatewayDriverException {

    private static final long serialVersionUID = 1L;
    
    public GatewayConnectionLostException(){
        super();
    }
    
    public GatewayConnectionLostException(String message){
        super(message);
    }
}
