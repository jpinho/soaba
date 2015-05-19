package soaba.services;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Calendar;
import java.util.UUID;

import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import soaba.core.config.AppConfig;
import soaba.core.config.ExcludeTransformer;
import soaba.core.models.Session;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.tenant.Tenant;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * The Authentication Service
 * 
 * @author João Pinho (jpe.pinho@gmail.com)
 * @since 0.5
 */
public class AuthService {

    private static AppConfig config = AppConfig.getInstance();
    private static JSONSerializer currentJsonSerializer;

    static {
        currentJsonSerializer = new JSONSerializer().transform(new ExcludeTransformer(), void.class).prettyPrint(true);
    }

    private static String toJSON(Object obj) {
        return currentJsonSerializer.deepSerialize(obj);
    }

    /**
     * Lists all datapoints configured in the application.
     */
    public static class AuthenticateUser extends
            ServerResource {

        public final static String ROUTE_URI = "/auth";

        @Post
        public String doPost(Representation entity) throws IOException {
            RestletServer.configureRestForm(getResponse());
            String[] body = entity.getText().split("&");
            String username = body[0].split("=")[1];
            String textpass = body[1].split("=")[1];

            String path = "resources/.stormpath/apiKey.properties";
            ApiKey apiKey = ApiKeys.builder().setFileLocation(path).build();
            Client client = Clients.builder().setApiKey(apiKey).build();

            @SuppressWarnings("rawtypes")
            AuthenticationRequest request = new UsernamePasswordRequest(username, textpass);

            Tenant tenant = client.getCurrentTenant();
            ApplicationList applications = tenant.getApplications(
                Applications.where(Applications.name().eqIgnoreCase("SOABA Secure")));
            Application application = applications.iterator().next();

            AuthenticationResult result = application.authenticateAccount(request);
            Account account = result.getAccount();
            
            Session newSession = new Session();
            newSession.setUserAccount(account);
            newSession.setToken(UUID.randomUUID().toString());
            config.getSessions().put(newSession.getToken().toString(), newSession);
            
            return toJSON(newSession);
        }
    }
    
    public static class CheckUserAuth extends ServerResource {

        public final static String ROUTE_URI = "/auth/check";
        
        @Post
        public String doPost(Representation entity) throws IOException {
            RestletServer.configureRestForm(getResponse());
            String userData = entity.getText();
            
            @SuppressWarnings("rawtypes")
            Session userSession = (Session)new JSONDeserializer().deserialize(userData);
            
            if(!config.getSessions().containsKey(userSession.getToken()))
                return "false";
            
            if(config.getSessions().get(userSession.getToken()).getExpirationDate().compareTo(Calendar.getInstance().getTime()) <= 0){
                config.getSessions().remove(userSession.getToken());
                return "false";
            }
            
            return "true";
        }
    }

    public static void CheckAuthorization(Form form) throws AccessDeniedException {
        Parameter pToken = form.getFirst("token");
        
        try{
            String token = pToken.getValue();
            
            if(config.getSessions().get(token).getExpirationDate().compareTo(Calendar.getInstance().getTime()) <= 0){
                config.getSessions().remove(token);
                throw new AccessDeniedException("Protected Server Resource.");
            }
        }
        catch (Exception ex){
            throw new AccessDeniedException("Protected Server Resource.");
        }
    }
}
