package soaba.core.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soaba.core.api.IDatapoint;
import soaba.core.api.IGatewayDriver;
import soaba.core.gateways.drivers.KNXGatewayDriver;
import soaba.core.models.Datapoint;
import soaba.core.models.Session;
import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * This class provides application-wide persisted configurations to a JSON file,
 * ultimately persisted on disk storage. 
 * 
 * @author João Pinho (jpe.pinho@gmail.com)
 * @since 0.5
 */
public class AppConfig {

    private static final String APP_CONFIG_FILE = "resources/soaba.config";
    private static AppConfig instance;
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    
    @JSON(include = false)
    private Map<String, Session> sessions = new HashMap<String, Session>(); 
    
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
        
        /**
         * Attach your configuration here.
         * You can remove EnergyLabSetup with your in code configurations.
         */
        EnergyLabSetup.attachConfig(gateways, datapoints);

        // loads the config from disk if it exists
        File f = new File(APP_CONFIG_FILE);

        if (f.exists()){
            logger.info("AppConfig#init() :: configuration found on disk, loading from file.");
            
            AppConfig config = AppConfig.load();
            
            // DEVELOPER MODE: use this code if typically changes are made on the code itself.
            //logger.info("AppConfig#init() :: updating disk configuration with code based configuration.");
            //config.update(this);
            //logger.info("AppConfig#init() :: file system configuration updated.");

            // PRODUCTION MODE: or use this code if typically changes are made on the running app config file.
            logger.info("AppConfig#init() :: updating code based configuration with disk configuration.");
            this.update(config);
            logger.info("AppConfig#init() :: configuration updated.");
            
            logger.info(
                    String.format("AppConfig#init() :: configuration summary%n - datapoints count: %d%n - gateways count: %d",
                    config.datapoints != null ? config.datapoints.size() : 0, 
                    config.gateways != null ? config.gateways.size() : 0));
            
            AppConfig.save(config);
            return config;
        }
        
        // persists the config file to disk
        logger.info("AppConfig#init() :: configuration not found, generating new file to disk.");
        AppConfig.save(this);
        return this;
    }

    public List<IGatewayDriver> getGateways() {
        return gateways;
    }

    public List<IDatapoint> getDatapoints() {
        return datapoints;
    }

    public void update(AppConfig newConfig){
        for(IDatapoint newDP : newConfig.datapoints){
            IDatapoint origin = findDatapointByName(newDP.getName());
            
            if(origin != null){
                logger.info(String.format("AppConfig#update() :: updating datapoint '%s'.", newDP.getName()));
                origin.setDisplayName(newDP.getDisplayName());
                origin.setDescription(newDP.getDescription());
                origin.setAccessType(newDP.getAccessType());
                origin.setDataType(newDP.getDataType());
                origin.setGatewayAddress(newDP.getGatewayAddress());
                origin.setReadAddress(newDP.getReadAddress());
                origin.setWriteAddress(newDP.getWriteAddress());
                origin.setUnit(newDP.getUnit());
            }
            else {
                logger.info(String.format("AppConfig#update() :: adding new datapoint '%s'.", newDP.getName()));
                this.datapoints.add(newDP);
            }
        }
    }
    
    /**
     * Stores the AppConfig to disk persistence.
     * 
     * @param theConfig the configuration file to persist in disk
     */
    private static void save(AppConfig theConfig) {
        JSONSerializer serializer = new JSONSerializer()
            .transform(new ExcludeTransformer(), void.class)
            .prettyPrint(true);
        try {
            FileWriter writer = new FileWriter(APP_CONFIG_FILE);
            serializer
                .deepSerialize(theConfig, writer);
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

    /**
     * Searchs for a datapoint by their id, read address or write address, the first to match
     * returns the underlyining datapoint.
     * 
     * @param dpointIdOrAddress, the id, read address or write address of the datapoint to be found
     * @return the datapoint found or null if none matches the query
     */
    public IDatapoint findDatapoint(String dpointIdOrAddress) {        
        for (IDatapoint dp : datapoints)
            if (dp.getId().equals(dpointIdOrAddress) || 
               (dp.getReadAddress() != null && dp.getReadAddress().equals(dpointIdOrAddress)) || 
               (dp.getWriteAddress() != null && dp.getWriteAddress().equals(dpointIdOrAddress)))
               return dp;
        return null;
    }
    
    public IDatapoint findDatapointByName(String name) {        
        for (IDatapoint dp : datapoints)
            if (dp.getName().equalsIgnoreCase(name))
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

    public Map<String, Session> getSessions() {
        return sessions;
    }

    public void setSessions(Map<String, Session> sessions) {
        this.sessions = sessions;
    }
}
