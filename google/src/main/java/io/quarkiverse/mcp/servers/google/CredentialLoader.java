package io.quarkiverse.mcp.servers.google;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import jakarta.inject.Singleton;

import org.jboss.logging.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

@Singleton
public class CredentialLoader {

    private static final Logger LOGGER = Logger.getLogger(CredentialLoader.class.getName());

    private static final java.io.File CREDENTIALS_FOLDER = new java.io.File(System.getProperty("user.home"),
            ".credentials/my-app");

    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleOAuthConfig config;

    public CredentialLoader(GoogleOAuthConfig config) {
        this.config = config;
        LOGGER.info("Client ID: " + config.clientId());
        LOGGER.info("Client Secret: " + config.clientSecret());
        LOGGER.info("Redirect URI: " + config.redirectUri());
        LOGGER.info("Scope: " + config.scope());
    }

    Credential loadCredential() throws IOException, GeneralSecurityException {
        LOGGER.info("Loading stored credentials from: " + CREDENTIALS_FOLDER.getAbsolutePath());
        var flow = createFlow();
        // "user" is a default ID used for single-user apps
        return flow.loadCredential("user");
    }

    private GoogleAuthorizationCodeFlow createFlow() throws GeneralSecurityException, IOException {
        var dataStoreFactory = new FileDataStoreFactory(CREDENTIALS_FOLDER);

        var clientSecrets = new GoogleClientSecrets()
                .setInstalled(new GoogleClientSecrets.Details()
                        .setClientId(config.clientId())
                        .setClientSecret(config.clientSecret()));

        return new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                Collections.singletonList(config.scope()))
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build();
    }
}
