package soaba.core.api;

import java.lang.reflect.Type;
import java.net.Inet4Address;

public interface IDatapoint {
    public enum DATAPOINT_ACCESSTYPE {
        WRITE_ONLY, READ_ONLY, READ_WRITE
    }

    public enum DATAPOINT_DATATYPE {
        BIT, NUMBER, TEXT, STREAM, JSON_OBJECT
    }

    public DATAPOINT_ACCESSTYPE getAccessType();

    public Type getDataType();

    public String getDescription();

    public void setDescription(String description);

    public String getName();

    public Inet4Address getGatewayAddress();

    public String getReadAddress();

    public String getWriteAddress();
}
