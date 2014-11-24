package soaba.core.api;

public interface IDatapoint {
    public enum ACCESSTYPE {

        /**
         * Represents a datapoint access, restricted to write only.
         */
        WRITE_ONLY,

        /**
         * Represents a datapoint access, restricted to read only.
         */
        READ_ONLY,

        /**
         * Represents a datapoint access, without restrictions of read or write.
         */
        READ_WRITE
    }

    public enum DATATYPE {

        /**
         * Represents the boolean data type: true or false.
         */
        BIT,

        /**
         * Represents the float data type (2 Bytes).
         */
        TINY_NUMBER,

        /**
         * Represents the float data type (4 Bytes).
         */
        NUMBER,

        /**
         * Represents the string data type.
         */
        TEXT,

        /**
         * Represents the unsigned integer data type, limited to range [0, 100].
         */
        PERCENTAGE,
        
        /**
         * Represents a data point type, whose underlyning type is not properly known.
         */
        UNKNOWN
    }

    public String getId();
    
    public ACCESSTYPE getAccessType();

    public DATATYPE getDataType();

    public String getDescription();

    public String getName();
    
    public String getDisplayName();

    public String getReadAddress();

    public String getWriteAddress();

    public String getGatewayAddress();
    
    public String getUnit();
    
    
    public void setAccessType(ACCESSTYPE accessType);

    public void setDataType(DATATYPE dataType);

    public void setName(String name);
    
    public void setDisplayName(String displayName);

    public void setDescription(String description);

    public void setReadAddress(String readAddress);

    public void setWriteAddress(String writeAddress);

    public void setGatewayAddress(String gatewayAddress);
    
    public void setUnit(String unit);
}
