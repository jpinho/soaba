package soaba.services;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * A simple Bonjour Service class to anounce the REST API in an easy way via Restlet,
 * through HTML.
 * 
 * @author João Pinho
 */
public class BonjourService extends
        ServerResource {

    private static final String BONJOUR_TMPL_PATH = "resources/bonjour.html.tmpl";
    private static final String BGROUND_IMG_PATH = "resources/pic.base64.txt";

    @SuppressWarnings("rawtypes")
    @Get
    public Representation doGet() throws IOException {
        final String addrStr = "http://hostname:" + RestletServer.SERVER_PORT + RestletServer.ROOT_URI;

        // sorting routes
        Map<String, Class> routesMap = RestletServer.getRoutes();
        String[] routes = new String[routesMap.keySet().size()];
        routesMap.keySet().toArray(routes);
        Arrays.sort(routes);

        /**
         * Bonjour Page Construction
         */
        String htmlTemplate = new String(Files.readAllBytes(java.nio.file.Paths.get(BONJOUR_TMPL_PATH))).trim();
        String imgContent = new String(Files.readAllBytes(java.nio.file.Paths.get(BGROUND_IMG_PATH))).trim();
        final StringBuilder output = new StringBuilder();

        // listing available routes
        int i = 0;
        for (String uri : routes) {
            output.append("<tr><td class='" + (i++ % 2 == 0 ? "even" : "odd")+ "'><a target='_blank' href='");
            output.append(RestletServer.ROOT_URI);
            output.append(uri);
            output.append("'>");
            output.append(addrStr);
            output.append(uri);
            output.append("</a></td></tr>");
        }

        htmlTemplate = htmlTemplate.replace("{#ROUTES#}", output.toString());
        htmlTemplate = htmlTemplate.replace("{#BGROUND_IMG#}", imgContent);
        return new StringRepresentation(htmlTemplate, MediaType.TEXT_HTML);
    }
}
