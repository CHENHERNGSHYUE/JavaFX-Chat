package wxs.org.login;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
//import yuema.local.UserInfo;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by martin on 17-10-11.
 *
 */
public class MainWindow implements Initializable {
    // 创建两个收发消息的Queue 来处理所有消�?


    @FXML
    private HBox onlineUsersHbox;
    //界面根容�?
    @FXML private BorderPane borderPane;
    //用户头像
    @FXML private ImageView userImageView;
    //用户�?
    @FXML private Label usernameLabel;
    //在线用户列表
    @FXML  
    public ListView<String> onlineuserListView;
    
    @FXML
	public ListView<String> offlineuserListView;
    
    @FXML
    public Label speed;
    //在线用户人数
    @FXML private Label onlineuserCountLabel;
    
    @FXML private Label offlineuserCountLabel;
    //消息显示列表
    @FXML private ListView<HBox> chatPaneListView;
    /*private ListView<String> chatPaneListView;*/
    //消息发�?�框
    @FXML private TextArea messageBoxTextArea;
    //对话消息按钮
    @FXML private Button sendButton;
    //聊天对象显示文本�?
    @FXML
    private Label otherUserNameLabel;
    
    private static ObservableList<HBox> list;
    private static ObservableList<String> onlineUser;
    ArrayList<String> userInfoList = new ArrayList<>();
    
    private ConcurrentLinkedQueue<String> onlineUserInfo = new ConcurrentLinkedQueue<String>();
    private ConcurrentLinkedQueue<String> offlineUserInfo = new ConcurrentLinkedQueue<String>();
    
    public void addOnline(String user) {
    	onlineUserInfo.add(user);
    }
    public void addOffline(String user) {
    	offlineUserInfo.add(user);
    }
    
    public void setName(String name) {
		this.name = name;
	}
	public static String toSend;
    public ListView<String> getOnlineuserListView() {
		return onlineuserListView;
	}
	private static BlockingQueue<String> ToAnotherClient;
    private static BlockingQueue<String> ToServerMessage;
    private boolean flag = true;
    String name = null;
    
    //store the chatting record
    
    //store the message when switch to another client
    public HashMap<String,ArrayList<HBox>> map = new HashMap<>();
    //store the name of client that has sent message to this client and it hasn't been seen
    public HashSet<String> newMessages = new HashSet<>();
    
    public void setInfo(BlockingQueue<String> ToServerMessage,
    		BlockingQueue<String> ToAnotherClient) {
    	this.ToServerMessage = ToServerMessage;
    	this.ToAnotherClient = ToAnotherClient;
    }
    
    public void add(String message) {
    	String from = message.split(":")[0];
    	ArrayList<HBox> mess;
    	HBox box = createHbox(message,Site.LEFT);
    	if(map.containsKey(from)) {
    		mess = map.get(from);
    		mess.add(box);
    	}
    	else {
			mess = new ArrayList<>();
			mess.add(box);
		}
    	map.put(from, mess);
    }
    
