package soaba.core.api;

import java.net.UnknownHostException;
import java.util.List;

import javax.activation.UnsupportedDataTypeException;

import soaba.core.exception.DatapointInvalidValueTypeException;
import soaba.core.exception.DatapointReadonlyAccessTypeException;
import soaba.core.exception.DatapointWriteonlyAccessTypeException;
import soaba.core.exception.GatewayDriverException;
import soaba.core.models.DatapointValue;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.link.KNXLinkClosedException;

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

    boolean isAddressOccupied(String addr) throws GatewayDriverException, InterruptedException, KNXFormatException, KNXException;

    List<String> scanNetworkDevices(int area, int line) throws GatewayDriverException, KNXLinkClosedException,
            KNXTimeoutException,
            InterruptedException;

    List<String> scanNetworkRouters() throws KNXLinkClosedException, KNXTimeoutException, InterruptedException, GatewayDriverException;
}