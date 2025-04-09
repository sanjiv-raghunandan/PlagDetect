package plagdetect.model;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FileModel {

    private static Connection getConnection() throws Exception {
        try (InputStream input = FileModel.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties props = new Properties();
            if (input == null) {
                throw new Exception("config.properties file not found.");
            }
            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            return DriverManager.getConnection(url, user, password);
        }
    }

    public static void saveFiles(List<String[]> files) throws Exception {
        try (Connection conn = getConnection()) {
            // Prepare statements for checking and inserting files
            String checkQuery = "SELECT COUNT(*) FROM uploaded_files WHERE file_name = ? AND file_path = ?";
            String insertQuery = "INSERT INTO uploaded_files (file_name, file_path, upload_time) VALUES (?, ?, ?)";
    
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                 PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
    
                for (String[] file : files) {
                    // Check if the file already exists in the database
                    checkStmt.setString(1, file[0]); // file_name
                    checkStmt.setString(2, file[1]); // file_path
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            // File already exists, skip insertion
                            System.out.println("Skipping duplicate file: " + file[0]);
                            continue;
                        }
                    }
    
                    // Insert the file into the database
                    insertStmt.setString(1, file[0]); // file_name
                    insertStmt.setString(2, file[1]); // file_path
                    insertStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // current timestamp
                    insertStmt.addBatch();
                }
    
                // Execute the batch insert
                insertStmt.executeBatch();
            }
        }
    }

    public static List<String[]> getUploadedFiles() throws Exception {
        List<String[]> files = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT file_name, file_path, upload_time FROM uploaded_files");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                files.add(new String[]{
                    rs.getString("file_name"),
                    rs.getString("file_path"),
                    rs.getTimestamp("upload_time").toString() 
                });
            }
        }
        return files;
    }

    public static void deleteAllFiles() throws Exception {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM uploaded_files")) {
            stmt.executeUpdate();
        }
    }

    public static void deleteFile(String fileName) throws Exception {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM uploaded_files WHERE file_name = ?")) {
            stmt.setString(1, fileName);
            stmt.executeUpdate();
        }
    }
}