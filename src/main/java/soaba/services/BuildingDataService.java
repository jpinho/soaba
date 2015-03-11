package soaba.services;

import java.net.UnknownHostException;
import java.nio.file.AccessDeniedException;
import java.util.List;

import javax.activation.UnsupportedDataTypeException;

import org.apache.log4j.Logger;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import soaba.core.api.IDatapoint;
import soaba.core.api.IDatapoint.ACCESSTYPE;
import soaba.core.api.IDatapoint.DATATYPE;
import soaba.core.api.IGatewayDriver;
import soaba.core.config.AppConfig;
import soaba.core.exception.DatapointInvalidValueTypeException;
import soaba.core.exception.DatapointReadonlyAccessTypeException;
import soaba.core.exception.DatapointWriteonlyAccessTypeException;
import soaba.core.exception.GatewayDriverException;
import soaba.core.exception.ServiceResourceErrorException;
import soaba.core.gateways.drivers.KNXGatewayDriver;
import soaba.core.models.Datapoint;
import soaba.core.models.DatapointValue;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import flexjson.JSONSerializer;

/**
 * The Building Data Service provides ways for interacting with gateways and datapoints.
 * 
 * @author João Pinho (jpe.pinho@gmail.com)
 * @since 0.5
 */
public class BuildingDataService {

    private static AppConfig config = AppConfig.getInstance();

    private static String toJSON(Object obj) {
        return new JSONSerializer().deepSerialize(obj);
    }

    /**
     * Lists all datapoints configured in the application.
     */
    public static class ListDatapoints extends
            ServerResource {

        public final static String ROUTE_URI = "/datapoints";

        @Get
        public String doGet() throws AccessDeniedException {
            RestletServer.configureRestForm(getResponse());
            JSONSerializer serializer = new JSONSerializer();
            return serializer.deepSerialize(config.getDatapoints());
        }
    }

    /**
     * Lists all gateways configured in the application.
     */
    public static class ListGateways extends
            ServerResource {

        public final static String ROUTE_URI = "/gateways";

        @Get
        public String doGet() {
            RestletServer.configureRestForm(getResponse());
            JSONSerializer serializer = new JSONSerializer();
            return serializer.deepSerialize(config.getGateways());
        }
    }

    /**
     * Checks if a given datapoint is busy for read/write operations.
     */
    public static final class ProbeDatapointStatus extends
            ServerResource {

        public final static String ROUTE_URI = "/management/probesbusystatus/{datapointaddr}";

        @SuppressWarnings("unused")
        @Get("json")
        public String doGet() throws GatewayDriverException,
                DatapointInvalidValueTypeException,
                UnknownHostException,
                DatapointReadonlyAccessTypeException,
                UnsupportedDataTypeException,
                DatapointWriteonlyAccessTypeException,
                InterruptedException,
                KNXFormatException,
                KNXException {
            RestletServer.configureRestForm(getResponse());

            final String dpointAddress = getRequest().getAttributes().get("datapointaddr").toString();
            final IDatapoint dpoint = config.findDatapoint(dpointAddress.replace('.', '/'));
            final IGatewayDriver gateway = config.findGateway(dpoint.getGatewayAddress());

            if (dpoint == null)
                return toJSON(new ServiceResourceErrorException("Datapoint not found."));

            if (gateway == null)
                return toJSON(new ServiceResourceErrorException("Gateway not found."));

            gateway.connect();
            Boolean result = gateway.isAddressOccupied(dpointAddress);
            gateway.disconnect();

            return toJSON(result);
        }
    }

    /**
     * Scans network devices on the field bus of a given gateway.
     */
    public static final class DiscoverDevices extends
            ServerResource {

        public final static String ROUTE_URI = "/management/{gateway_address}/discover/devices/{area}/{line}";

        @Get("json")
        public String doGet() throws GatewayDriverException,
                DatapointInvalidValueTypeException,
                UnknownHostException,
                DatapointReadonlyAccessTypeException,
                UnsupportedDataTypeException,
                DatapointWriteonlyAccessTypeException,
                KNXLinkClosedException,
                KNXTimeoutException,
                InterruptedException {
            RestletServer.configureRestForm(getResponse());

            final String gwAddress = getRequest().getAttributes().get("gateway_address").toString();
            final String area = getRequest().getAttributes().get("area").toString();
            final String line = getRequest().getAttributes().get("line").toString();
            final IGatewayDriver gateway = config.findGateway(gwAddress);

            if (gateway == null)
                return toJSON(new ServiceResourceErrorException("Gateway not found."));

            gateway.connect();
            List<String> result = gateway.scanNetworkDevices(Integer.valueOf(area), Integer.valueOf(line));
            gateway.disconnect();

            return toJSON(result);
        }
    }

    /**
     * Scans network routers on the field bus of a given gateway.
     */
    public static final class DiscoverRouters extends
            ServerResource {

        public final static String ROUTE_URI = "/management/{gateway_address}/discover/routers";

        @Get("json")
        public String doGet() throws GatewayDriverException,
                DatapointInvalidValueTypeException,
                UnknownHostException,
                DatapointReadonlyAccessTypeException,
                UnsupportedDataTypeException,
                DatapointWriteonlyAccessTypeException,
                KNXLinkClosedException,
                KNXTimeoutException,
                InterruptedException {
            RestletServer.configureRestForm(getResponse());

            final String gwAddress = getRequest().getAttributes().get("gateway_address").toString();
            final IGatewayDriver gateway = config.findGateway(gwAddress);

            if (gateway == null)
                return toJSON(new ServiceResourceErrorException("Gateway not found."));

            gateway.connect();
            List<String> result = gateway.scanNetworkRouters();
            gateway.disconnect();

            return toJSON(result);
        }
    }

