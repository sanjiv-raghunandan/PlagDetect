package plagdetect.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    
        // Extract file names from the local directory
        List<String> localFileNames = allFiles.stream()
            .map(File::getName)
            .toList();
    
        // Retrieve all files from the database
        List<String[]> dbFiles = FileModel.getUploadedFiles();
        List<String> dbFileNames = dbFiles.stream()
            .map(file -> file[0]) // Extract file names from the database entries
            .toList();
    
        // Identify files that are in the database but not in the local directory
        List<String> filesToDeleteFromDb = dbFileNames.stream()
            .filter(fileName -> !localFileNames.contains(fileName))
            .toList();
    
        // Delete missing files from the database
        for (String fileName : filesToDeleteFromDb) {
            FileModel.deleteFile(fileName);
            System.out.println("Deleted '" + fileName + "' from the database as it no longer exists locally.");
        }
    
        // Define valid extensions
        List<String> validExtensions = List.of("java", "cpp");
    
        // Identify new files to add to the database
        List<String[]> newFiles = allFiles.stream()
            .filter(file -> !dbFileNames.contains(file.getName()))
            .map(file -> {
                String fileName = file.getName();
                String fileExtension = getFileExtension(fileName);
    
                // Check if the file extension is valid
                if (!validExtensions.contains(fileExtension)) {
                    fileExtension = "invalid"; // Mark as invalid if not .java or .cpp
                }
    
                return new String[]{fileName, fileExtension};
            })
            .toList();
    
        // Add new files to the database
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

    // Helper method to get the file extension
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
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
}