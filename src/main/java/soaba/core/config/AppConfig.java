package soaba.core.config;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import soaba.core.api.IDatapoint;
import soaba.core.api.IGatewayDriver;
import soaba.core.gateways.drivers.KNXGatewayDriver;

public class AppConfig {
    @SuppressWarnings("unused")
    private static final String APP_CONFIG_FILE = "soaba.config";
    private static AppConfig instance;

    private AppConfig() {
    }

    public static AppConfig getInstance() {
        if (instance != null)
            return instance;
        return instance = new AppConfig().init();
    }

    public AppConfig init() {
        //TODO: read config from json file
        return this;
    }

    public List<IGatewayDriver> getGateways() throws UnknownHostException {
        List<IGatewayDriver> gateways = new ArrayList<IGatewayDriver>();
        gateways.add(new KNXGatewayDriver(Inet4Address.getByName("172.20.70.147")));
        gateways.add(new KNXGatewayDriver(Inet4Address.getByName("172.20.70.241")));
        return gateways;
    }

    public List<IDatapoint> getDatapoints() {
        return null;
    }
}
