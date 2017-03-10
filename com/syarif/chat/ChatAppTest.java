package com.syarif.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatAppTest extends Application {

    @Override
    public void start(Stage primaryStage)throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("mainwindow.fxml"));
        primaryStage.setScene(new Scene(root, 600,600));
        primaryStage.show();
    }


    // The main method will launch the application.
    public static void main(String[] args) {
        launch(args);
    }


}

