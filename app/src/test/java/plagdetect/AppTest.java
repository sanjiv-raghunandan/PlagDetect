package plagdetect;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JPanel;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

import de.jplag.JPlag;
import de.jplag.options.JPlagOptions;
import de.jplag.java.JavaLanguage;
// import de.jplag.options.LanguageOptions;
import de.jplag.JPlagResult;
import de.jplag.Language;
import de.jplag.exceptions.ExitException;
import de.jplag.reporting.reportobject.ReportObjectFactory;
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
    @Test
    void testJPlag(){
        String SUB_DIR = "../../resources/submissions/";
        String RES_DIR = "../../resources/results/";
        Set<File> submissionDirs = Set.of(new File(SUB_DIR));

        Language language = new JavaLanguage();
        JPlagOptions options = new JPlagOptions(language, submissionDirs, Set.of());
        try{
            JPlagResult result = JPlag.run(options);
            File resultZip = File.createTempFile("result",".zip" );
            ReportObjectFactory reportObjectFactory = new ReportObjectFactory(resultZip);
            reportObjectFactory.createAndSaveReport(result);

            

            if (result!=null){
                System.out.println("JPlag completed successfully");

            }
            else{
                System.out.println("JPlag failed to complete");
            }
        }
        catch(ExitException e){
            System.out.println("JPlag exited with code");
        }
        catch(Exception e){
            fail("JPlag failed: "+ e.getMessage());
        }
}
}