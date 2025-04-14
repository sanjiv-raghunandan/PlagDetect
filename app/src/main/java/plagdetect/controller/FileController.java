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

        // Convert metadata to the format required by FileModel
        List<String[]> fileData = temporaryMetadata.stream()
            .map(metadata -> new String[]{
                metadata.get("fileName"),
                metadata.get("fileExtension")
            })
            .toList();

        // Save files to the database
        FileModel.saveFiles(fileData);

        // Log the uploaded files
        System.out.println("Uploaded " + fileData.size() + " files to the database:");
        for (String[] data : fileData) {
            System.out.println("File Name: " + data[0] + ", File Extension: " + data[1]);
        }
    }

    public List<String[]> getUploadedFiles() throws Exception {
        return FileModel.getUploadedFiles();
    }

    public void deleteAllFiles() throws Exception {
        FileModel.deleteAllFiles();
    }

    public void deleteFile(String fileName) throws Exception {
        // Delete the file from the database
        FileModel.deleteFile(fileName);

        // Locate the file in the local directory
        File submissionsDir = new File("src/main/resources/submissions");
        File[] files = submissionsDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().equals(fileName)) {
                    if (file.delete()) {
                        System.out.println("File '" + fileName + "' deleted from the local directory.");
                        deleteEmptyParentDirectories(file.getParentFile()); // Check and delete empty parent directories
                    } else {
                        throw new Exception("Failed to delete file '" + fileName + "' from the local directory.");
                    }
                    return;
                } else if (file.isDirectory()) {
                    // Recursively search in subdirectories
                    if (deleteFileFromDirectory(file, fileName)) {
                        return;
                    }
                }
            }
        }

        throw new Exception("File '" + fileName + "' not found in the local directory.");
    }

    // Helper method to delete a file from subdirectories
    private boolean deleteFileFromDirectory(File directory, String fileName) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().equals(fileName)) {
                    if (file.delete()) {
                        System.out.println("File '" + fileName + "' deleted from the local directory.");
                        deleteEmptyParentDirectories(file.getParentFile()); // Check and delete empty parent directories
                        return true;
                    }
                } else if (file.isDirectory()) {
                    if (deleteFileFromDirectory(file, fileName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Helper method to delete empty parent directories recursively
    private void deleteEmptyParentDirectories(File directory) {
        if (directory != null && directory.isDirectory() && directory.list().length == 0) {
            if (directory.delete()) {
                System.out.println("Directory '" + directory.getAbsolutePath() + "' deleted as it was empty.");
                deleteEmptyParentDirectories(directory.getParentFile()); // Recursively check the parent directory
            }
        }
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
        // Get all files from the submissions directory
        File submissionsDir = new File("src/main/resources/submissions");
        List<File> allFiles = new ArrayList<>();
        getAllFiles(submissionsDir, allFiles);

        // Extract metadata for all files
        List<Map<String, String>> metadataList = MetadataExtractor.extractFileMetadata(allFiles);

        // Compare with the database and add missing files
        List<String[]> dbFiles = FileModel.getUploadedFiles();
        List<String> dbFileNames = dbFiles.stream().map(file -> file[0]).toList();

        List<String[]> newFiles = metadataList.stream()
            .filter(metadata -> !dbFileNames.contains(metadata.get("fileName")))
            .map(metadata -> new String[]{
                metadata.get("fileName"),
                metadata.get("fileExtension")
            })
            .toList();

        if (!newFiles.isEmpty()) {
            FileModel.saveFiles(newFiles);
            System.out.println("Synced " + newFiles.size() + " new files with the database:");
            for (String[] file : newFiles) {
                System.out.println("File Name: " + file[0] + ", File Extension: " + file[1]);
            }
        } else {
            System.out.println("No new files to sync with the database.");
        }
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