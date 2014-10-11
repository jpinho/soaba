package soaba.core;

import java.net.Inet4Address;
import java.util.List;

import soaba.core.api.IDatapoint;
import soaba.core.api.IGatewayDriver;
import soaba.core.exception.DatapointInvalidValueTypeException;

public class GatewayManager {
    private static GatewayManager instance;

    private GatewayManager() { /* singleton */
    }

    public static GatewayManager getInstance() {
        if (instance != null)
            return instance;
        return instance = new GatewayManager();
    }

    public List<IGatewayDriver> getGateways() {
        return null;
    }

    public List<IDatapoint> getDatapoints(IGatewayDriver gateway) {
        return null;
    }

    public <T> T readDatapoint(Inet4Address gatewayID, String datapointID) throws DatapointInvalidValueTypeException {
        return null;
    }

    public <T> void writeDatapoint(Inet4Address gatewayID, String datapointID, T value) throws DatapointInvalidValueTypeException {
        //TODO
    }
}
