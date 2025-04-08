package plagdetect;

//public class App {
//    public static void main(String[] args) {
//        System.out.println("Testing JPlag module import...");
//     
//        try {
//            // Just check if the base JPlag class is available
//            Class<?> jplagClass = Class.forName("de.jplag.JPlag");
//         
//            System.out.println("SUCCESS: JPlag class found: " + jplagClass.getName());
//         
//            // Check for specific classes to understand the package structure
//            try {
//                Class<?> optionsClass = Class.forName("de.jplag.options.JPlagOptions");
//                System.out.println("JPlagOptions class found at: " + optionsClass.getName());
//            } catch (ClassNotFoundException e) {
//                System.out.println("JPlagOptions class not found in 'de.jplag.options' package");
//            }
//         
//            try {
//                Class<?> languageOptionClass = Class.forName("de.jplag.options.LanguageOption");
//                System.out.println("LanguageOption class found at: " + languageOptionClass.getName());
//            } catch (ClassNotFoundException e) {
//                System.out.println("LanguageOption class not found in 'de.jplag.options' package");
//            }
//         
//            // Try alternative package structures
//            try {
//                Class<?> languageOptionAlt = Class.forName("de.jplag.LanguageOption");
//                System.out.println("LanguageOption class found at: " + languageOptionAlt.getName());
//            } catch (ClassNotFoundException e) {
//                System.out.println("LanguageOption class not found in 'de.jplag' package");
//            }
//         
//        } catch (ClassNotFoundException e) {
//            System.err.println("ERROR: JPlag class not found. Make sure the dependency is correctly configured.");
//            System.err.println("You may need to update your build.gradle file.");
//        } catch (Exception e) {
//            System.err.println("ERROR: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}

//package plagdetect;

import javafx.application.Application;
import plagdetect.view.UI;

import de.jplag.JPlag;
public class App {
    public static void main(String[] args) {
        // Launch JavaFX application
        Application.launch(UI.class, args);
    }
}
