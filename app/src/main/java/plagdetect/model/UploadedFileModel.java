package plagdetect.model;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UploadedFileModel {

    private static Connection getConnection() throws Exception {
        try (InputStream input = UploadedFileModel.class.getClassLoader().getResourceAsStream("config.properties")) {
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
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO uploaded_files (file_name, file_path) VALUES (?, ?)")) {

            for (String[] file : files) {
                stmt.setString(1, file[0]); // file_name
                stmt.setString(2, file[1]); // file_path
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public static List<String[]> getUploadedFiles() throws Exception {
        List<String[]> files = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT file_name, file_path FROM uploaded_files");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                files.add(new String[]{rs.getString("file_name"), rs.getString("file_path")});
            }
        }
        return files;
    }
}