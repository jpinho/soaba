package soaba.core.models;

import soaba.core.api.IDatapoint;
import soaba.core.api.IDatapoint.DATATYPE;
import soaba.core.exception.DatapointReadonlyAccessTypeException;
import soaba.core.exception.DatapointWriteonlyAccessTypeException;

/**
 * Data Point Value, represents the value carried back and forth for a given data point 
 * through a generic gateway driver.
 * 
 * @author João Pinho (jpe.pinho@gmail.com)
 * @since 0.5
 * @param <T> The datatype of the value boxed by this class.
 */
public class DatapointValue<T> {

    private final IDatapoint datapoint;
    private T value;

    private DatapointValue(IDatapoint originDatapoint) {
        /* built type */
        this.datapoint = originDatapoint;
        this.value = null;
    }

    public T getValue() throws DatapointWriteonlyAccessTypeException {
        return value;
    }

    @SuppressWarnings("unchecked")
    public <U> void setValue(U newValue) throws DatapointReadonlyAccessTypeException {
        DATATYPE dataType = datapoint.getDataType();

        if (dataType == null)
            return;

        switch (dataType) {
            case BIT:
                this.value = (T)new Boolean(String.valueOf(newValue));
                break;
            case TINY_NUMBER:
                this.value = (T)new Float(String.valueOf(newValue));
                break;
            case PERCENTAGE:
                this.value = (T)new Integer(String.valueOf(newValue));
                break;
            case NUMBER:
                this.value = (T)new Double(String.valueOf(newValue));
                break;
            case TEXT:
            default:
                this.value = (T)String.valueOf(newValue);
                break;
        }
    }

    public IDatapoint getDatapoint() {
        return datapoint;
    }

    public static DatapointValue<?> build(IDatapoint datapointSource) {
        if (datapointSource == null)
            return null;

        DATATYPE dataType = datapointSource.getDataType();

        if (dataType == null)
            return null;

        switch (dataType) {
            case BIT:
                return new DatapointValue<Boolean>(datapointSource);
            case TINY_NUMBER:
                return new DatapointValue<Float>(datapointSource);  
            case PERCENTAGE:
                return new DatapointValue<Integer>(datapointSource);  
            case NUMBER:
                return new DatapointValue<Double>(datapointSource);
            case TEXT:
                return new DatapointValue<String>(datapointSource);
            default:
                return new DatapointValue<Object>(datapointSource);
        }
    }
}