    /**
     * Reads the datapoint value of the specified datapoint address.
     */
    public static final class ReadDatapoint extends
            ServerResource {

        public final static String ROUTE_URI = "/datapoints/{datapointaddr}";

        @SuppressWarnings("unused")
        @Get("json")
        public String doGet() {
            RestletServer.configureRestForm(getResponse());

            final String dpointAddress = getRequest().getAttributes().get("datapointaddr").toString();
            final IDatapoint dpoint = config.findDatapoint(dpointAddress.replace('.', '/'));
            
            if(dpoint == null){
                Logger.getLogger(BuildingDataService.class).error("datapoint not found.");
                return toJSON(new ServiceResourceErrorException("datapoint not found"));
            }
                
            Logger.getLogger(BuildingDataService.class).info("gw address:" + dpoint.getGatewayAddress());
            
            final IGatewayDriver gateway = config.findGateway(dpoint.getGatewayAddress());

            if (dpoint == null)
                return toJSON(new ServiceResourceErrorException("Datapoint not found."));

            if (gateway == null)
                return toJSON(new ServiceResourceErrorException("Gateway not found."));

            try {
                Logger.getLogger(BuildingDataService.class).info("calling ReadDatapoint service.");
                DatapointValue<?> result;
                gateway.connect();

                result = gateway.read(dpoint);

                gateway.disconnect();
                Logger.getLogger(BuildingDataService.class).info("returning from ReadDatapoint service.");
                return toJSON(result);
            } catch (Exception e) {
                return toJSON(new ServiceResourceErrorException(e));
            }
        }
    }

    /**
     * Writes the datapoint value into the specified datapoint address.
     */
    public static final class WriteDatapoint extends
            ServerResource {

        public final static String ROUTE_URI = "/datapoints/{datapointaddr}/{value}";

        @SuppressWarnings("unused")
        @Get("json")
        public String doPut(Representation res) throws ServiceResourceErrorException,
                DatapointReadonlyAccessTypeException,
                UnknownHostException,
                GatewayDriverException,
                UnsupportedDataTypeException,
                DatapointInvalidValueTypeException,
                DatapointWriteonlyAccessTypeException {

            /**
             * Authentication Sample
             * (under development)
             * 
                RestletServer app = (soaba.services.RestletServer) getApplication();
                if (!app.authenticate(getRequest(), getResponse())) {
                    // not authenticated
                    getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
                    return null;
                }
            */
            
            RestletServer.configureRestForm(getResponse());
            final String dpointAddress = getRequest().getAttributes().get("datapointaddr").toString();
            final String dpointValue = getRequest().getAttributes().get("value").toString();
            final IDatapoint dpoint = config.findDatapoint(dpointAddress.replace('.', '/'));
            final IGatewayDriver gateway = config.findGateway(dpoint.getGatewayAddress());

            if (dpoint == null)
                throw new ServiceResourceErrorException("Datapoint not found.");

            if (gateway == null)
                throw new ServiceResourceErrorException("Gateway not found.");

            try {
                DatapointValue<?> value = DatapointValue.build(dpoint);
                value.setValue(dpointValue);

                gateway.connect();
                gateway.write(value);
                gateway.disconnect();
                return null;
            } catch (Exception e) {
                return toJSON(new ServiceResourceErrorException(e));
            }
        }
    }

    /**
     * Reads the datapoint value from the specied datapoint address through a specific gatway address.
     */
    public static final class ReadDatapointFromGW extends
            ServerResource {

        public final static String ROUTE_URI = "/datapoints/{gwaddr}/{datapointaddr}/{datapointtype}";

        @Get("json")
        public String doGet() throws DatapointInvalidValueTypeException,
                UnknownHostException,
                DatapointReadonlyAccessTypeException,
                UnsupportedDataTypeException,
                DatapointWriteonlyAccessTypeException {
            RestletServer.configureRestForm(getResponse());

            final String gwAddress = getRequest().getAttributes().get("gwaddr").toString();
            final String dpAddress = getRequest().getAttributes().get("datapointaddr").toString().replace(".", "/");
            final String dataType = getRequest().getAttributes().get("datapointtype").toString().toUpperCase();
            DATATYPE dpType;

            try {
                dpType = DATATYPE.valueOf(dataType);
            } catch (Exception e) {
                dpType = DATATYPE.UNKNOWN;
            }

            final IDatapoint dpoint = new Datapoint(gwAddress, "dp", ACCESSTYPE.READ_ONLY, dpType, dpAddress, null);
            final IGatewayDriver gateway = new KNXGatewayDriver(gwAddress);

            DatapointValue<?> result;

            try {
                gateway.connect();
                result = gateway.read(dpoint);
                gateway.disconnect();
            } catch (GatewayDriverException e) {
                return toJSON(e);
            }

            return toJSON(result);
        }
    }
    
    /**
     * Releases all allocated resources, for this service.
     */
    public static void dispose(){
        KNXGatewayDriver.dispose();
    }
}
