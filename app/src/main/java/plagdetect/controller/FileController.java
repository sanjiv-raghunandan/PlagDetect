package plagdetect.controller;

import java.io.File;
import java.util.List;

import plagdetect.model.DriveDownloader;
import plagdetect.model.FileModel;

public class FileController {

    public void uploadFiles(List<File> files) throws Exception {
        List<String[]> fileData = files.stream()
                .map(file -> new String[]{file.getName(), file.getAbsolutePath()})
                .toList();
        FileModel.saveFiles(fileData);
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
        driveDownloader.downloadFilesFromDrive(); // Delegate the download logic to DriveDownloader
    }
}