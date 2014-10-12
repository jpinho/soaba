package soaba.core.api;

import java.net.InetAddress;
import java.net.UnknownHostException;

import soaba.core.exception.DatapointInvalidValueTypeException;
import soaba.core.exception.GatewayDriverException;
import soaba.core.models.Datapoint;

public interface IGatewayDriver {
    public abstract void connect() throws GatewayDriverException, UnknownHostException;

    public abstract void disconnect() throws GatewayDriverException;

    public abstract void reconnectGateway();

    public abstract boolean readBool(Datapoint datapoint) throws GatewayDriverException,
            DatapointInvalidValueTypeException;

    public abstract float read2ByteFloat(Datapoint datapoint) throws GatewayDriverException,
            DatapointInvalidValueTypeException;

    public abstract String readString(Datapoint datapoint) throws GatewayDriverException,
            DatapointInvalidValueTypeException;

    public abstract float readPercentage(Datapoint datapoint) throws GatewayDriverException,
            DatapointInvalidValueTypeException;

    public abstract void writeBool(Datapoint datapoint, boolean value) throws DatapointInvalidValueTypeException,
            GatewayDriverException;

    public abstract void write2ByteFloat(Datapoint datapoint, float value) throws DatapointInvalidValueTypeException,
            GatewayDriverException;

    public abstract void writeString(Datapoint datapoint, String value) throws DatapointInvalidValueTypeException,
            GatewayDriverException;

    public abstract void writePercentage(Datapoint datapoint, int value) throws DatapointInvalidValueTypeException,
            GatewayDriverException;

    public abstract InetAddress getAddress();

    public abstract void setAddress(InetAddress address);
}
