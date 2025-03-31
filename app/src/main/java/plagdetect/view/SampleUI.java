// package plagdetect.view;

// import javafx.application.Application;
// import javafx.scene.Scene;
// import javafx.scene.control.TableColumn;
// import javafx.scene.control.TableView;
// import javafx.scene.control.cell.PropertyValueFactory;
// import javafx.stage.Stage;
// import javafx.scene.layout.VBox;
// import javafx.geometry.Insets;
// import plagdetect.model.User;

// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Properties;
// import java.util.Collections;
// import java.io.InputStream;

// public class SampleUI extends Application {

//     private TableView<User> tableView = new TableView<>();

//     @Override
//     public void start(Stage primaryStage) {
//         TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
//         idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//         idColumn.setPrefWidth(50);
//         idColumn.setResizable(false);

//         TableColumn<User, String> nameColumn = new TableColumn<>("Name");
//         nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//         nameColumn.setPrefWidth(150);
//         nameColumn.setResizable(false);

//         TableColumn<User, String> emailColumn = new TableColumn<>("Email");
//         emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
//         emailColumn.setPrefWidth(200);
//         emailColumn.setResizable(false);

//         Collections.addAll(tableView.getColumns(), idColumn, nameColumn, emailColumn);
//         tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//         tableView.getColumns().forEach(col -> col.setReorderable(false));

//         loadData(); // Fetch data from database

//         VBox vbox = new VBox();
//         vbox.setSpacing(5);
//         vbox.setPadding(new Insets(10));
//         vbox.getChildren().add(tableView);

//         Scene scene = new Scene(vbox, 400, 250);
//         primaryStage.setScene(scene);
//         primaryStage.setTitle("User Data");
//         primaryStage.show();
//     }

//     private void loadData() {
//         List<User> userList = new ArrayList<>();

//         try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
//             Properties props = new Properties();
//             if (input != null) {
//                 props.load(input);
//             } else {
//                 System.err.println("config.properties file not found.");
//                 return;
//             }

//             String url = props.getProperty("db.url");
//             String user = props.getProperty("db.user");
//             String password = props.getProperty("db.password");

//             try (Connection conn = DriverManager.getConnection(url, user, password);
//                  PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
//                  ResultSet rs = stmt.executeQuery()) {

//                 while (rs.next()) {
//                     userList.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
//                 }
//                 tableView.getItems().setAll(userList);
//             }

//         } catch (Exception e) {
//             System.err.println("Error loading data: " + e.getMessage());
//         }
//     }

//     public static void main(String[] args) {
//         launch(args);
//     }
// }
