package io.quarkiverse.mcp.servers.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;

public class McpServerGoogleDrive {

    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final CredentialLoader credentialLoader;

    public McpServerGoogleDrive(CredentialLoader credentialLoader) {
        this.credentialLoader = credentialLoader;
    }

    @Tool(description = "List files in Google Drive")
    String listFiles(
            @ToolArg(description = "The mimeType of files to list, if any") String mimeType) throws Exception {
        // Use the credentials to access the Drive API
        Drive drive = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credentialLoader.loadCredential())
                .setApplicationName("Quarkus Google Drive MCP Server")
                .build();

        var list = drive.files().list()
                .setFields("nextPageToken, files(id, name)")
                .setPageSize(20);
        if (mimeType != null && !mimeType.isEmpty()) {
            list.setQ("mimeType='" + mimeType + "'");
        }

        var files = list.execute();
        return files.toPrettyString();
    }

    /*
     * @Prompt(description = "Make a greeting")
     * PromptMessage make_greeting(@PromptArg(description = "Who you want to be greeted") String who) {
     * return PromptMessage.withUserRole(new TextContent(
     * """
     * The assistants goal is to greet the user NOT_FOUND
     * """));
     * }
     */
}
