package soaba.core.models;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import soaba.core.api.IDatapoint;

public class Datapoint
        implements IDatapoint, Serializable {

    private static final long serialVersionUID = 7462299013208883734L;

    public static long getSerialversionUID() {
        return serialVersionUID;
    }

    private ACCESSTYPE accessType;
    private DATATYPE dataType;
    private String description;
    private String gatewayAddress;
    private String id;
    private String name;
    private String readAddress;
    private String writeAddress;
    private String unit;
    private String displayName;

    public Datapoint() {
    }

    public Datapoint(String gatewayAddress,
                     String name,
                     ACCESSTYPE accessType,
                     DATATYPE dataType,
                     String readAddress,
                     String writeAddress) {
        this.gatewayAddress = gatewayAddress;
        this.name = name;
        this.accessType = accessType;
        this.dataType = dataType;
        this.readAddress = readAddress;
        this.writeAddress = writeAddress;
        this.setDisplayName(name);
        
        byte[] dpID = (gatewayAddress + name + readAddress + writeAddress).getBytes();

        try {
            this.id = new BigInteger(1, MessageDigest.getInstance("SHA-1").digest(dpID)).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    public Datapoint(String gatewayAddress,
                     String name,
                     ACCESSTYPE accessType,
                     DATATYPE dataType,
                     String readAddress,
                     String writeAddress,
                     String unit) {
        this(gatewayAddress, name, accessType, dataType, readAddress, writeAddress);
        this.setUnit(unit);
        this.setDisplayName(name);
    }
    
    public Datapoint(String gatewayAddress,
                     String name,
                     String displayName, 
                     ACCESSTYPE accessType,
                     DATATYPE dataType,
                     String readAddress,
                     String writeAddress,
                     String unit) {
        this(gatewayAddress, name, accessType, dataType, readAddress, writeAddress, unit);
        this.setDisplayName(displayName);
    }
    
    public Datapoint(String gatewayAddress,
                     String name,
                     String displayName, 
                     ACCESSTYPE accessType,
                     DATATYPE dataType,
                     String readAddress,
                     String writeAddress) {
        this(gatewayAddress, name, accessType, dataType, readAddress, writeAddress);
        this.setDisplayName(displayName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Datapoint other = (Datapoint) obj;
        if (gatewayAddress == null) {
            if (other.gatewayAddress != null)
                return false;
        } else if (!gatewayAddress.equals(other.gatewayAddress))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (readAddress == null) {
            if (other.readAddress != null)
                return false;
        } else if (!readAddress.equals(other.readAddress))
            return false;
        if (writeAddress == null) {
            if (other.writeAddress != null)
                return false;
        } else if (!writeAddress.equals(other.writeAddress))
            return false;
        return true;
    }

    public ACCESSTYPE getAccessType() {
        return accessType;
    }

    public DATATYPE getDataType() {
        return dataType;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getGatewayAddress() {
        return gatewayAddress;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getReadAddress() {
        return readAddress;
    }

    public String getWriteAddress() {
        return writeAddress;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((gatewayAddress == null) ? 0 : gatewayAddress.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((readAddress == null) ? 0 : readAddress.hashCode());
        result = prime * result + ((writeAddress == null) ? 0 : writeAddress.hashCode());
        return result;
    }

    public void setAccessType(ACCESSTYPE accessType) {
        this.accessType = accessType;
    }

    public void setDataType(DATATYPE dataType) {
        this.dataType = dataType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGatewayAddress(String gatewayAddress) {
        this.gatewayAddress = gatewayAddress;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReadAddress(String readAddress) {
        this.readAddress = readAddress;
    }

    public void setWriteAddress(String writeAddress) {
        this.writeAddress = writeAddress;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
