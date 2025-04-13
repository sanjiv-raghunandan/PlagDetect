package plagdetect.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import plagdetect.model.DriveDownloader;
import plagdetect.model.FileModel;
import plagdetect.model.MetadataExtractor;

public class FileController {

    private List<Map<String, String>> temporaryMetadata = new ArrayList<>();

    public void uploadFiles(List<File> directories) throws Exception {
        // Collect all files from the provided directories (including subdirectories)
        List<File> allFiles = new ArrayList<>();
        for (File directory : directories) {
            if (directory.isDirectory()) {
                getAllFiles(directory, allFiles); // Recursively collect files
            } else {
                allFiles.add(directory); // Add individual files directly
            }
        }

        // Extract metadata using MetadataExtractor
        temporaryMetadata = MetadataExtractor.extractFileMetadata(allFiles);

        // Log the uploaded files
        System.out.println("Uploaded " + temporaryMetadata.size() + " files (metadata stored temporarily):");
        for (Map<String, String> metadata : temporaryMetadata) {
            System.out.println("File Name: " + metadata.get("fileName") + ", File Extension: " + metadata.get("fileExtension"));
        }
    }

    public List<String[]> getUploadedFiles() throws Exception {
        return FileModel.getUploadedFiles();
    }

    public void deleteAllFiles() throws Exception {
        FileModel.deleteAllFiles();
    }

    public void deleteFile(String fileName) throws Exception {
        FileModel.deleteFile(fileName);
    }

    /**
     * Downloads files from Google Drive using the DriveDownloader class.
     * @throws Exception if an error occurs during the download process.
     */
    public void downloadFilesFromDrive() throws Exception {
        DriveDownloader driveDownloader = new DriveDownloader();

        
        String folderLink = "https://drive.google.com/drive/folders/1zM8zhJlAnbvwx589GA5g079erkTyWiSx";

        // Pass the folder link to the method
        driveDownloader.downloadFilesFromDrive(folderLink);
    }

    public void syncDatabaseWithDirectory() throws Exception {
        if (temporaryMetadata.isEmpty()) {
            System.out.println("No files to sync with the database.");
            return;
        }

        // Convert metadata to the format required by FileModel
        List<String[]> fileData = temporaryMetadata.stream()
            .map(metadata -> new String[]{
                metadata.get("fileName"),
                metadata.get("fileExtension")
            })
            .toList();

        // Save files to the database
        FileModel.saveFiles(fileData);

        // Log the synced files
        System.out.println("Synced " + fileData.size() + " files with the database:");
        for (String[] data : fileData) {
            System.out.println("File Name: " + data[0] + ", File Extension: " + data[1]);
        }

        // Clear the temporary metadata after syncing
        temporaryMetadata.clear();
    }

    public List<Map<String, String>> getTemporaryMetadata() {
        return temporaryMetadata;
    }

    private void getAllFiles(File directory, List<File> fileList) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getAllFiles(file, fileList); // Recursive call for subdirectories
                } else {
                    fileList.add(file);
                }
            }
        }
    }

    private void viewUploadedFiles() {
        try {
            VBox fileListLayout = new VBox();
            fileListLayout.setSpacing(10);
            fileListLayout.setPadding(new Insets(10));

            // Display temporary metadata
            List<Map<String, String>> temporaryMetadata = getTemporaryMetadata();
            if (temporaryMetadata.isEmpty()) {
                Label noFilesLabel = new Label("No files uploaded yet.");
                noFilesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                fileListLayout.getChildren().add(noFilesLabel);
            } else {
                for (Map<String, String> metadata : temporaryMetadata) {
                    VBox fileEntry = new VBox();
                    fileEntry.setSpacing(5);
                    fileEntry.setStyle("-fx-background-color: #ffffff; -fx-padding: 10px; -fx-border-color: #ccc; -fx-border-width: 1px;");

                    Label fileNameLabel = new Label("File Name: " + metadata.get("fileName"));
                    fileNameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

                    Label fileExtensionLabel = new Label("File Extension: " + metadata.get("fileExtension"));
                    fileExtensionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

                    fileEntry.getChildren().addAll(fileNameLabel, fileExtensionLabel);
                    fileListLayout.getChildren().add(fileEntry);
                }
            }

            ScrollPane scrollPane = new ScrollPane(fileListLayout);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(600, 400);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Uploaded Files");
            alert.setHeaderText("List of Uploaded Files (Temporary)");
            alert.getDialogPane().setContent(scrollPane);
            alert.showAndWait();

        } catch (Exception e) {
            showAlert("Error", "Error retrieving uploaded files: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}