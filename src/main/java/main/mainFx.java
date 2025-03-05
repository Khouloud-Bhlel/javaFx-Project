package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mainFx extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        showMainDashboard();
    }

    public void showMainDashboard() {
        try {
            // Ensure the path matches the FXML's location in resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDashboard.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            // Verify the CSS path as well
            scene.getStylesheets().add(getClass().getResource("/style/UserDashboard.css").toExternalForm());

            primaryStage.setTitle("User Dashbord");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLocationManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationCrud.fxml"));
            Parent root = loader.load();
            showScene(root, "Location Management");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCalendarView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/calendar-view.fxml"));
            Parent root = loader.load();
            showScene(root, "Calendar View");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showScene(Parent root, String title) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/LocationCrud.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}