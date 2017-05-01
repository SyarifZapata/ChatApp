package ch.unibas.dmi.cs108.sand.gui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Arkad on 4/5/2017.
 */
public class LobbyController {
    static boolean trumpsSelected;
    static boolean mexicansSelected;

    @FXML
    private ImageView imageViewTrump;
    @FXML
    private ImageView imageViewMexican;
    @FXML
    private TextFlow lobbyMessages;
    @FXML
    private TextField messageTextField;
    @FXML
    private Button startGameButton;

    private ObservableList<String> ObsGamesList;


    public void initialize(){
        messageTextField.requestFocus();

        Image trump = new Image(getClass().getResourceAsStream("/img/trump2.gif"));
        imageViewTrump.setImage(trump);
        imageViewTrump.setFitHeight(300);
        imageViewTrump.setFitWidth(300);

        Image mexican = new Image(getClass().getResourceAsStream("/img/sombrero.gif"));
        imageViewMexican.setImage(mexican);
        imageViewMexican.setFitHeight(300);
        imageViewMexican.setFitWidth(300);

    }

    public void trumpsChoosen(){
        if(trumpsSelected){
            trumpsSelected = false;
            imageViewMexican.setDisable(false);
            imageViewMexican.setImage(new Image(getClass().getResourceAsStream("/img/sombrero.gif")));
        }else{
            trumpsSelected = true;
            imageViewMexican.setImage(new Image(getClass().getResourceAsStream("/img/trump2.gif")));
            System.out.println("trump's selected");
            imageViewMexican.setDisable(true);
        }
    }

    public void mexicansChoosen(){
        if(mexicansSelected){
            mexicansSelected = false;
            imageViewTrump.setDisable(false);
            imageViewTrump.setImage(new Image(getClass().getResourceAsStream("/img/trump2.gif")));
        }else {
            mexicansSelected = true;
            imageViewTrump.setImage(new Image(getClass().getResourceAsStream("/img/sombrero.gif")));
            System.out.println("mexicans' selected");
            imageViewTrump.setDisable(true);
        }
    }

    public void send(){
        ClientController.getClient().lobbyChat(messageTextField.getText());
        messageTextField.clear();
    }

    public void hitEnter(){
        send();
    }

    public void startGame(){
        ClientController.getClient().startGame(mexicansSelected);
        startGameButton.setDisable(true);
        lobbyMessages.getChildren().add(new Text("Waiting for the second client to press the start game button"+"\n"));

    }

    public void setLobbyMessages(String clientName, String messages){
        String show = messages.replace("\\%", "%");//msg needs to be final..
        Text text = new Text(show + "\n");
        if (show.startsWith(clientName)) {
            text.setStyle("-fx-font-size: 12; -fx-fill: #0b701c;");
        } else {
            text.setStyle("-fx-font-size: 12; -fx-fill: #2b6acc;");
        }
        Platform.runLater(
                () -> {
                    lobbyMessages.getChildren().add(text);
                }
        );
    }


    public void clearGamesList(){
        ObsGamesList.clear();
    }

    public void hideWindow(){
        startGameButton.getScene().getWindow().hide();
    }

    /** Open default browser and go to our blog */
    public void link(){
        try {
            Desktop.getDesktop().browse(new URI("http://www.sand-unibas.blogspot.com"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
