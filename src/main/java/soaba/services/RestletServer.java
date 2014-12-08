package soaba.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Message;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.CacheDirective;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Cookie;
import org.restlet.data.Protocol;
import org.restlet.engine.header.Header;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MapVerifier;
import org.restlet.util.Series;

/**
 * Responsible for launching SOABA REST Service, this class orchestrates all server resources 
 * available through REST interfaces. This class creates the main endpoint for accessing SOABA, 
 * and ensures all content exchange is made via JSON only.
 * 
 * @author João Pinho (jpe.pinho@gmail.com)
 * @since 0.5
 */
public class RestletServer extends
        Application {

    final static String ROOT_URI = "/soaba";
    final static int SERVER_PORT = 9095;
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ALLOW_ALL_FROM_ORIGIN = "*";
    
    /**
     * The exponential back-off base period between retries when bounding the endpoint to an 
     * already in use port.
     */
    private static final int BOUND_BACKOFF_PERIOD = 15000;
    private static final String HEADERS_KEY = "org.restlet.http.headers";
    private static final Logger logger = Logger.getLogger(RestletServer.class);
    
    /**
     * The map between server resources and their REST URIs.
     */
    @SuppressWarnings("rawtypes")
    private static final Map<String, Class> resx = new HashMap<String, Class>();
    private final static RestletServer singleton = new RestletServer();
    
    static {
        /**
         * Server Resources Routes
         */
        resx.put(BuildingDataService.ListDatapoints.ROUTE_URI, BuildingDataService.ListDatapoints.class);
        resx.put(BuildingDataService.ListGateways.ROUTE_URI, BuildingDataService.ListGateways.class);
        resx.put(BuildingDataService.ReadDatapoint.ROUTE_URI, BuildingDataService.ReadDatapoint.class);
        resx.put(BuildingDataService.WriteDatapoint.ROUTE_URI, BuildingDataService.WriteDatapoint.class);
        resx.put(BuildingDataService.DiscoverRouters.ROUTE_URI, BuildingDataService.DiscoverRouters.class);
        resx.put(BuildingDataService.DiscoverDevices.ROUTE_URI, BuildingDataService.DiscoverDevices.class);
        resx.put(BuildingDataService.ProbeDatapointStatus.ROUTE_URI, BuildingDataService.ProbeDatapointStatus.class);
        resx.put(BuildingDataService.ReadDatapointFromGW.ROUTE_URI, BuildingDataService.ReadDatapointFromGW.class);
    }

    /**
     * Prints all routes to 'System.out' exposed by this application.
     */
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

    /**
     * Starts this application, which envolves launching an embedded HTTP server and binding it to 
     * a predefined server port.
     */
    private static void serverStart() {
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
                System.out.println(String.format("Port %d is busy! Trying again in %.2f secs.", 
                        SERVER_PORT, (BOUND_BACKOFF_PERIOD / 1000.0)));
                try {
                    Thread.sleep(BOUND_BACKOFF_PERIOD);
                } catch (InterruptedException ex) {
                    // thread interrupted, exit normally.
                    return;
                }
            }
        }
    }

    /**
     * Configures REST HTTP Request Forms.
     * 
     * @param message the HTTP message to setup
     * @return the message configured HTTP headers.
     */
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

    /**
     * This application instance.
     * @return the current application
     */
    public static RestletServer getInstance() {
        return singleton;
    }
    
    /**
     * The application available routes.
     * @return the application routes
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, Class> getRoutes() {
        return resx;
    }

    /**
     * When called as main class, this method launches the Restlet Server.
     * 
     * @param args the command line arguments passed by the user
     */
    public static void main(String[] args) {
        serverStart();
        printAvailableEndpoints();

        System.out.println("Press ENTER to exit [SOABA Rest Server]");
        while (true) {
            try {
                int c = System.in.read();
                if (c == 10) {
                    RestletServer.getInstance().shutdown();
                    RestletServer.getInstance().stop();
                    break;
                }
            } catch (IOException e) {
                System.exit(-2);
                return;
            } catch (Exception e) {
                logger.error(e);
                System.exit(-1);
                return;
            }
        }

        System.out.println("Chillout time for my bits... Goodbye Human!");
        System.exit(0);
    }
    
    /**
     * Used for authentication of server resources.
     */
    private ChallengeAuthenticator authenticator;

    private RestletServer() {
        /* singleton class */
    }

    /**
     * Setups a Challenge Authenticator for this RestletServer resources.
     * 
     * @return the challenge authenticator properly configured
     */
    private ChallengeAuthenticator createAuthenticator() {
        Context context = getContext();
        boolean optional = true;
        ChallengeScheme challengeScheme = ChallengeScheme.HTTP_BASIC;
        String realm = "SOABA_REALM";

        MapVerifier verifier = new MapVerifier();
        verifier.getLocalSecrets().put("login", "secrets".toCharArray());

        ChallengeAuthenticator auth = new ChallengeAuthenticator(context, optional, challengeScheme, realm, verifier) {
            @Override
            protected boolean authenticate(Request request, Response response) {                
                if(request.getCookies().getFirst("x-soaba-auth", true) == null){
                    response.getCookieSettings().add("x-soaba-auth", "");
                    return false;
                }
                
                Cookie authCookie = request.getCookies().getFirst("x-soaba-auth", true);
                return "login".equals(authCookie.getValue().split(":")[0]) &&
                       "secrets".equals(authCookie.getValue().split(":")[1]);
            }
        };

        return auth;
    }

    /**
     * Authenticates the given request and modifies the corresponding response with 
     * authentication headers.
     * 
     * @param request the request requiring authentication
     * @param response the requests' response
     * @return true if the request was authenticated successfully, false if otherwise
     */
    public boolean authenticate(Request request, Response response) {
        if (!request.getClientInfo().isAuthenticated()) {
            authenticator.challenge(response, false);
            return false;
        }
        return true;
    }

    /**
     * Binds all server resources to their corresponding URIs with the current application.
     */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized Restlet createInboundRoot() {
        final Router appRouter = new Router(getContext());
        this.authenticator = createAuthenticator();
        
        for (String resxURI : resx.keySet())
            appRouter.attach(resxURI, (Class<ServerResource>) resx.get(resxURI));

        // attachs bonjour service, which presents the API UI
        appRouter.attach("", BonjourService.class);
        appRouter.attach("/", BonjourService.class);
        
        authenticator.setNext(appRouter);
        return authenticator;
    }

    /**
     * Performs the disposal of all resources allocated by the application.
     */
    public void shutdown() {
        BuildingDataService.dispose();
    }
}
