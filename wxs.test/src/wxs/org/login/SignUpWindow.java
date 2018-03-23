package wxs.org.login;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import wxs.org.Client.Client;
import wxs.org.Client.ConnectionToServer;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.omg.CORBA.FREE_MEM;

public class SignUpWindow implements Initializable {
	@FXML
    private Label theme;

    @FXML Label notification;

    @FXML
    private JFXPasswordField password;
    
    @FXML
    private JFXTextField question;
    
    @FXML
    private JFXTextField answer;
    
    @FXML
    private JFXButton backButton;
    
    @FXML
    private JFXButton signUpButton;

    @FXML
    private JFXTextField userID;

    @FXML
    private JFXButton loginButton;
    
    private boolean connected;
    
    private static Stage mainWindow;
    private static Scene loginScene;
    private static BlockingQueue<String> toServerMessage;
    private static Client client;
    private boolean called = false;
    
    
    //initial the varibles
    public void start(Stage mainWindow,Scene loginScene,
    		BlockingQueue<String> toServerMessage,Client client,
    		boolean connected) {
    	this.mainWindow = mainWindow;
    	this.loginScene = loginScene;
    	this.toServerMessage = toServerMessage;
    	this.client = client;
    	System.out.println("this.client = " + this.client + " client = " + client);
    	this.connected = connected;
    }
	public void Signup(MouseEvent mouseevent) {
		String passwords = password.getText();
        String userId = userID.getText();
        String Answer = answer.getText();
        String Question = question.getText();
        
        System.out.println("client = " + client);
        startConnectionToServer();

        System.out.println("toServer" + toServerMessage);
        
        toServerMessage.add("00"+userId);
        toServerMessage.add("01"+passwords);
        toServerMessage.add("02"+Answer);
        toServerMessage.add("03"+Question);
	}
	
	public void Back(MouseEvent mouseevent) {
		System.out.println("loginScene = " + loginScene);
		mainWindow.setScene(loginScene);
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}
	
	private void startConnectionToServer(){
		System.out.println("client:" + client);
        if(!connected) {
            client.startConServer();;
            connected = true;
        }
    }
}
