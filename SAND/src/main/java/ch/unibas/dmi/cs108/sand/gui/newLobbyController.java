package ch.unibas.dmi.cs108.sand.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Created by Syarif on 4/30/2017.
 */
public class newLobbyController {
    @FXML
    private TextField lobbyName;

    public String getLobbyName() {
        return lobbyName.getText();
    }
}
