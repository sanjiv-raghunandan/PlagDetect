package plagdetect.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import plagdetect.controller.FileController;
import plagdetect.controller.JPlagDetect;

import java.io.File;
import java.util.List;

public class UI extends Application {

    private final FileController fileController = new FileController();
	private final JPlagDetect jPlagDetect = new JPlagDetect(); 

    @Override
    public void start(Stage primaryStage) {
        // Add Download from Drive Button
        Button downloadDriveButton = new Button("Download Files");
        downloadDriveButton.setOnAction(e -> downloadFilesFromDrive());

        // Add View Uploaded Files Button
        Button viewFilesButton = new Button("View Uploaded Files");
        viewFilesButton.setOnAction(e -> viewUploadedFiles());
		
		Button plagDetectButton = new Button("Detect Plagarism");
		plagDetectButton.setOnAction(e -> detectPlag());

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(downloadDriveButton, viewFilesButton, plagDetectButton);

        Scene scene = new Scene(vbox, 400, 150); // Adjust height for the buttons
        primaryStage.setScene(scene);
        primaryStage.setTitle("File Upload");
        primaryStage.show();
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
            List<String[]> files = fileController.getUploadedFiles();
            fileListLayout.getChildren().removeIf(node -> node instanceof VBox); // Clear existing file entries

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
            // Step 1: Download files from Google Drive
            fileController.downloadFilesFromDrive();
            showAlert("Success", "Files downloaded successfully from Google Drive.");
    
            // Step 2: Automatically upload the downloaded files
            File downloadDir = new File("src/main/resources/submissions");
            File[] downloadedFiles = downloadDir.listFiles();
    
            if (downloadedFiles != null && downloadedFiles.length > 0) {
                fileController.uploadFiles(List.of(downloadedFiles));
                showAlert("Success", "Downloaded files uploaded successfully.");
            } else {
                showAlert("Info", "No files found to upload after download.");
            }
        } catch (Exception e) {
            showAlert("Error", "Error during download or upload: " + e.getMessage());
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
	private void detectPlag(){
		jPlagDetect.handlePlagiarismDetection();
	}
    public static void main(String[] args) {
        launch(args);
    }

}
