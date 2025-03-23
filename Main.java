import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();
        
        try {
            // Load database credentials from config.properties
            FileInputStream fis = new FileInputStream("config.properties");
            props.load(fis);

            String URL = props.getProperty("db.url");
            String USER = props.getProperty("db.user");
            String PASSWORD = props.getProperty("db.password");

            // Connect to MySQL
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL!");

            // Execute query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");

            // Print results
            System.out.println("\n Users List:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Email: " + rs.getString("email"));
            }

            // Close connection
            conn.close();
            System.out.println("\nConnection Closed!");
        } catch (IOException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
