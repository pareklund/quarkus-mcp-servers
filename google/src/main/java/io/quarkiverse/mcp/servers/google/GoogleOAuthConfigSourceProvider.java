package io.quarkiverse.mcp.servers.google;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.spi.ConfigSource;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;

@ApplicationScoped
public class GoogleOAuthConfigSourceProvider implements ConfigSourceFactory {

    private static final java.io.File CREDENTIALS_FOLDER = new java.io.File(System.getProperty("user.home"),
            ".credentials/my-app");

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext configSourceContext) {
        var properties = new java.util.Properties();
        try (var inputStream = new java.io.FileInputStream(CREDENTIALS_FOLDER + "/config.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties", e);
        }
        return java.util.Collections.singletonList(new io.smallrye.config.PropertiesConfigSource(
                properties, "Google OAuth Config", 1000));
    }
}
