package plagdetect.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class DriveDownloader {

    private final List<String[]> files = List.of(
        new String[]{"1RUIOugmUS3bzIoBMkDp-viHI05nS3vhv", "URL_Shortner_Project.pdf"},
        new String[]{"1mrL_Zkp-zVnb2STPqrdYGNMsJGDjn3BF", "Kubernetes_Deployment.pdf"}
    );

    // Public getter for the files list
    public List<String[]> getFiles() {
        return files;
    }

    /**
     * Downloads all files from the internal list.
     */
    public void downloadFilesFromDrive() {
        downloadFilesFromDrive(files);
    }

    /**
     * Downloads multiple files from Google Drive.
     * @param files A list of file ID and file name pairs.
     */
    public void downloadFilesFromDrive(List<String[]> files) {
        try {
            System.out.println("Connecting to Google Drive...");
    
            // Base submissions directory in main/resources
            String baseDownloadPath = "src/main/resources/submissions";
            File baseDir = new File(baseDownloadPath);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
                System.out.println("Base directory created: " + baseDir.getAbsolutePath());
            }
    
            for (String[] file : files) {
                String fileId = file[0];
                String fileName = file[1];
    
                // Extract the directory name from the file name (remove the extension)
                String directoryName = fileName.substring(0, fileName.lastIndexOf('.'));
    
                // Create a new directory for each file using the file name (without extension)
                String userDirPath = baseDownloadPath + "/" + directoryName;
                File userDir = new File(userDirPath);
                if (!userDir.exists()) {
                    userDir.mkdirs();
                    System.out.println("Directory created: " + userDir.getAbsolutePath());
                }
    
                // Download the file into the corresponding directory
                String downloadUrl = "https://drive.google.com/uc?export=download&id=" + fileId;
                System.out.println("Downloading from: " + downloadUrl);
    
                URL url = new URL(downloadUrl);
                URLConnection conn = url.openConnection();
    
                try (InputStream in = conn.getInputStream();
                     FileOutputStream out = new FileOutputStream(userDirPath + "/" + fileName)) {
    
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
    
                System.out.println("Downloaded successfully as: " + userDirPath + "/" + fileName);
            }
    
        } catch (IOException e) {
            System.out.println("Failed to download files: " + e.getMessage());
        }
    }

    /**
     * Downloads a single file from Google Drive.
     * @param fileId The ID of the file on Google Drive.
     * @param fileName The name to save the file as.
     */
    public void downloadFileFromDrive(String fileId, String fileName) {
        // Wrap the single file into a list and call the existing method
        downloadFilesFromDrive(java.util.Collections.singletonList(new String[]{fileId, fileName}));
    }
}