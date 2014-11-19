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
    private static final String GATEWAY_LAB_158 = "172.20.70.242";
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


        /** MIT - LAB 1.58 **/
        if (gwLab158 != null) {
            // lights
            datapoints.add(new Datapoint(gwLab158, "EnergyLab All Lights", ACCESSTYPE.WRITE_ONLY, DATATYPE.PERCENTAGE, null, "0/1/8"));
            datapoints.add(new Datapoint(gwLab158, "EnergyLab Light Blackboard", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/7/1", "0/1/0"));
            datapoints.add(new Datapoint(gwLab158, "EnergyLab Light Middle1", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/7/21", "0/1/2"));
            datapoints.add(new Datapoint(gwLab158, "EnergyLab Light Middle2", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/7/41", "0/1/4"));
            datapoints.add(new Datapoint(gwLab158, "EnergyLab Light TV", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/7/61", "0/1/6"));

            // blinds
            datapoints.add(new Datapoint(gwLab158, "EnergyLab All Blinds", ACCESSTYPE.WRITE_ONLY, DATATYPE.BIT, null, "0/2/12"));
            datapoints.add(new Datapoint(gwLab158, "EnergyLab Blind1", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/2/0", "0/2/3"));
            datapoints.add(new Datapoint(gwLab158, "EnergyLab Blind2", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/2/13", "0/2/6"));
            datapoints.add(new Datapoint(gwLab158, "EnergyLab Blind3", ACCESSTYPE.READ_WRITE, DATATYPE.PERCENTAGE, "0/2/14", "0/2/9"));

            // door
            datapoints.add(new Datapoint(gwLab158, "EnergyLab Door", ACCESSTYPE.WRITE_ONLY, DATATYPE.BIT, null, "0/3/0"));

            // meteo station sensors
            datapoints.add(new Datapoint(gwLab158, "EnergyLab CO2", ACCESSTYPE.READ_ONLY, DATATYPE.NUMBER, "0/4/0", null));
            datapoints.add(new Datapoint(gwLab158, "EnergyLab Humidity", ACCESSTYPE.READ_ONLY, DATATYPE.NUMBER, "0/4/1", null));
            datapoints.add(new Datapoint(gwLab158, "EnergyLab Temperature", ACCESSTYPE.READ_ONLY, DATATYPE.NUMBER, "0/4/3", null));
            datapoints.add(new Datapoint(gwLab158, "EnergyLab Temperature Door", ACCESSTYPE.READ_ONLY, DATATYPE.NUMBER, "0/4/5", null));
            datapoints.add(new Datapoint(gwLab158, "EnergyLab Lux", ACCESSTYPE.READ_ONLY, DATATYPE.NUMBER, "0/4/4", null));

            // hvac
            datapoints.add(new Datapoint(gwLab158, "EnergyLab HVAC ONOFF", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "1/0/8", "1/0/0"));
            datapoints.add(new Datapoint(gwLab158, "EnergyLab HVAC Mode", ACCESSTYPE.READ_WRITE, DATATYPE.BIT, "1/0/9", "1/0/1"));

            //TODO: add more datapoints of Lab158
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
            datapoints.add(new Datapoint(gwNucleus14, "2-N14.1.1E/2E - Lights Circuit", ACCESSTYPE.WRITE_ONLY, DATATYPE.BIT, "0/0/16", "0/0/16"));

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
