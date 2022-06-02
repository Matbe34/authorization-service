package as;

import as.Security.Secured;
import as.Utils.FileUtil;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;

@Path("/v1")
public class AS {
    
    @GET
    @Path("/ping")
    public Response ping() {
        return Response.ok().entity("Service online").build();
    }
    
    @POST
    @Secured
    @Path("/authorize_rule")
    public Response authorize_rule(@FormParam("request") String request, @FormParam("policy") String rule) {
        String policyLocation = null;
        String name = null;
        try {
            Properties props = new Properties();
            props.load(AS.class.getClassLoader().getResourceAsStream("app.properties"));
            String path = props.getProperty("path");
            name = FileUtil.randomFileName();
            policyLocation = path + File.separator + name;
            FileUtil.createDirectory(policyLocation);
            FileUtil.createFile(policyLocation + File.separator + "policy.xml", rule);
            FileUtil.createFile(policyLocation + File.separator + "request.xml", request);
        } catch (IOException e) {
            Logger.getLogger(AS.class.getName()).log(Level.SEVERE, null, e);
            return Response.serverError().build();
        }
        String result = null;
        try {
            PolicyFinder policyFinder = new PolicyFinder();
            Set<PolicyFinderModule> modules = new HashSet<>();
            Set<String> policySet = new HashSet<>();
            policySet.add(policyLocation);
            modules.add(new FileBasedPolicyFinderModule(policySet));
            policyFinder.setModules(modules);
            PDPConfig pdpc = new PDPConfig(null, policyFinder, null);
            PDP pdp = new PDP(pdpc);
            result = pdp.evaluate(request);
        }
        catch (Exception ex) {
            Logger.getLogger(AS.class.getName()).log(Level.SEVERE, null, ex);
            FileUtil.deleteDirectory(policyLocation);
            return Response.serverError().build();
        }
        FileUtil.deleteDirectory(policyLocation);
        return Response.ok(result).build();
    }
    
    
    
}