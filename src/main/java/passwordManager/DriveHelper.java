package passwordManager;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Nico on 14/06/2017.
 */
public class DriveHelper {
    private final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/password-manager");
    private FileDataStoreFactory DATA_STORE_FACTORY;
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private HttpTransport HTTP_TRANSPORT;
    private final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private Drive drive = null;

    public DriveHelper() {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            Platform.exit();
        }
    }

    private Credential authorize() throws IOException {
        InputStream in = getClass().getResourceAsStream("/client_id.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    private Drive getDriveService() throws IOException {
        if (drive == null) {
            Credential credential = authorize();
            drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(PasswordManager.TITLE)
                    .build();
        }

        return drive;
    }

    public PSWFile chooseFile() {
        try {
            Drive service = getDriveService();

            FileList result = service
                    .files()
                    .list()
                    .setPageSize(10)
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            List<File> files = result.getFiles();
            if (files == null || files.size() == 0) {
                System.out.println("No files found.");
            } else {
                Optional<File> s = picker(files);
                return s.map(file -> new PSWFile(file.getId(), true)).orElse(null);

            }
        } catch (IOException ignored) {}

        return null;
    }

    private Optional<File> picker(List<File> files) {
        Dialog<File> pathDialog = new Dialog<>();

        ListView<File> filesView = new ListView<>();
        filesView.setCellFactory((lv) -> new ListCell<File>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        filesView.setItems(
                FXCollections.observableArrayList(
                        files
                                .stream()
                                .filter(s -> s.getName().endsWith(".psw"))
                                .collect(Collectors.toList())
                )
        );

        pathDialog.setResultConverter((b) -> {
            if (b == ButtonType.OK) {
                return filesView.getSelectionModel().getSelectedItem();
            }

            return null;
        });

        pathDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        pathDialog.getDialogPane().setContent(filesView);
        pathDialog.getDialogPane().setPrefSize(400, 400);

        return pathDialog.showAndWait();
    }

    public java.io.File downloadFile(PSWFile uploadedFile) {
        java.io.File parentDir = new java.io.File(".");
        java.io.File childFile = new java.io.File(parentDir, uploadedFile.getIdDansDrive());

        try (
                OutputStream fileOut = new FileOutputStream(childFile);
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            Drive.Files.Get g = getDriveService()
                    .files()
                    .get(uploadedFile.getIdDansDrive());

            g.executeMediaAndDownloadTo(out);

            out.writeTo(fileOut);

            uploadedFile.setNomDansDrive(g.execute().getName());
            uploadedFile.setIdDansDrive(g.getFileId());

            return childFile;
        } catch (IOException io) {
            io.printStackTrace();
        }

        return null;
    }

    public void uploadFile(PSWFile localFile) {
        File file = new File();
        file.setName(localFile.getNomDansDrive());
        file.setDescription("A save for the software Password Manager");
        file.setMimeType("text/plain");

        FileContent mediaContent = new FileContent("text/plain", localFile.getFichier());
        try {
            if (localFile.getIdDansDrive().length() == 0) {
                File remoteFile = getDriveService()
                        .files()
                        .create(file, mediaContent)
                        .setFields("id")
                        .execute();

                localFile.setIdDansDrive(remoteFile.getId());
                System.out.println("File was created on Drive");
            } else {
                getDriveService()
                        .files()
                        .update(localFile.getIdDansDrive(), file, mediaContent)
                        .execute();

                System.out.println("File was updated on Drive");
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }
}
