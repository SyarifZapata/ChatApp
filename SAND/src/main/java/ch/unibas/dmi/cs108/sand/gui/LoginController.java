package ch.unibas.dmi.cs108.sand.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/** This is the controller of the login ch.unibas.dmi.cs108.sand.gui */
public class LoginController {

    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldIP;
    @FXML
    private CheckBox checkBoxConnectAs;
    @FXML
    private TextField textFieldPort;
    @FXML
    private Button connectBtn;
    @FXML
    private Label warning;

    /** Hide warning label */
    public void initialize(){
        warning.setVisible(false);
				setCompName();
    }
		
    /** connect button is pressed, if Checkbox is checked, call the Sever scene,
     *  otherwise client scene will be called
     *  User inputs are validated here.
     *  We pass validated user inputs to the corresponding controller*/
    public void connect() throws Exception {
        Parent root;
        if (checkBoxConnectAs.isSelected()) {
            if(textFieldIP.getText().equals("")){
                warning.setText("Server-Ip cannot be empty");
                warning.setVisible(true);
            }else if(textFieldPort.getText().equals("")){
                warning.setText("Port cannot be empty");
                warning.setVisible(true);
            }else {
                try {
                    //URL resource = (getClass().getResource("Server.fxml"));
                    URL resource = ServerController.class.getResource("/fxml/Server.fxml");
                    //System.out.println(resource);
                    //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Server.fxml"));
                    FXMLLoader fxmlLoader = new FXMLLoader(resource);
                    root = fxmlLoader.load();
                    ServerController controller = fxmlLoader.<ServerController>getController();
                    controller.setPort(Integer.valueOf(textFieldPort.getText()));

                    Stage stage = new Stage();
                    stage.setTitle("Server");
                    stage.setScene(new Scene(root, 600, 400));
                    stage.setResizable(false);
                    stage.show();
                    // Hide this current window (if this is what you want)
                    connectBtn.getScene().getWindow().hide();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if(textFieldIP.getText().equals("")){
                warning.setText("Server-Ip cannot be empty");
                warning.setVisible(true);
            }else if(textFieldPort.getText().equals("")){
                warning.setText("Port cannot be empty");
                warning.setVisible(true);
            }else if(textFieldName.getText().equals("")){
                warning.setText("Username cannot be empty");
                warning.setVisible(true);
            }else {
                try {
                    URL resource = ClientController.class.getResource("/fxml/Client.fxml");
                    //System.out.println(resource);
                    FXMLLoader fxmlLoader = new FXMLLoader(resource);
                    root = fxmlLoader.load();
                    ClientController controller = fxmlLoader.<ClientController>getController();
                    controller.setPort(Integer.valueOf(textFieldPort.getText()));
                    // pass user inputs to the controller
                    controller.setIp(textFieldIP.getText());
                    controller.setUserName(textFieldName.getText());
                    Stage stage = new Stage();
                    stage.setTitle("Client-" + textFieldName.getText());
                    stage.setScene(new Scene(root, 880, 600));


                    stage.setX(100);
                    stage.setResizable(false);
                    stage.show();
                    // Hide this current window (if this is what you want)
                    connectBtn.getScene().getWindow().hide();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
		
/** Get computername and suggest as nickname */
		public void setCompName() {
				try {
					InetAddress localMachine = InetAddress.getLocalHost();
					String name = localMachine.getHostName();
					name = name.substring(0, name.lastIndexOf("-"));
					textFieldName.textProperty().set(name);
				} catch (UnknownHostException e) {
					System.out.println("Hostname not resolved");
				}
		}

}
