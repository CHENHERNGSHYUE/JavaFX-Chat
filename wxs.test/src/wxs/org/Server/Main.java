package wxs.org.Server;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import wxs.org.Db.Database;
import wxs.org.encry.DesEncrypter;
import wxs.org.login.Tasks;

public class Main extends Application{
	private BlockingQueue<String> tasks = new ArrayBlockingQueue<String>(10);
	Database db = new Database();
	serverController server;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args); 
	}

	@Override
	public void start(Stage window) throws Exception {
		// TODO Auto-generated method stub
		FXMLLoader mainWindow = new FXMLLoader(getClass().getResource("serverWindow.fxml"));
		Parent root = mainWindow.load();
		server = mainWindow.getController();
        Scene mainScene = new Scene(root);
        window.resizableProperty().setValue(Boolean.FALSE);
        FXMLLoader loginWindow = new FXMLLoader(getClass().getResource("loginWindow.fxml"));
		root = loginWindow.load();
		LoginWindow login = loginWindow.getController();
		login.set(window,mainScene);
        Scene loginScene = new Scene(root);
        window.setScene(loginScene);
        window.show();
        server.setInfo(tasks);
        setMessage();
        listen();
	}
	
	Task<Void> listen() {
    	//System.out.println(notification);
    	Task<Void> listener = new Task<Void>() {

    		@Override
    		protected Void call() throws Exception {
    			// TODO Auto-generated method stub
    			while(!isCancelled()) {
    	        	String task = tasks.take();
    	        	if(task.equals("update")) {
    	        		setMessage();
    	        	}
    	        	else {
    	        		break;
    	        	}
    	        }
    			return null;
    		}
       	};
    	new Thread(listener).start();
    	return listener;
    }
	public void stop(){
        tasks.add("exit");
    }
	private void setMessage() {
		ArrayList<ArrayList<String>> userInfo = db.get();
        ArrayList<String> online = db.get("online");
        ArrayList<String> offline = db.get("offline");
        System.out.println(offline);
        ArrayList<ArrayList<String>> onlineuser = new ArrayList<>();
        try {
        	 for(ArrayList<String> user : userInfo) {
             	System.out.println("test");
             	if(online.contains(user.get(0))) {
             		ArrayList<String> u = new ArrayList<>();
             		for(int i = 0;i < 3;i ++)
             			u.add(DesEncrypter.dencrybyDEC(user.get(i)));
             		onlineuser.add(u);
             	}
             }
             ArrayList<String> offlineuser = new ArrayList<>();
             for(String Offline : offline) {
             	System.out.println(Offline);
             	offlineuser.add(DesEncrypter.dencrybyDEC(Offline));
             }
             Platform.runLater(()->server.show(onlineuser, offlineuser));
        }catch(Exception e) {
        	e.printStackTrace();
        }
       
	}
}
