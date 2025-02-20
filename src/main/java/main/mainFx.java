package main;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class    mainFx extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {



        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/LocationCrud.fxml"));
        Scene scene = new Scene(fxmlLoader.load());



        primaryStage.setScene(scene);
        primaryStage.setTitle("Location management system");




        primaryStage.show();

    }
    public static void main(String[] args) {
        launch(args);
    }
}