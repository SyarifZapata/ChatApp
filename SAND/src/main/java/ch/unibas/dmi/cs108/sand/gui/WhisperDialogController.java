package ch.unibas.dmi.cs108.sand.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Created by Arkad on 4/19/2017.
 */
public class WhisperDialogController {
    @FXML
    private TextField userId;

    public int getUserId(){
        String id = userId.getText();
        return Integer.valueOf(id);
    }
}
