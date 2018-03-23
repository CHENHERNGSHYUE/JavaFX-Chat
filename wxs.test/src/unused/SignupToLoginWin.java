package unused;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SignupToLoginWin {
	private static boolean called = false;
	private static ConcurrentLinkedQueue<String> fromServerMessage;
	
	@FXML
    private static Stage mainWindow;
    private static Scene mainScene;
    
    @FXML
    private static Label notification;
    
    static ExecutorService executor;
    public static void start(Stage mainWindow,Scene mainScene,ConcurrentLinkedQueue<String> fromServerMessage,Label notification) {
    	SignupToLoginWin.mainWindow = mainWindow;
    	SignupToLoginWin.mainScene = mainScene;
    	SignupToLoginWin.fromServerMessage = fromServerMessage;
    	SignupToLoginWin.notification = notification;
    }
    
    static Task<Void> listen() {
    	System.out.println(notification);
    	Task<Void> listener = new Task<Void>() {

    		@Override
    		protected Void call() throws Exception {
    			// TODO Auto-generated method stub
    			while(!called) {
    				while(!fromServerMessage.isEmpty()) {
    					//Attention:until exit this will be over
    					//System.out.println("Loginwindow" + fromServerMessage);
    					if(fromServerMessage.peek().equals("Log in successfully!")) {
    						fromServerMessage.poll();
    						Platform.runLater(()->mainWindow.setScene(mainScene));
    					}
    					else if(fromServerMessage.peek().equals("Log in unsuccessfully!")) {
    						fromServerMessage.poll();
    						Platform.runLater(()->notification.setText("Incorrect username or password."));
    					}
    					else if (fromServerMessage.peek().equals("ok")) {
							called = true;
						}
    				}
    			}
    			return null;
    		}
    	};
    	new Thread(listener).start();
    	return listener;
    }
	
}
