package unused;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
/*
 a new thread to switch the signin window to chating window 
 */
public class LogintoChatWin {
	private static boolean called = false;
	private static BlockingQueue<String> fromServerMessage;
	
	@FXML
    private static Stage mainWindow;
    private static Scene mainScene;
    
    @FXML
    private static Label notification;
    
    static ExecutorService executor;
    public static void start(Stage mainWindow,Scene mainScene,BlockingQueue<String> fromServerMessage,Label notification) {
    	LogintoChatWin.mainWindow = mainWindow;
    	LogintoChatWin.mainScene = mainScene;
    	LogintoChatWin.fromServerMessage = fromServerMessage;
    	LogintoChatWin.notification = notification;
    }
    
    static Task<Void> listen() {
    	System.out.println(notification);
    	Task<Void> listener = new Task<Void>() {

    		@Override
    		protected Void call() throws Exception {
    			// TODO Auto-generated method stub
    			while(!called) {
    				while(!fromServerMessage.isEmpty()) {
    					System.out.println(fromServerMessage.peek());
    					if(fromServerMessage.peek().equals("Sign in successfully!")) {
    						fromServerMessage.poll();
    						Platform.runLater(()->mainWindow.setScene(mainScene));
    						return null;
    					}
    					else if(fromServerMessage.peek().equals("Incorrect username or password.")) {
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
