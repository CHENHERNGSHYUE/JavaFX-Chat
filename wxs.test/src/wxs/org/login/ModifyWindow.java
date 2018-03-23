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

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

import org.omg.CORBA.FREE_MEM;

public class ModifyWindow  implements Initializable {
	@FXML
    private Label theme;

    @FXML Label notification;

    @FXML
    private JFXPasswordField password;
    
    @FXML
    JFXTextField question;
    
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
    
    private static Scene chatScene;
    private static boolean connected;
    private static Stage mainWindow;
    private static Scene loginScene;
    private static BlockingQueue<String> toServerMessage;
    private static BlockingQueue<String> fromServerMessage;
    static Client client;
    private static boolean called = false;
    
	private String currentUsrid = "";
    
    //initial the varibles
    public void start(Stage mainWindow,Scene loginScene,
    		BlockingQueue<String> toServerMessage,
    		BlockingQueue<String> fromServerMessage,
    		Client client,boolean connected) {
    	this.mainWindow = mainWindow;
    	this.loginScene = loginScene;
    	this.toServerMessage = toServerMessage;
    	this.fromServerMessage = fromServerMessage;
    	this.client = client;
    	this.connected = connected;
    }
    String toShow = null;
	public void Modify(MouseEvent mouseevent) {
		String pwd = password.getText();
		String usrid = userID.getText();
		String ans = answer.getText();
		startConnectionToServer();
		toServerMessage.add("20" + usrid);
		toServerMessage.add("22" + ans);
		toServerMessage.add("23" + pwd);
	}
	public void show(MouseEvent mouseEvent) {
		String usr = userID.getText();
		if(!usr.equals(currentUsrid)) {
			toServerMessage.add("60"+usr);
		}
		startConnectionToServer();
		while(true) {
			String line;
			try {
				line = fromServerMessage.take();
				if(line.startsWith("@12")) {
					if(line.equals("@12un_show")) {
						notification.setText("Incorrect userId");
					}
					else {
						question.setText(line.substring(3));
					}
					break;
				}
				fromServerMessage.add(line);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void Back(MouseEvent mouseevent) {
		mainWindow.setScene(loginScene);
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}
	private void startConnectionToServer(){
        if(!connected) {
            client.startConServer();
            connected = true;
        }
    }
}
