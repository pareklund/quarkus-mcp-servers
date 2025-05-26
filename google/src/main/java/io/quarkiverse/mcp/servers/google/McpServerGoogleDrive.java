package io.quarkiverse.mcp.servers.google;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;

public class McpServerGoogleDrive {

    private static final int PAGE_SIZE = 20;
    private static final String FIELDS = "nextPageToken, files(id, name)";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final Drive drive;

    private final ConcurrentMap<String, String> pageTokenToMimeTypeMap = new ConcurrentHashMap<>();

    public McpServerGoogleDrive(CredentialLoader credentialLoader) throws Exception {
        drive = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credentialLoader.loadCredential())
                .setApplicationName("Quarkus Google Drive MCP Server")
                .build();
    }

    @Tool(description = "List files in Google Drive")
    String listFiles(
            @ToolArg(description = "The mimeType of files to list, if any") String mimeType) throws Exception {
        var list = drive.files().list()
                .setFields(FIELDS)
                .setPageSize(PAGE_SIZE);
        if (mimeType != null && !mimeType.isEmpty()) {
            list.setQ("mimeType='" + mimeType + "'");
        }
        return listFilesAndRecordPageToken(list, mimeType);
    }

    @Tool(description = "List files in Google Drive, given a page token")
    String listFilesForPageToken(@ToolArg(description = "The page token") String pageToken) throws Exception {
        var list = drive.files().list()
                .setFields(FIELDS)
                .setPageSize(PAGE_SIZE)
                .setPageToken(pageToken);
        String mimeType = null;
        if (pageTokenToMimeTypeMap.containsKey(pageToken)) {
            mimeType = pageTokenToMimeTypeMap.get(pageToken);
            if (mimeType != null && !mimeType.isEmpty()) {
                list.setQ("mimeType='" + mimeType + "'");
            }
        }
        return listFilesAndRecordPageToken(list, mimeType);
    }

    private String listFilesAndRecordPageToken(Drive.Files.List list, String mimeType) throws Exception {
        var files = list.execute();
        if (files.getNextPageToken() != null) {
            pageTokenToMimeTypeMap.put(files.getNextPageToken(), mimeType);
        }
        return files.toPrettyString();

    }
}
