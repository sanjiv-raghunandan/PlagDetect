package plagdetect.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetadataExtractor {

    public static void main(String[] args) {
        try {
            List<File> submissions = getSubmissions(); // Get all files in submissions directory
            
            if (submissions.isEmpty()) {
                System.out.println("No submissions found.");
                return;
            }
            
            System.out.println("Found " + submissions.size() + " submissions:");
    
            // Print all files for debugging
            for (File file : submissions) {
                System.out.println("File: " + file.getAbsolutePath());
            }
    
            List<Map<String, String>> fileMetadata = extractFileMetadata(submissions);
            
            sendToDatabase(fileMetadata);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static List<File> getSubmissions() throws IOException {
        Path subPath = Path.of("src/main/resources/submissions");

        if (!Files.exists(subPath)) {
            throw new IOException("Submissions directory does not exist.");
        }

        List<File> allFiles = new ArrayList<>();
        getAllFiles(subPath.toFile(), allFiles); // Recursively collect all files
        return allFiles;
    }

    public static List<Map<String, String>> extractFileMetadata(List<File> files) {
        List<Map<String, String>> metadataList = new ArrayList<>();
        
        for (File file : files) {
            Map<String, String> metadata = new HashMap<>();
            
            String fileName = file.getName();
            String fileExtension = getFileExtension(fileName);

            // Check if the file extension is valid
            if (!fileExtension.equals("java") && !fileExtension.equals("cpp")) {
                fileExtension = "invalid"; // Mark as invalid if not .java or .cpp
            }

            metadata.put("fileName", fileName);
            metadata.put("fileExtension", fileExtension);
            
            metadataList.add(metadata);
        }
        
        return metadataList;
    }

    private static void getAllFiles(File directory, List<File> fileList) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursive call for subdirectories
                    getAllFiles(file, fileList);
                } else {
                    // Add file to the list
                    fileList.add(file);
                }
            }
        }
    }

    private static void sendToDatabase(List<Map<String, String>> metadataList) {
        // TODO: Implement database connection and storage
        System.out.println("Sending " + metadataList.size() + " records to database...");
        for (Map<String, String> metadata : metadataList) {
            System.out.println("File Name: " + metadata.get("fileName") + ", File Extension: " + metadata.get("fileExtension"));
        }
    }

    private static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "unknown"; // Handle null or empty file names
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase().trim(); // Convert to lowercase and trim spaces
        }
        return "unknown"; // Return "unknown" if no valid extension is found
    }
}
