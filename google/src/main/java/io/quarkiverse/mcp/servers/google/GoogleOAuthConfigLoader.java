package io.quarkiverse.mcp.servers.google;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;

@ApplicationScoped
public class GoogleOAuthConfigLoader {

    private static final java.io.File CREDENTIALS_FOLDER = new java.io.File(System.getProperty("user.home"),
            ".credentials/my-app");

    record GoogleOauthConfigImpl(String clientId, String clientSecret, String redirectUri, String scope)
            implements
                GoogleOAuthConfig {
    }

    @Produces
    GoogleOAuthConfig loadGoogleOAuthConfig() {
        var properties = new java.util.Properties();
        try (var inputStream = new java.io.FileInputStream(CREDENTIALS_FOLDER + "/config.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties", e);
        }
        return new GoogleOauthConfigImpl(
                properties.getProperty(fullPropertyName(GoogleOAuthConfig.CLIENT_ID)),
                properties.getProperty(fullPropertyName(GoogleOAuthConfig.CLIENT_SECRET)),
                properties.getProperty(fullPropertyName(GoogleOAuthConfig.REDIRECT_URI)),
                properties.getProperty(fullPropertyName(GoogleOAuthConfig.SCOPE)));
    }

    private static String fullPropertyName(String property) {
        return GoogleOAuthConfig.PREFIX + "." + property;
    }
}
