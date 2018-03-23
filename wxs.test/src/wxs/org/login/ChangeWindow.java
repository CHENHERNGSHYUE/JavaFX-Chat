package wxs.org.login;

import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.omg.Messaging.SyncScopeHelper;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ChangeWindow {
	private static boolean called = false;
	private BlockingQueue<Tasks> tasks;
	private static BlockingQueue<String> fromServerMessage;
	
	@FXML
    private static Stage mainWindow;
    private static Scene loginScene;
    private static Scene signupScene;
    private static Scene modifyScene;
    private static Scene chatScene;
    
    private LoginWindow login;
	private ModifyWindow modify;
    private SignUpWindow signup;
    private MainWindow chat;
    
    private String question;
    public ChangeWindow(Stage mainWindow,Scene loginScene,
    		Scene signupScene,Scene modifyScene,
    		Scene chatScene,LoginWindow login,
    		ModifyWindow modify,SignUpWindow signup,
    		MainWindow chat,
    		BlockingQueue<Tasks> tasks,
    		BlockingQueue<String> fromServerMessage) {
    	this.mainWindow = mainWindow;
    	this.loginScene = loginScene;
    	this.signupScene = signupScene;
    	this.modifyScene = modifyScene;
    	this.chatScene = chatScene;
    	this.tasks = tasks;
    	this.fromServerMessage = fromServerMessage;
    	this.login = login;
    	this.modify = modify;
    	this.signup = signup;
    	this.chat = chat; 
    	//ChangeWindow.notification = notification;
    }
    
    public void giveQuestion(String question) {
    	this.question = question;
    }
    
    Task<Void> listen() {
    	//System.out.println(notification);
    	Task<Void> listener = new Task<Void>() {

    		@Override
    		protected Void call() throws Exception {
    			// TODO Auto-generated method stub
    			while(!called) {
    				Tasks task = tasks.take();
    				//System.out.println(task);
    				switch (task) {
    				//Sign in successfully!
					case sign_in:
						Thread.sleep(100); 
						//chatScene = new Scene(FXMLLoader.load(getClass().getResource("MainWindow.fxml")));
						Platform.runLater(()->mainWindow.setScene(chatScene));
						break;
					case unsign_in:
						Platform.runLater(()->login.notification.setText("Incorrect username or password."));
						break;
					case sign_up: 
						Platform.runLater(()->mainWindow.setScene(loginScene));
						break;
					case unsign_up:
						Platform.runLater(()->mainWindow.setScene(signupScene));
						Platform.runLater(()->signup.notification.setText("Incorrect username or password."));
						System.out.println("unsign up");
						break;
					case modify_pwd:
						Platform.runLater(()->mainWindow.setScene(loginScene));
						break;
					case unmodify_pwd:
						Platform.runLater(()->mainWindow.setScene(modifyScene));
						Platform.runLater(()->modify.notification.setText("Incorrect answer."));
						break;
					case show:
						tasks.add(Tasks.show);
						break;
					case un_show:
						tasks.add(Tasks.show);
						break;
					case refresh:
						
						break;
					default:
						called = true;
						tasks.add(Tasks.ok);
						break;
					}
    			}	
    			return null;
    		}
       	};
    	new Thread(listener).start();
    	return listener;
    }
}
