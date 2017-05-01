package ch.unibas.dmi.cs108.sand;

import ch.unibas.dmi.cs108.sand.gui.ClientController;
import ch.unibas.dmi.cs108.sand.gui.LoginController;
import ch.unibas.dmi.cs108.sand.gui.ServerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    /** Start the application and show the login ch.unibas.dmi.cs108.sand.gui */
    @Override
    public void start(Stage primaryStage) throws Exception{
        URL resource = LoginController.class.getResource("/fxml/login.fxml");
        Parent root = FXMLLoader.load(resource);
        primaryStage.setTitle("Welcome to SAND");
        primaryStage.setScene(new Scene(root, 400, 275));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        if (ClientController.getClient() != null){
            ClientController.getClient().requestLogout();
        }else if (ServerController.getMyServer() != null) {
            try {
                System.exit(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}