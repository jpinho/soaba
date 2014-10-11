package soaba.services.rest;

import org.json.JSONObject;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class KNXGatewayEndpoints {

    public class ListDevices extends
            ServerResource {
        
        @Get
        public JSONObject doGet() {
            configureRestForm(getResponse());
            return null;
        }
    }

    public final class ReadDatapoint extends
            ServerResource {

        @Get
        public JSONObject doGet() {
            configureRestForm(getResponse());
            return null;
        }
    }

    public final class WriteDatapoint extends
            ServerResource {

        @Put
        public JSONObject doGet() {
            configureRestForm(getResponse());
            return null;
        }
    }

    public static boolean start() {
        return false;
    }

    /**
     * Gets the rest form.
     * 
     * @return the rest form
     */
    private static Form configureRestForm(Response response) {
        Form responseHeaders = (Form) response.getAttributes().get("org.restlet.http.headers");

        if (responseHeaders == null) {
            responseHeaders = new Form();
            response.getAttributes().put("org.restlet.http.headers", responseHeaders);
        }

        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Access-Control-Allow-Methods", "GET");
        return responseHeaders;
    }
}
