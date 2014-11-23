package soaba.core.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soaba.core.api.IDatapoint;
import soaba.core.api.IDatapoint.ACCESSTYPE;
import soaba.core.api.IDatapoint.DATATYPE;
import soaba.core.api.IGatewayDriver;
import soaba.core.gateways.drivers.KNXGatewayDriver;
import soaba.core.models.Datapoint;
import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class AppConfig {
    private static final String GATEWAY_NUCLEUS_14 = "172.20.70.241";
    private static final String GATEWAY_LAB_158 = "172.20.70.209";
    private static final String APP_CONFIG_FILE = "resources/soaba.config";
    private static AppConfig instance;
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @JSON(include = true)
    private List<IGatewayDriver> gateways = new ArrayList<IGatewayDriver>();

    @JSON(include = true)
    private List<IDatapoint> datapoints = new ArrayList<IDatapoint>();
    
    private AppConfig() {
        /* singleton class */
    }

    public static AppConfig getInstance() {
        if (instance != null)
            return instance;
        return instance = new AppConfig().init();
    }

    public AppConfig init() {
        logger.info("calling AppConfig#init()");
        
        // loads the config from disk if it exists
        File f = new File(APP_CONFIG_FILE);

        if (f.exists()){
            logger.info("AppConfig#init() :: configuration found on disk, loading from file.");
            return AppConfig.load();
        }
        
        logger.info("AppConfig#init() :: configuration not found, generating new file to disk.");
        String gwNucleus14 = null;
        String gwLab158 = null;

        /**
         * Gateways Registration
         */
        gateways.add(new KNXGatewayDriver("KNX Gateway Lab 1.58", gwLab158 = GATEWAY_LAB_158));
        gateways.add(new KNXGatewayDriver("KNX Gateway Nucleus 14", gwNucleus14 = GATEWAY_NUCLEUS_14));


        /**
         * Datapoints Registration
         */
        String prefix = null;
        
        /** MIT - LAB 1.58 **/
        if (gwLab158 != null) {
            // lights
            prefix = "EnergyLab ";
            datapoints.add(new Datapoint(gwLab158, prefix + "All Lights", ACCESSTYPE.WRITE_ONLY, DATATYPE.PERCENTAGE, null, "0/1/8"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Light Blackboard", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/7/1", "0/1/0"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Light Middle1", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/7/21", "0/1/2"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Light Middle2", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/7/41", "0/1/4"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Light TV", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/7/61", "0/1/6"));

            // blinds
            datapoints.add(new Datapoint(gwLab158, prefix + "All Blinds", ACCESSTYPE.WRITE_ONLY, DATATYPE.BIT, null, "0/2/12"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Blind1", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/2/0", "0/2/3"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Blind2", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/2/13", "0/2/6"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Blind3", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/2/14", "0/2/9"));

            // door
            datapoints.add(new Datapoint(gwLab158, prefix + "Door", ACCESSTYPE.WRITE_ONLY, DATATYPE.BIT, null, "0/3/0"));

            // meteo station sensors
            datapoints.add(new Datapoint(gwLab158, prefix + "CO2", ACCESSTYPE.READ_ONLY, DATATYPE.NUMBER, "0/4/0", null));
            datapoints.add(new Datapoint(gwLab158, prefix + "Humidity", ACCESSTYPE.READ_ONLY, DATATYPE.NUMBER, "0/4/1", null));
            datapoints.add(new Datapoint(gwLab158, prefix + "Temperature", ACCESSTYPE.READ_ONLY, DATATYPE.NUMBER, "0/4/3", null));
            datapoints.add(new Datapoint(gwLab158, prefix + "Temperature Door", ACCESSTYPE.READ_ONLY, DATATYPE.NUMBER, "0/4/5", null));
            datapoints.add(new Datapoint(gwLab158, prefix + "Lux", ACCESSTYPE.READ_ONLY, DATATYPE.NUMBER, "0/4/4", null));

            // hvac
            datapoints.add(new Datapoint(gwLab158, prefix + "HVAC ONOFF", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "1/0/8", "1/0/0"));
            datapoints.add(new Datapoint(gwLab158, prefix + "HVAC Mode", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "1/0/9", "1/0/1"));

            // meteo station (bus Q.E. floor 1)
            prefix = "Meteo Station BUS[Q.E] Floor1 - ";
            datapoints.add(new Datapoint(gwLab158, prefix + "Luminosity - East Sensor", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/5", null));
            datapoints.add(new Datapoint(gwLab158, prefix + "Luminosity - South Sensor", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/6", null));
            datapoints.add(new Datapoint(gwLab158, prefix + "Luminosity - West Sensor", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/7", null));
            datapoints.add(new Datapoint(gwLab158, prefix + "Luminosity - Crepuscular Sensor", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/8", null));
            datapoints.add(new Datapoint(gwLab158, prefix + "Wind Speed Warn Interval", ACCESSTYPE.READ_WRITE, DATATYPE.TINY_NUMBER, "0/6/9", "0/6/9"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Wind Speed Sensor", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/10", "0/6/10"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Outside Temp. Sensor", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/11", "0/6/11"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Rain Sensor", ACCESSTYPE.READ_ONLY, DATATYPE.BIT, "0/6/13", "0/6/13"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Outside Temp. Sensor Precision", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/16", "0/6/16"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Max. Temp Reached Precision", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/19", "0/6/19"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Min. Temp Reached Precision", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/20", "0/6/20"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Relative Hum. Sensor Precision", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/22", "0/6/22"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Drew Point", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/25", "0/6/25"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Absolute Humidity", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/27", "0/6/27"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Exterior Entalpia", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/28", "0/6/28"));
            datapoints.add(new Datapoint(gwLab158, prefix + "Global Solar Radiation Sensor", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/6/29", "0/6/29"));
        }

        /** MIT - NUCLEUS 14 **/
        if (gwNucleus14 != null) {
            // lights
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - All Lights", ACCESSTYPE.WRITE_ONLY, DATATYPE.BIT, null, "0/0/1"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.02 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/2", "0/0/2"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.04 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/3", "0/0/3"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.06 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/4", "0/0/4"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.08 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/5", "0/0/5"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.10 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/6", "0/0/6"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.12 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/7", "0/0/7"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.14 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/8", "0/0/8"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.16 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/9", "0/0/9"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.18 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/10", "0/0/10"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.20 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/11", "0/0/11"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.22 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/12", "0/0/12"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.24 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/13", "0/0/13"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.26 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/14", "0/0/14"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.28 - Lights", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/0/15", "0/0/15"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.1.1E/2E - Lights Circuit", ACCESSTYPE.WRITE_ONLY, DATATYPE.BIT, null, "0/0/16"));


            // energy and general purpose sensors
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Energy Meter - Circ. A - Hall Lights", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/2/0", null));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Energy Meter - Circ. B - Hall Lights", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/2/1", null));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Energy Meter - Circ. C - HVAC Supply", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/2/2", null));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Energy Meter - Circ. D - HVAC Supply", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/2/3", null));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Energy Time Counter - Circ. A - Hall Lights", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/2/4", null));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Energy Time Counter - Circ. B - Hall Lights", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/2/5", null));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Energy Time Counter - Circ. C - HVAC Supply", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/2/6", null));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Energy Time Counter - Circ. D - HVAC Supply", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/2/7", null));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Status - Circ. A - Hall Lights", ACCESSTYPE.READ_ONLY, DATATYPE.BIT, "0/2/12", null));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Status - Circ. B - Hall Lights", ACCESSTYPE.READ_ONLY, DATATYPE.BIT, "0/2/13", null));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Status - Circ. C - HVAC Supply", ACCESSTYPE.READ_ONLY, DATATYPE.BIT, "0/2/14", null));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Status - Circ. D - HVAC Supply", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "0/2/15", "0/2/15"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Luminosity - Hall - North Sensor", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/2/16", null));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Luminosity - Hall - Middle Sensor", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/2/17", null));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14 - Luminosity - Hall - South Sensor", ACCESSTYPE.READ_ONLY, DATATYPE.TINY_NUMBER, "0/2/18", null));
            
            
            // hvac hot H2O valves
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.02 - HVAC - Hot H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/14", "0/1/14"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.04 - HVAC - Hot H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/15", "0/1/15"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.06 - HVAC - Hot H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/16", "0/1/16"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.08 - HVAC - Hot H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/17", "0/1/17"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.10 - HVAC - Hot H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/18", "0/1/18"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.12 - HVAC - Hot H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/19", "0/1/19"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.14 - HVAC - Hot H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/20", "0/1/20"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.16 - HVAC - Hot H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/21", "0/1/21"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.18 - HVAC - Hot H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/22", "0/1/22"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.20 - HVAC - Hot H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/23", "0/1/23"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.24 - HVAC - Hot H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/24", "0/1/24"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.26 - HVAC - Hot H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/25", "0/1/25"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.28 - HVAC - Hot H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/26", "0/1/26"));

            
            // hvac cold H2O valves
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.02 - HVAC - Cold H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/27", "0/1/27"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.04 - HVAC - Cold H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/28", "0/1/28"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.06 - HVAC - Cold H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/29", "0/1/29"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.08 - HVAC - Cold H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/30", "0/1/30"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.10 - HVAC - Cold H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/31", "0/1/31"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.12 - HVAC - Cold H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/32", "0/1/32"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.14 - HVAC - Cold H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/33", "0/1/33"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.16 - HVAC - Cold H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/34", "0/1/34"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.18 - HVAC - Cold H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/35", "0/1/35"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.20 - HVAC - Cold H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/36", "0/1/36"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.24 - HVAC - Cold H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/37", "0/1/37"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.26 - HVAC - Cold H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/38", "0/1/38"));
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.28 - HVAC - Cold H2O Valve", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/1/39", "0/1/39"));
            
            //TODO: add more datapoints of N14
        }

        // persists the config file to disk
        AppConfig.save(this);

        return this;
    }

    public List<IGatewayDriver> getGateways() {
        return gateways;
    }

    public List<IDatapoint> getDatapoints() {
        return datapoints;
    }

    /**
     * Stores the AppConfig to disk persistence.
     * 
     * @param theConfig the configuration file to persist in disk
     */
    private static void save(AppConfig theConfig) {
        JSONSerializer serializer = new JSONSerializer().prettyPrint(true);
        try {
            FileWriter writer = new FileWriter(APP_CONFIG_FILE);
            serializer.deepSerialize(theConfig, writer);
            writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            logger.error("AppConfig#save(theConfig)", e);
        }
    }

    /**
     * Loads AppConfig from disk persistence.
     */
    private static AppConfig load() {
        JSONDeserializer<AppConfig> serializer = new JSONDeserializer<AppConfig>();
        AppConfig result = null;
        try {
            result = serializer
                    .use("datapoints", ArrayList.class)
                    .use("datapoints.values", Datapoint.class)
                    .use("gateways", ArrayList.class)
                    .use("gateways.values", KNXGatewayDriver.class)
                    .deserialize(new String(Files.readAllBytes(java.nio.file.Paths.get(APP_CONFIG_FILE))));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            logger.error("AppConfig#load()", e);
        }
        return result;
    }

    public IDatapoint findDatapoint(String dpointAddress) {
        for (IDatapoint dp : datapoints)
            if (dp.getId().equals(dpointAddress))
                return dp;

        return null;
    }

    public IGatewayDriver findGateway(String gatewayAddress) {
        for (IGatewayDriver gateway : gateways)
            if (gateway.getAddress().equalsIgnoreCase(gatewayAddress))
                return gateway;

        return null;
    }

    public void setGateways(List<IGatewayDriver> gateways) {
        this.gateways = gateways;
    }

    public void setDatapoints(List<IDatapoint> datapoints) {
        this.datapoints = datapoints;
    }
}
