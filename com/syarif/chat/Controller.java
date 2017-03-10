package com.syarif.chat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Created by Arkad on 3/10/2017.
 */
public class Controller {


    private String username;
    private String ipAdresse;
    private boolean isServer;
    private NetworkConnection connection;
    //= isServer ? createServer() : createClient();


    @FXML
    private TextArea messagesTextArea;
    @FXML
    private TextField textMessage;
    @FXML
    private TextField name;
    @FXML
    private Button connectButton;
    @FXML
    private TextField ip;
    @FXML
    private CheckBox connectAs;


    /*public void initialize() throws Exception{
        connection.startConnection();
    }*/

    public void connect()throws Exception{
        ipAdresse = ip.getText();
        username = name.getText();

        if (connectAs.isSelected()){
            connection = createServer();
        }else{
            connection = createClient();
        }

        connection.startConnection();

        name.setDisable(true);
        ip.setDisable(true);
        connectButton.setDisable(true);

    }



    public void sendMessage(){
            //String message = isServer ? "Server: " : "Client: ";
            String message = username+": ";
            message += textMessage.getText();
            textMessage.clear();

            messagesTextArea.appendText(message + "\n" );

            try {
                connection.send(message);
            } catch (Exception e) {
                messagesTextArea.appendText("Failed to send\n" );
            }

    }

    private Server createServer(){
        return  new Server(55555, data -> {
            Platform.runLater(()->{
                messagesTextArea.appendText(data.toString()+ '\n');
            });
        });
    }

    // Put the private ip address of the server. not the client.
    private Client createClient(){
        return new Client(ipAdresse, 55555, data ->{
            messagesTextArea.appendText(data.toString()+ '\n');
        });
    }

}

//31.10.129.108