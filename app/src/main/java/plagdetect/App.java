package plagdetect;

import plagdetect.view.SampleUI;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        Properties props = new Properties();

        try (InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("config.properties file not found in resources folder.");
                return;
            }
            props.load(input);
        } catch (IOException e) {
            System.err.println("Failed to load config.properties: " + e.getMessage());
            return;
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        List<String[]> userData = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("id"));
                String name = rs.getString("name");
                String email = rs.getString("email");
                userData.add(new String[]{id, name, email});
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return;
        }

        // Now pass the extracted data to the UI
        SwingUtilities.invokeLater(() -> new SampleUI(userData));
    }
}
