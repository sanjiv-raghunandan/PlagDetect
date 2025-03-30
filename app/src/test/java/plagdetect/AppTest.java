package plagdetect;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

class AppTest {
    @Test 
    void testDatabaseConnection() {
        Properties props = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                fail("config.properties file not found in resources folder.");
            }
            props.load(input);
        } catch (IOException e) {
            fail("Failed to load config.properties: " + e.getMessage());
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            assertNotNull(conn, "Database connection should not be null");
        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }
}
