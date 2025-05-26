package io.quarkiverse.mcp.servers.google;

import io.quarkiverse.mcp.servers.shared.SharedApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * This is the main entry point for the google server.
 * It will ...
 */
@QuarkusMain(name = "google")
public class GoogleApplication {
    public static void main(String[] args) {
        SharedApplication.main(args, (remainingArgs) -> null);
    }
}
