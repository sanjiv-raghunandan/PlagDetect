package plagdetect.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import plagdetect.controller.FileController;

import java.io.File;
import java.util.List;

public class SampleUI extends Application {

    private final FileController fileController = new FileController();

    @Override
    public void start(Stage primaryStage) {
        // Add Upload File Button
        Button uploadButton = new Button("Upload Files");
        uploadButton.setOnAction(e -> uploadFiles());

        // Add View Uploaded Files Button
        Button viewFilesButton = new Button("View Uploaded Files");
        viewFilesButton.setOnAction(e -> viewUploadedFiles());

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(uploadButton, viewFilesButton);

        Scene scene = new Scene(vbox, 400, 150); // Adjust height for the buttons
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
            List<String[]> files = fileController.getUploadedFiles();
            StringBuilder fileList = new StringBuilder("Uploaded Files:\n");
            int index = 1; // Start indexing from 1
            for (String[] file : files) {
                fileList.append(index++).append(". File Name: ").append(file[0])
                        .append("\n   File Path: ").append(file[1]).append("\n");
            }
    
            // Display the list in an alert dialog
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Uploaded Files");
            alert.setHeaderText("List of Uploaded Files");
            TextArea textArea = new TextArea(fileList.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefSize(600, 300);
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        } catch (Exception e) {
            showAlert("Error", "Error retrieving uploaded files: " + e.getMessage());
        }
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
