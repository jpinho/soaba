package soaba.services.rest;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

public class KNXGatewayService extends
        Application {

    private static Component component;
    private static String ROUTE_KNXGW_DEVICES = "/knxgw/devices";
    private static String ROUTE_KNXGW_DPREAD = "/knxgw/{datapointaddr}/{cmdtype}";
    private static String ROUTE_KNXGW_DPWRITE = "/knxgw/{datapointaddr}/{cmdtype}/{value}";
    private final static int SERVER_PORT = 8080;
    private final static KNXGatewayService singleton = new KNXGatewayService();
    
    
    private KNXGatewayService(){
    }
        
    @Override
    public Restlet createInboundRoot() {
        final Router router = new Router(getContext());

        // @GET
        router.attach(ROUTE_KNXGW_DEVICES, KNXGatewayEndpoints.ListDevices.class);

        // @PUT
        router.attach(ROUTE_KNXGW_DPWRITE, KNXGatewayEndpoints.ReadDatapoint.class);

        // @GET
        router.attach(ROUTE_KNXGW_DPREAD, KNXGatewayEndpoints.WriteDatapoint.class);

        return super.createInboundRoot();
    }
    
    
    private static KNXGatewayService getInstance(){
        return singleton;
    }
    
    private static void serverStart() throws InterruptedException {
        component = new Component();

        // Add a new HTTP server listening on default port.
        component.getServers().add(Protocol.HTTP, SERVER_PORT);
        boolean serverBound = false;

        while (!serverBound) {
            try {
                System.out.println("REST Service Initializing...");
                component.start();
                serverBound = true;
            } catch (Exception e) {
                System.out.println("Port " + SERVER_PORT + " is busy! Trying again in 15 sec.");
                Thread.sleep(15000);
            }
        }
    }

    private static void serverAttach() {
        component.getDefaultHost().attach(KNXGatewayService.getInstance());
    }

    private static void printAvailableEndpoints() {
        final String addrStr = "   -> http://<hostname>:" + SERVER_PORT;
        System.out.println("\nKNX GW REST API methods:");
        
        System.out.printf("%s%s%n", addrStr, ROUTE_KNXGW_DEVICES);
        System.out.printf("%s%s%n", addrStr, ROUTE_KNXGW_DPREAD);
        System.out.printf("%s%s%n", addrStr, ROUTE_KNXGW_DPWRITE);
        
        System.out.println();
    }
    
    public static void main(String[] args) throws InterruptedException {
        if (KNXGatewayEndpoints.start()==false) {
            printAvailableEndpoints();
            serverStart();
            serverAttach();
        } else {
            System.out.println("Server closing... Motive: Gateway is closed!");
            System.exit(-1);
        }
    }
}