    public void getMessageFromScene() {
    	ArrayList<HBox> mylist = new ArrayList<>();
    	ObservableList<HBox> view = chatPaneListView.getItems();
    	while(view.size() != 0) {
    		mylist.add(view.get(0));
    		view.remove(0);
    	}
    	map.put(toSend,mylist);
    	list = chatPaneListView.getItems();
    }
    //mark the user who send this client new message but not readed
    public void mark(String name) {
    	if(!newMessages.contains(name)) {
    		newMessages.add(name);
    		onlineUser.set(onlineUser.indexOf(name),name + " ...");
    	}
    }
    //After reading the new message, sweep away the sign
    public void sweep(String name) {
    	if(newMessages.contains(name)) {
    		newMessages.remove(name);
    		onlineUser.set(onlineUser.indexOf(name),name);
    	}
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	setUserInfo();
    	list = chatPaneListView.getItems();
    	onlineUser = onlineuserListView.getItems();
    }
    //print the online user and offline user
    public void setUserInfo() {
    	onlineuserCountLabel.setText(onlineUserInfo.size() - 1 +"");
    	System.out.println("set user info");
    	System.out.println("mainwindow online" + onlineUserInfo);
    	ObservableList<String> online = FXCollections.observableArrayList();
         while(!onlineUserInfo.isEmpty()) {
        	 String user = onlineUserInfo.poll();
        	 if(!user.equals(name)) {
        		 online.add(user);
        	 }
         }
         onlineuserListView.setItems(online);
         System.out.println("mainwindow offline" + offlineUserInfo);
         ObservableList<String> offline = FXCollections.observableArrayList();
         offlineuserCountLabel.setText(offlineUserInfo.size() + "");
         while(!offlineUserInfo.isEmpty()) {
        	 System.out.println("offline add :" + offlineUserInfo.peek());
        	 offline.add(offlineUserInfo.poll());
         }
         offlineuserListView.setItems(offline);
         System.out.println("offline + " + offlineuserListView.getItems());
         usernameLabel.setText(name);
    }
    public void show(String message,Site site) {
    	chatPaneListView.getItems().add(createHbox(message,site));
    }

    public HBox createHbox(String message,Site site) {
    	HBox hbox = new HBox(1);
    	hbox.getChildren().addAll(new Text(message));
    	if(Site.isRight(site)) {
    		hbox.setAlignment(Pos.BASELINE_RIGHT);
    	}
    	else if (Site.isCenter(site)) {
    		hbox.setAlignment(Pos.BASELINE_CENTER);
		}
    	else {
			hbox.setAlignment(Pos.BASELINE_LEFT);
		}
    	return hbox;
    }
    
    public void offline(MouseEvent mouseEvent) {
    	String usr = offlineuserListView.getSelectionModel().getSelectedItem();
    	getMessageFromScene();
    	toSend = usr;
    	ArrayList<HBox> view = map.get(usr);
    	if(view != null && view.size() != 0) {
    		chatPaneListView.getItems().addAll(view);
    	}
    	sweep(toSend);
    }
    
    public void online(MouseEvent mouseEvent) {
    	String usr = onlineuserListView.getSelectionModel().getSelectedItem();
    	getMessageFromScene();
    	toSend = usr;
    	ArrayList<HBox> view = map.get(usr);
    	if(view != null && view.size() != 0) {
    		chatPaneListView.getItems().addAll(view);
    	}
    	sweep(toSend);
    }
    public void setUserList(ArrayList<String> userInfolist) {
    	
	}

    public void closeImgViewPressedAction(MouseEvent mouseEvent) {

    }

    public void refreshImgViewPressedAction(MouseEvent mouseEvent) {
    	onlineuserListView.getItems().clear();
    	offlineuserListView.getItems().clear();
    	offlineuserListView.getItems().add("aaa");
    	/*remove(0,offlineuserListView.getItems().size()-1);*/
    	ToServerMessage.add("70"+name);
    	//listen();  	
    }

    public void emojiSelectorBtnAction(ActionEvent actionEvent) {
    }

    public void sendBtnAction(ActionEvent actionEvent) {
    	
    }

    public void sendMethod(KeyEvent keyEvent) {
    	
    }
    public void send(MouseEvent mouseEvent) {
        String message = messageBoxTextArea.getText();
        ToServerMessage.add("33" + toSend + ":" + name + ":" + message);
        //clear the message
        messageBoxTextArea.clear();
        show(name + ":" + message,Site.RIGHT);
    }
    public void transfer(MouseEvent mouseEvent) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open Resource File");
    	fileChooser.getExtensionFilters().addAll(
                 new FileChooser.ExtensionFilter("ALL", "*.*")  
             );
    	File file = fileChooser.showOpenDialog(new Stage());
    	String path = file.toString();
    	System.out.println(path+name);
    	String filename = file.getName();
    	ToServerMessage.add("33" + toSend + ":" + name + ":" + filename + "@123S");
    	ToServerMessage.add(path);
    }
}
