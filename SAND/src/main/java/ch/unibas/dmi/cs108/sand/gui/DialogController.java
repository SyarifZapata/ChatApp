package ch.unibas.dmi.cs108.sand.gui;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Created by Syarif on 24.03.17.
 */
public class DialogController {

    @FXML
    private TextField username;

    public String changeUsername(){
        String newUsername = username.getText();
        return newUsername;
    }

}
