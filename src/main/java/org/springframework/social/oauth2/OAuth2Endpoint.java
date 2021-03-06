package org.springframework.social.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by psmelser on 2015-12-18.
 *
 * @author paul_smelser@silanis.com
 */
public class OAuth2Endpoint {
    private static Logger logger = LoggerFactory.getLogger(OAuth2Endpoint.class);
    private String authUrl;
    private String resourceId;
    private OAuth2Endpoint(String authUrl, String resourceId){
        this.authUrl = authUrl;
        this.resourceId = resourceId;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public static OAuth2Endpoint parseAuthUrl(String response, String defaultResourceId) {
        logger.debug("Parsing Authorization from WWW-Authenticate header: "+ response);
        try {
            Pattern r = Pattern.compile("authorization_uri=(\\S*(?<!,))(,\\s*resource_id=(\\S*$))?");
            Matcher m = r.matcher(response);
            m.find();
            return new OAuth2Endpoint(m.group(1), getResourceId(defaultResourceId, m));
        }catch(RuntimeException e){
            logger.error("Could not parse Authorization URI from OAuth2DiscoveryService response", e);
            throw e;
        }

    }

    private static String getResourceId(String defaultResourceId, Matcher m) {
        return m.group(3) != null ? m.group(3) : defaultResourceId;
    }

    public String parseTenantId() {
        Pattern pattern = Pattern.compile(".*/([a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}).*");
        Matcher matcher = pattern.matcher(authUrl);
        matcher.find();
        return matcher.group(1);
    }

    public String parseTokenUri(){
        return authUrl.replace("authorize", "token");
    }

    public String getResourceId() {
        return resourceId;
    }
}
