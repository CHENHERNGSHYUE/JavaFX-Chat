package wxs.org.Server;

import java.awt.Button;
import java.awt.List;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;

import javax.jws.soap.SOAPBinding.Use;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class serverController implements Initializable {
	@FXML
	private ListView<String> onlineUserId;
	
	@FXML
	private ListView<String> onlineIp;
	
	@FXML
	private ListView<String> onlinePort;
	
	@FXML 
	private ListView<String> offline;
	
	private BlockingQueue<String> tasks;
	
	
	public void setInfo(BlockingQueue<String> tasks) {
		this.tasks = tasks;
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		/*onlineUserId.getItems().add("aaa");
		onlineIp.getItems().add("aaa");
		onlinePort.getItems().add("aaa");
		offline.getItems().add("bb");*/
		
	}
	public void update(MouseEvent mouseEvent) {
		onlineUserId.getItems().clear();
		onlineIp.getItems().clear();
		onlinePort.getItems().clear();
		offline.getItems().clear();
		
		tasks.add("update");
	}
	
	public void show(ArrayList<ArrayList<String>> onlineUser,ArrayList<String> offlineUser) {
		//System.out.println(onlineUserId + " " + onlineIp+ " " + onlinePort + " ");
		for(ArrayList<String> user : onlineUser) {
			onlineUserId.getItems().add(user.get(0));
			onlineIp.getItems().add(user.get(1));
			onlinePort.getItems().add(user.get(2));
		}
		for(String user:offlineUser) {
			offline.getItems().add(user);
		}
	}
	
}
