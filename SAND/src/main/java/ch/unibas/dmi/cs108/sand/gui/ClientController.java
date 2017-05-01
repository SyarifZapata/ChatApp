package ch.unibas.dmi.cs108.sand.gui;


import ch.unibas.dmi.cs108.sand.network.Client;
import ch.unibas.dmi.cs108.sand.network.CommandList;
import ch.unibas.dmi.cs108.sand.network.Message;
import ch.unibas.dmi.cs108.sand.network.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Nils, Syarif on 3/15/2017.
 * Client side of the chat application
 * All messages are Objects of Message class
 */
public class ClientController {

    private static Client client;
    private int port;
    private String ip;
    private static String userName;
    private ObservableList<String> gamesList;
    private static boolean isMexican;
    private GameControllerTrump trumpController;
    private GameControllerMexican mexicanController;
    private LobbyController newGameController;
    private FXMLLoader gameLoader;
    private Parent root;
    private boolean isWhispering;
    private int idToWhisper;
    private int yourId;
    private String lobbyName;

    //Todo Liste: Wenn beide Trump auswaehlen. FXLoader anpassen


    @FXML
    private TextFlow textAreaMessages;
    @FXML
    private TextField textFieldMessage;
    @FXML
    private TableView<User> clientList;
    @FXML
    private TableColumn<User,Integer> id;
    @FXML
    private TableColumn<User, String> clientNames;
    @FXML
    private ListView<String> openedGames;
    @FXML
    private BorderPane clientBorderPane;
    @FXML
    private javafx.scene.control.MenuItem whisperModus;
    @FXML
    private MenuItem exitWhisper;


