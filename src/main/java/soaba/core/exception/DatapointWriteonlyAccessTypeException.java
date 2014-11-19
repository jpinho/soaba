package soaba.core.exception;

public class DatapointWriteonlyAccessTypeException extends
        Exception {

    private static final long serialVersionUID = 162322254924212908L;

    public static long getSerialversionUID() {
        return serialVersionUID;
    }

    public DatapointWriteonlyAccessTypeException(String datapointId) {
        super(datapointId);
    }
}
