package soaba.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Message;
import org.restlet.Restlet;
import org.restlet.data.CacheDirective;
import org.restlet.data.Protocol;
import org.restlet.engine.header.Header;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.restlet.util.Series;

import soaba.core.gateways.drivers.KNXGatewayDriver;

public class RestletServer extends
        Application {

    private static final String ALLOW_ALL_FROM_ORIGIN = "*";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ROOT_URI = "/soaba";
    private static final int BOUND_BACKOFF_PERIOD = 15000;
    private final static int SERVER_PORT = 8095;
    private static final String HEADERS_KEY = "org.restlet.http.headers";
    private final static RestletServer singleton = new RestletServer();

    @SuppressWarnings("rawtypes")
    private static final Map<String, Class> resx = new HashMap<String, Class>();
    private static final Logger logger = Logger.getLogger(RestletServer.class);

    static {
        /**
         * Server Resources Routes
         */
        resx.put(KNXGatewayService.ListDatapoints.ROUTE_URI, KNXGatewayService.ListDatapoints.class);
        resx.put(KNXGatewayService.ListGateways.ROUTE_URI, KNXGatewayService.ListGateways.class);
        resx.put(KNXGatewayService.ReadDatapoint.ROUTE_URI, KNXGatewayService.ReadDatapoint.class);
        resx.put(KNXGatewayService.WriteDatapoint.ROUTE_URI, KNXGatewayService.WriteDatapoint.class);
        resx.put(KNXGatewayService.DiscoverRouters.ROUTE_URI, KNXGatewayService.DiscoverRouters.class);
        resx.put(KNXGatewayService.DiscoverDevices.ROUTE_URI, KNXGatewayService.DiscoverDevices.class);
        resx.put(KNXGatewayService.ProbeDatapointStatus.ROUTE_URI, KNXGatewayService.ProbeDatapointStatus.class);
        resx.put(KNXGatewayService.ReadDatapointFromGW.ROUTE_URI, KNXGatewayService.ReadDatapointFromGW.class);
    }

    public static void main(String[] args) throws InterruptedException {
        serverStart();
        printAvailableEndpoints();

        System.out.println("Press CTRL+D to exist RestletServer.");
        while (true) {
            try {
                if (System.in.read() == 4) {
                    RestletServer.getInstance().shutdown();
                    RestletServer.getInstance().stop();
                    break;
                }
            } catch (IOException e) {
                /* no catch */
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private RestletServer() {
        /* singleton class */
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized Restlet createInboundRoot() {
        final Router appRouter = new Router(getContext());
        for (String resxURI : resx.keySet())
            appRouter.attach(resxURI, (Class<ServerResource>) resx.get(resxURI));
        return appRouter;
    }

    public static RestletServer getInstance() {
        return singleton;
    }

    private static void serverStart() throws InterruptedException {
        boolean serverBound = false;

        while (!serverBound) {
            try {
                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    public void run() {
                        try {
                            RestletServer.getInstance().shutdown();
                        } catch (Exception e) {
                            logger.error("RestletServer Shutdown Hook :: " + e.getMessage(), e);
                        }
                    }
                }));

                // creating a new Restlet component w/ an HTTP server connector to it
                Component component = new Component();
                component.getServers().add(Protocol.HTTP, SERVER_PORT);
                component.getDefaultHost().attach(ROOT_URI, RestletServer.getInstance());

                // starting the component.
                // note that the HTTP server connector is also automatically started.
                component.start();
                serverBound = true;
            } catch (Exception e) {
                System.out.println(String.format("Port %d is busy! Trying again in %.2f secs.", SERVER_PORT,
                        (BOUND_BACKOFF_PERIOD / 1000.0)));
                Thread.sleep(BOUND_BACKOFF_PERIOD);
            }
        }
    }

    private static void printAvailableEndpoints() {
        final String addrStr = "   -> http://<hostname>:" + SERVER_PORT + ROOT_URI;
        System.out.println("\nSOABA REST API:");

        // sorting API routes
        String[] routes = new String[resx.keySet().size()];
        resx.keySet().toArray(routes);
        Arrays.sort(routes);

        // listing available routes
        for (String uri : routes)
            System.out.printf("%s%s%n", addrStr, uri);

        System.out.println();
    }

    @SuppressWarnings("unchecked")
    public static Series<Header> configureRestForm(Message message) {
        ConcurrentMap<String, Object> attrs = message.getAttributes();
        Series<Header> headers = (Series<Header>) attrs.get(HEADERS_KEY);

        if (headers == null) {
            headers = new Series<Header>(Header.class);
            Series<Header> prev = (Series<Header>) attrs.putIfAbsent(HEADERS_KEY, headers);

            if (prev != null)
                headers = prev;
        }

        headers.add(ACCESS_CONTROL_ALLOW_ORIGIN, ALLOW_ALL_FROM_ORIGIN);
        message.getCacheDirectives().add(CacheDirective.noCache());
        return headers;
    }

    public void shutdown() {
        // eventually here could be put some code that would iterate over all instances implementing
        // IGatewayDriver, and calling dispose over such instances. seems overkill for now.
        KNXGatewayDriver.dispose();
    }
}
