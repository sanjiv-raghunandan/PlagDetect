package plagdetect.controller;

import plagdetect.model.UploadedFileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileController {

    public void uploadFiles(List<File> files) throws Exception {
        List<String[]> fileData = new ArrayList<>();
        for (File file : files) {
            fileData.add(new String[]{file.getName(), file.getAbsolutePath()});
        }
        UploadedFileModel.saveFiles(fileData);
    }

    public List<String[]> getUploadedFiles() throws Exception {
        return UploadedFileModel.getUploadedFiles();
    }
}