    /** erstelle neuen Client und überlade die Methode display() */
    public void initialize()throws Exception{
        isWhispering = false;
        exitWhisper.setDisable(true);
        textFieldMessage.requestFocus();
        clientList.setFocusTraversable(false);
        openedGames.setFocusTraversable(false);
        openedGames.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    String gameNameSelected = openedGames.getSelectionModel().getSelectedItem();
                    client.enterGameLobby(gameNameSelected);
                    startNewGame();
                }
            }
        });

        (new Thread(){
            public void run() {
                client = new Client(ip, userName, port) {

                    public void display(CommandList command, String msg) {
                        switch (command) {
                            case WELCOME:
                                Platform.runLater(
                                        () -> {
                                            Text text = new Text("Welcome to SAND, " + userName + "\n" + "Your Id is " + msg + "\n");
                                            text.setStyle("-fx-font-size: 14; -fx-fill: #cb5217;");
                                            textAreaMessages.getChildren().add(text);
                                            yourId = Integer.valueOf(msg);
                                        }
                                );

                                break;
                            case MESSAGE:
                                Platform.runLater(
                                        () -> {
                                            String show = msg.replace("\\%", "%");//msg needs to be final..
                                            Text text = new Text(show + "\n");
                                            if (show.startsWith(userName)) {
                                                text.setStyle("-fx-font-size: 12; -fx-fill: #0b701c;");
                                            } else {
                                                text.setStyle("-fx-font-size: 12; -fx-fill: #2b6acc;");
                                            }
                                            textAreaMessages.getChildren().add(text);
                                        }
                                );

                                break;
                        }
                    }

                    // Todo overload showAvailableGames, show in a ListView
                    public void showAvailableGames(ArrayList<String> games) {
                        try {
                            gamesList = FXCollections.observableArrayList(games);
                            Platform.runLater(
                                    () -> {
                                        openedGames.setItems(gamesList);
                                    }
                            );

                        } catch (NullPointerException e) {
                            newGameController.clearGamesList();
                        }
                    }

                    /**
                     * practically update character in GUI
                     */
                    public void sendCharacter(String updates) {
                        if (trumpController != null) {
                            trumpController.setPos(updates);
                            System.out.println(updates);
                        } else if (mexicanController != null) {
                            mexicanController.setPos(updates);
                        }
                    }

                   public void letsPlayGame(CommandList playAs) {

                       if (playAs == CommandList.MEXICAN) {
                           //TODO play as Mex
                           gameLoader = new FXMLLoader(getClass().getResource("/fxml/gameWindowMexican.fxml"));
                           System.out.println("playing as mex");
                       } else if (playAs == CommandList.TRUMP) {
                           //TODO playAs Trump
                           gameLoader = new FXMLLoader(getClass().getResource("/fxml/gameWindowTrump.fxml"));
                           System.out.println("playing as trump");
                       }

                       Platform.runLater(
                               () -> {
                                   Parent root = null;
                                   try {
                                       root = gameLoader.load();
                                   } catch (IOException e) {
                                       e.printStackTrace();
                                   }
                                   System.out.println("lets start the party");
                                   Stage gameStage = new Stage();
                                   gameStage.setTitle("Trampeltier VS Sombreros");
                                   gameStage.setScene(new Scene(root, 1200, 700));
                                   gameStage.setResizable(false);
                                   gameStage.show();
                                   newGameController.hideWindow();
                               }
                       );

                   }

                    public void displayLobbyChat(String message){
                       newGameController.setLobbyMessages(userName,message);
                    }

                    public void showAvailableClients(ArrayList<User> users) {
                       ObservableList<User> userList = FXCollections.observableArrayList(users);
                       System.out.println(userList.get(0).getName());
                       id.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));
                       clientNames.setCellValueFactory(new PropertyValueFactory<User, String>("name"));
                       clientList.setItems(userList);
                    }

                    /*public void letsPlayGame() {


                    }*/

                };

            }
        }).start();

    }

    /** Get text from Chat-Input and send it to the server */
    public void send(){
        String getText = textFieldMessage.getText();
        getText = getText.replace("%","\\%");
        if(isWhispering){
            Message message = new Message(CommandList.WHISPER_CHAT,String.valueOf(yourId)+"$"+textFieldMessage.getText()+"$"+String.valueOf(idToWhisper));
            client.writeToServer(message);
        }else{
            Message message = new Message(getText);
            client.writeToServer(message);

        }
        textFieldMessage.clear();
    }

    /** call the send method if user hit enter after typed in the message */
    public void hitEnter(){
        send();
    }

    /** These setter method are used to pass user inputs from the login dialog to this controller */
    public void setPort(int port) {
        this.port = port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static String getUserName(){
        return userName;
    }

    /** Exit button from menu */
    public void logout(){
        client.requestLogout();
    }

    public static Client getClient(){
        return client;
    }

    @FXML
    public void startWhisperModus(){
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.initOwner(clientBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/whisperDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            WhisperDialogController controller = fxmlLoader.getController();
            System.out.println("whispering started");
            idToWhisper = controller.getUserId();
            System.out.println(idToWhisper);
        } else {
            System.out.println("Cancel pressed");
        }

        isWhispering = true;
        whisperModus.setDisable(true);
        exitWhisper.setDisable(false);
    }

    @FXML
    public void exitWhisperModus(){
        isWhispering = false;
        whisperModus.setDisable(false);
        exitWhisper.setDisable(true);
    }


    @FXML
    public void showUsernameDialog() {
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.initOwner(clientBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/settingsDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            System.out.println("OK pressed");
            userName = controller.changeUsername(); // username changed
            client.requestNewName(userName);
            Stage stage = (Stage)clientBorderPane.getScene().getWindow();
            stage.setTitle("Client-" + userName);
        } else {
            System.out.println("Cancel pressed");
        }
    }

    @FXML
    public void startNewGame(){
        Parent root = null;
        URL resource = ServerController.class.getResource("/fxml/Lobby.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(resource);


        try {
            root = fxmlLoader.load();
            newGameController = fxmlLoader.getController();
        } catch (IOException e) {
            System.out.println("Couldn't load new-game dialog");
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Server");
        stage.setScene(new Scene(root, 630, 650));
        stage.setResizable(false);
        stage.show();




        /*if (result.isPresent() && result.get() == ButtonType.OK) {
            // Todo get selected game to join

            //String gameToJoin = newGameController.joinGame();

            if (LobbyController.trumpsSelected) {
                try {
                    textAreaMessages.getChildren().add(new Text("Waiting for another client to join \n"));
                    //Todo join game as Trump
                   // client.enterGameServer(gameToJoin,false);

                    isMexican = false;
                    gameLoader = new FXMLLoader(getClass().getResource("/fxml/gameWindowTrump.fxml"));
                    root = gameLoader.load();

                    // Create reference of TrumpController
                    trumpController = gameLoader.getController();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (LobbyController.mexicansSelected) {
                try {
                    textAreaMessages.getChildren().add(new Text("Waiting for another client to join \n"));
                    // Todo join game as Mexican
                    //client.enterGameServer(gameToJoin,true);

                    isMexican = true;
                    gameLoader = new FXMLLoader(getClass().getResource("/fxml/gameWindowMexican.fxml"));
                    root = gameLoader.load();

                    // Create reference of MexicanController
                    mexicanController = gameLoader.getController();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            System.out.println("Cancel pressed");
        }*/
    }

    /** Start a new game by creating a lobby and wait for other clients to join */

    @FXML
    public void createLobby(){
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.initOwner(clientBorderPane.getScene().getWindow());
        dialog.setTitle("Start a new game");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/createLobby.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load create-lobby dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
           newLobbyController controller = fxmlLoader.getController();
           lobbyName = controller.getLobbyName();
           ClientController.getClient().requestNewGameServer(lobbyName);

        }else{
            System.out.println("Cancel pressed");
        }
    }

    public static boolean isMexican() {
        return isMexican;
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
