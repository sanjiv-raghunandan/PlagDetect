package plagdetect.view;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import plagdetect.controller.FileController;
import plagdetect.controller.JPlagDetect;

public class UI extends Application {

    private final FileController fileController = new FileController();
    private final JPlagDetect jPlagDetect = new JPlagDetect();

    @Override
    public void start(Stage primaryStage) {
        // Add Upload File Button
        Button uploadButton = new Button("Upload Files");
        uploadButton.setOnAction(e -> uploadFiles());

        // Add View Uploaded Files Button
        Button viewFilesButton = new Button("View Uploaded Files");
        viewFilesButton.setOnAction(e -> viewUploadedFiles());

        // Add Download from Drive Button
        Button downloadDriveButton = new Button("Download from Drive");
        downloadDriveButton.setOnAction(e -> downloadFilesFromDrive());

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);

        // Add buttons to the VBox layout
        vbox.getChildren().addAll(uploadButton, viewFilesButton, downloadDriveButton);

        Scene scene = new Scene(vbox, 400, 200); // Adjust height for the buttons
        primaryStage.setScene(scene);
        primaryStage.setTitle("File Upload");
        primaryStage.show();
    }

    private void uploadFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files to Upload");
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            try {
                fileController.uploadFiles(selectedFiles);
                showAlert("Success", "Files uploaded successfully.");
            } catch (Exception e) {
                showAlert("Error", "Error uploading files: " + e.getMessage());
            }
        }
    }

    private void viewUploadedFiles() {
        try {
            VBox fileListLayout = new VBox();
            fileListLayout.setSpacing(10);
            fileListLayout.setPadding(new Insets(10));

            // Add a "Delete All Files" button
            Button deleteAllButton = new Button("Delete All Files");
            deleteAllButton.setPrefWidth(150); // Set preferred width for the "Delete All Files" button
            deleteAllButton.setOnAction(e -> {
                try {
                    if (fileController.getUploadedFiles().isEmpty()) {
                        showAlert("Info", "No files available to delete.");
                    } else {
                        fileController.deleteAllFiles();
                        showAlert("Success", "All files deleted successfully.");
                        loadFileList(fileListLayout); // Refresh the file list
                    }
                } catch (Exception ex) {
                    showAlert("Error", "Error deleting files: " + ex.getMessage());
                }
            });

            fileListLayout.getChildren().add(deleteAllButton);

            // Load the file list
            loadFileList(fileListLayout);

            // Display the list in an alert dialog
            ScrollPane scrollPane = new ScrollPane(fileListLayout);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(600, 400);

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Uploaded Files");
            alert.setHeaderText("List of Uploaded Files");
            alert.getDialogPane().setContent(scrollPane);
            alert.showAndWait();

        } catch (Exception e) {
            showAlert("Error", "Error retrieving uploaded files: " + e.getMessage());
        }
    }

    private void loadFileList(VBox fileListLayout) {
        try {
            // Fetch the list of uploaded files from the file model
            List<String[]> files = fileController.getUploadedFiles();
            fileListLayout.getChildren().clear(); // Clear existing file entries

            int index = 1;
            for (String[] file : files) {
                VBox fileEntry = new VBox();
                fileEntry.setSpacing(5);

                // File details (file name, file path, and uploaded time on separate lines)
                Label fileNameLabel = new Label(index++ + ". File Name: " + file[0]);
                Label filePathLabel = new Label("File Path: " + file[1]);
                Label uploadedTimeLabel = new Label("Uploaded Time: " + file[2]);

                // Delete button for the specific file
                Button deleteButton = new Button("Delete");
                deleteButton.setPrefWidth(100); // Set preferred width for individual "Delete" buttons
                deleteButton.setOnAction(e -> {
                    try {
                        fileController.deleteFile(file[0]); // Assuming deleteFile takes the file name as input
                        showAlert("Success", "File '" + file[0] + "' deleted successfully.");
                        loadFileList(fileListLayout); // Refresh the file list
                    } catch (Exception ex) {
                        showAlert("Error", "Error deleting file: " + ex.getMessage());
                    }
                });

                fileEntry.getChildren().addAll(fileNameLabel, filePathLabel, uploadedTimeLabel, deleteButton);
                fileListLayout.getChildren().add(fileEntry);
            }

            if (files.isEmpty()) {
                Label noFilesLabel = new Label("No files available.");
                fileListLayout.getChildren().add(noFilesLabel);
            }

        } catch (Exception e) {
            showAlert("Error", "Error loading file list: " + e.getMessage());
        }
    }

    private void downloadFilesFromDrive() {
        try {
            fileController.downloadFilesFromDrive();
            showAlert("Success", "Files downloaded successfully from Google Drive.");
        } catch (Exception e) {
            showAlert("Error", "Error downloading files: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}