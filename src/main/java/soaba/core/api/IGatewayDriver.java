package soaba.core.api;

import java.net.UnknownHostException;
import java.util.List;

import javax.activation.UnsupportedDataTypeException;

import soaba.core.exception.DatapointInvalidValueTypeException;
import soaba.core.exception.DatapointReadonlyAccessTypeException;
import soaba.core.exception.DatapointWriteonlyAccessTypeException;
import soaba.core.exception.GatewayDriverException;
import soaba.core.models.DatapointValue;

/**
 * This interface provides the abstraction for a Gateway Driver implementation.
 * 
 * @author João Pinho (jpe.pinho@gmail.com)
 * @since 0.5
 */
public interface IGatewayDriver {

    public abstract void connect() throws GatewayDriverException, UnknownHostException;

    public abstract void disconnect() throws GatewayDriverException;

    public abstract void reconnectGateway();

    public abstract DatapointValue<?> read(IDatapoint datapoint) throws GatewayDriverException,
            DatapointInvalidValueTypeException,
            DatapointWriteonlyAccessTypeException,
            DatapointReadonlyAccessTypeException,
            UnsupportedDataTypeException; 

    public abstract String getAddress();

    public abstract void setAddress(String address);

    public abstract void write(DatapointValue<?> value) throws GatewayDriverException,
            DatapointInvalidValueTypeException,
            DatapointWriteonlyAccessTypeException,
            DatapointReadonlyAccessTypeException,
            UnsupportedDataTypeException;

    boolean isAddressOccupied(String addr) throws GatewayDriverException, InterruptedException;

    List<String> scanNetworkDevices(int area, int line) throws GatewayDriverException, InterruptedException;

    List<String> scanNetworkRouters() throws InterruptedException, GatewayDriverException;
}