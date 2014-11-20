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

    private static final String BGROUND_PATH = "resources/bground-b64.txt";

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
         * HTML Helper Variables
         */
        final String title = "SOABA REST API", footer = "Developed by João Pinho";

        final String globalStyle = "<style type='text/css'>body{ " + "background: url('"
                + new String(Files.readAllBytes(java.nio.file.Paths.get(BGROUND_PATH))) + "');" + " }</style>";

        final String stylesContainer = "margin:20px auto; width:600px; text-align:center; font-family: tahoma; font-size:0.9em; padding:20px;"
                + "border-radius:10px; -webkit-box-shadow: 0px 0px 15px 1px rgba(73,147,237, 0.4);-moz-box-shadow: 0px 0px 15px 1px rgba(73,147,237, 0.4);"
                + "box-shadow:0px 0px 15px 1px rgba(73,147,237, 0.4);border:1px solid #ddd; background-color:#fff;";

        final String stylesHeader = "background-color: #F0F0F0;margin: -20px;padding: 20px;padding-bottom: 0px;margin-bottom: 10px;border-top-left-radius: 10px;"
                + "border-top-right-radius: 10px;";

        final String stylesLineHeader = "border:0px none;border-top:1px solid #000;height:1px;margin-left: -20px;margin-right: -20px;";

        final String stylesFooter = "text-align:right; font-size: 0.7em; background-color: #F0F0F0;margin: -20px;padding: 10px 20px; margin-top: 10px;"
                + "border-bottom-left-radius: 10px;border-bottom-right-radius: 10px;";

        final String stylesLineFooter = "border:0px none;border-top:1px solid #000;height:1px;margin-left: -20px;margin-right: -20px; margin-top:-10px;";

        /**
         * Bonjour Page Construction
         */
        StringBuilder output = new StringBuilder();
        output.append(globalStyle + "<div style='" + stylesContainer + "'>" + " <div style='" + stylesHeader + "'>"
                + "<b style='letter-spacing: 1px;'>" + title + "</b>" + "<br/><br/><hr style='" + stylesLineHeader
                + "'/>" + "</div>"
                + " <table style='text-align:left;font-size:0.9em;width: 100%; border:1px solid #ddd; '>");

        // listing available routes
        int i = 0;
        for (String uri : routes) {
            output.append("<tr><td style='padding:5px;" + (i++ % 2 == 0 ? "background-color: #f9f9f9;" : "")
                    + "'><a target='_blank' style='color:#444;' href='");
            output.append(RestletServer.ROOT_URI);
            output.append(uri);
            output.append("'>");
            output.append(addrStr);
            output.append(uri);
            output.append("</a></td></tr>");
        }

        output.append("</table><div style='" + stylesFooter + "'><hr style='" + stylesLineFooter + "'/>" + footer
                + "</div></div>");

        return new StringRepresentation(output.toString(), MediaType.TEXT_HTML);
    }
}
