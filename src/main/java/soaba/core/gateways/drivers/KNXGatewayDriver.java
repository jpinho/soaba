package soaba.core.gateways.drivers;

import java.net.Inet4Address;
import java.util.List;

import soaba.core.api.IGatewayDriver;
import soaba.core.exception.DatapointInvalidValueTypeException;
import soaba.core.exception.GatewayDriverException;
import soaba.core.models.Datapoint;

public class KNXGatewayDriver implements IGatewayDriver{

    public void connect() throws GatewayDriverException {
        // TODO Auto-generated method stub
        
    }

    public void disconnect() throws GatewayDriverException {
        // TODO Auto-generated method stub
        
    }

    public List<Datapoint> getDatapoints() {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> T readDatapoint(String datapointID) throws DatapointInvalidValueTypeException {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> void writeDatapoint(String datapointID, T value) throws DatapointInvalidValueTypeException {
        // TODO Auto-generated method stub
        
    }

    public Inet4Address getAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    public Inet4Address setAddress() {
        // TODO Auto-generated method stub
        return null;
    }
   
}
