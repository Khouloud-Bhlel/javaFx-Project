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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDashboard.fxml"));
            Parent root = loader.load();

            // Set up main window
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style/main.css").toExternalForm());

            primaryStage.setTitle("QuickMove - Carpool System");
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
        scene.getStylesheets().add(getClass().getResource("/style/main.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}