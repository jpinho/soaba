package soaba.core.models;

import java.lang.reflect.Type;
import java.net.Inet4Address;
import java.util.stream.Stream;

import org.json.simple.JSONObject;

import soaba.core.api.IDatapoint;

public class Datapoint implements IDatapoint {

    private final String name;
    private String description;
    private final String readAddress;
    private final String writeAddress;
    private final DATAPOINT_DATATYPE dataType;
    private final DATAPOINT_ACCESSTYPE accessType;
    private final Inet4Address gatewayAddress;

    public Datapoint(String name,
                     DATAPOINT_ACCESSTYPE accessType,
                     DATAPOINT_DATATYPE dataType,
                     String readAddress,
                     String writeAddress,
                     Inet4Address gatewayAddress) {
        this.name = name;
        this.accessType = accessType;
        this.dataType = dataType;
        this.readAddress = readAddress;
        this.writeAddress = writeAddress;
        this.gatewayAddress = gatewayAddress;
    }

    public DATAPOINT_ACCESSTYPE getAccessType() {
        return accessType;
    }

    public Type getDataType() {
        switch (dataType) {
            case BIT:
                return Boolean.class;
            case NUMBER:
                return Double.class;
            case TEXT:
                return String.class;
            case STREAM:
                return Stream.class;
            case JSON_OBJECT:
                return JSONObject.class;
            default:
                return Object.class;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Inet4Address getGatewayAddress() {
        return gatewayAddress;
    }

    public String getReadAddress() {
        return readAddress;
    }

    public String getWriteAddress() {
        return writeAddress;
    }
}
