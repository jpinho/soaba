package soaba.core.models;

import soaba.core.api.IDatapoint;
import soaba.core.api.IDatapoint.DATATYPE;
import soaba.core.exception.DatapointReadonlyAccessTypeException;
import soaba.core.exception.DatapointWriteonlyAccessTypeException;

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
        this.value = ((T) newValue);
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
            case PERCENTAGE:
                return new DatapointValue<Float>(datapointSource);
            case NUMBER:
                return new DatapointValue<Double>(datapointSource);
            case TEXT:
                return new DatapointValue<String>(datapointSource);
            default:
                return new DatapointValue<Object>(datapointSource);
        }
    }
}
