package soaba.core.exception;

public class DatapointReadonlyAccessTypeException extends
        Exception {

    private static final long serialVersionUID = -4356727386463993468L;

    public static long getSerialversionUID() {
        return serialVersionUID;
    }
    
    public DatapointReadonlyAccessTypeException(String datapointId){
        super(datapointId);
    }
}
