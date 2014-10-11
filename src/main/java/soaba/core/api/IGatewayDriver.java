package soaba.core.api;

import java.net.Inet4Address;
import java.util.List;

import soaba.core.exception.DatapointInvalidValueTypeException;
import soaba.core.exception.GatewayDriverException;
import soaba.core.models.Datapoint;

public interface IGatewayDriver {
    public void connect() throws GatewayDriverException;

    public void disconnect() throws GatewayDriverException;

    public List<Datapoint> getDatapoints();

    public <T> T readDatapoint(String datapointID) throws DatapointInvalidValueTypeException;

    public <T> void writeDatapoint(String datapointID, T value) throws DatapointInvalidValueTypeException;
    
    public Inet4Address getAddress();
    
    public Inet4Address setAddress();
}
