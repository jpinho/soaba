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

    public void setDescription(String description);

    public String getName();

    public String getReadAddress();

    public String getWriteAddress();

    public String getGatewayAddress();
}
