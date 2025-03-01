package main;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class    mainFx extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {



        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/LocationCrud.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/style/LocationCrud.css").toExternalForm());


        primaryStage.setScene(scene);
        primaryStage.setTitle("Location management system");




        primaryStage.show();

    }
    public static void main(String[] args) {
        launch(args);
    }
}