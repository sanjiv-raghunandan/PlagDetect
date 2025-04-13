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
import javafx.stage.Stage;
import plagdetect.controller.FileController;
import plagdetect.controller.JPlagDetect;

public class UI extends Application {

    private final FileController fileController = new FileController();
    private final JPlagDetect jPlagDetect = new JPlagDetect();

    @Override
    public void start(Stage primaryStage) {

        VBox vbox = new VBox();
        vbox.setSpacing(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #101820;");

        // Add header
        Label header = new Label("Plagiarism Detection App");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Add buttons
        Button downloadDriveButton = new Button("Download Files");
        styleButton(downloadDriveButton);
        downloadDriveButton.setOnAction(e -> downloadFilesFromDrive());

        Button uploadFilesButton = new Button("Upload Files");
        styleButton(uploadFilesButton);
        uploadFilesButton.setOnAction(e -> uploadFilesFromSubmissions());

        Button viewFilesButton = new Button("View Uploaded Files");
        styleButton(viewFilesButton);
        viewFilesButton.setOnAction(e -> viewUploadedFiles());

        Button plagDetectButton = new Button("Detect Plagiarism");
        styleButton(plagDetectButton);
        plagDetectButton.setOnAction(e -> detectPlag());

        

        vbox.getChildren().addAll(header, downloadDriveButton, uploadFilesButton, viewFilesButton, plagDetectButton);

        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Plagiarism Detection App");
        primaryStage.show();
    }

    // Button styling
    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #FEE715; -fx-text-fill: #333; -fx-font-size: 14px; "
                + "-fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #FEE715; -fx-text-fill: #333; "
                + "-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #FEE715; -fx-text-fill: #333; "
                + "-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
    }

    private void downloadFilesFromDrive() {
        try {
            fileController.downloadFilesFromDrive();
            showAlert("Success", "Files downloaded successfully from Google Drive.");
        } catch (Exception e) {
            showAlert("Error", "Error downloading files: " + e.getMessage());
        }
    }

    private void uploadFilesFromSubmissions() {
        try {
            File submissionsDir = new File("src/main/resources/submissions");
            File[] filesToUpload = submissionsDir.listFiles();

            if (filesToUpload != null && filesToUpload.length > 0) {
                fileController.uploadFiles(List.of(filesToUpload));
                showAlert("Success", "Files from the submissions directory uploaded temporarily.");
            } else {
                showAlert("Info", "No files found in the submissions directory to upload.");
            }
        } catch (Exception e) {
            showAlert("Error", "Error uploading files to the database: " + e.getMessage());
        }
    }

    private void viewUploadedFiles() {
        try {
            VBox fileListLayout = new VBox();
            fileListLayout.setSpacing(10);
            fileListLayout.setPadding(new Insets(10));

            Button deleteAllButton = new Button("Delete All Files");
            styleButton(deleteAllButton);
            deleteAllButton.setOnAction(e -> {
                try {
                    fileController.deleteAllFiles();
                    deleteAllFilesFromSubmissions();
                    showAlert("Success", "All files deleted successfully.");
                    loadFileList(fileListLayout);
                } catch (Exception ex) {
                    showAlert("Error", "Error deleting files: " + ex.getMessage());
                }
            });

            Button syncDatabaseButton = new Button("Sync Database");
            styleButton(syncDatabaseButton);
            syncDatabaseButton.setOnAction(e -> {
                try {
                    fileController.syncDatabaseWithDirectory();
                    showAlert("Success", "Database synchronized with the directory successfully.");
                    loadFileList(fileListLayout);
                } catch (Exception ex) {
                    showAlert("Error", "Error synchronizing database with the directory: " + ex.getMessage());
                }
            });

            fileListLayout.getChildren().addAll(deleteAllButton, syncDatabaseButton);
            loadFileList(fileListLayout);

            ScrollPane scrollPane = new ScrollPane(fileListLayout);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(600, 400);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Uploaded Files");
            alert.setHeaderText("List of Uploaded Files");
            alert.getDialogPane().setContent(scrollPane);
            alert.showAndWait();

        } catch (Exception e) {
            showAlert("Error", "Error retrieving uploaded files: " + e.getMessage());
        }
    }

    private void deleteAllFilesFromSubmissions() {
        File submissionsDir = new File("src/main/resources/submissions");
        if (submissionsDir.exists() && submissionsDir.isDirectory()) {
            File[] files = submissionsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    } else if (file.isDirectory()) {
                        deleteDirectory(file);
                    }
                }
            }
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    private void loadFileList(VBox fileListLayout) {
        try {
            List<String[]> files = fileController.getUploadedFiles();
            fileListLayout.getChildren().removeIf(node -> node instanceof VBox);

            int index = 1;
            for (String[] file : files) {
                VBox fileEntry = new VBox();
                fileEntry.setSpacing(5);

                Label fileNameLabel = new Label(index++ + ". File Name: " + file[0]);
                fileNameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

                Label fileExtensionLabel = new Label("File extension: " + file[1]);
                fileExtensionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

                

                Button deleteButton = new Button("Delete");
                styleButton(deleteButton);
                deleteButton.setOnAction(e -> {
                    try {
                        fileController.deleteFile(file[0]);
                        showAlert("Success", "File '" + file[0] + "' deleted successfully.");
                        loadFileList(fileListLayout);
                    } catch (Exception ex) {
                        showAlert("Error", "Error deleting file: " + ex.getMessage());
                    }
                });

                fileEntry.getChildren().addAll(fileNameLabel, fileExtensionLabel,  deleteButton);
                fileListLayout.getChildren().add(fileEntry);
            }

        } catch (Exception e) {
            showAlert("Error", "Error loading file list: " + e.getMessage());
        }
    }

    private void detectPlag() {
        jPlagDetect.handlePlagiarismDetection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}