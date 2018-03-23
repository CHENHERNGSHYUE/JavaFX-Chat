package wxs.org.login;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import wxs.org.Client.Client;
import wxs.org.Client.IfPortUsed;
import wxs.org.Client.RandomInteger;
import wxs.org.fileTransfer.ReceiveFile;
import wxs.org.fileTransfer.SendFile;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by martin on 17-10-11.
 * 1. 重拍消息的顺序是不可能的�? ?
 * 2. 服务器只有被动的建立连接, 但是客户端含有主动的连接和被动的连接
 */
public class Main extends Application { 
    private static Client client;
    private BlockingQueue<String> FromServerMessage  = new ArrayBlockingQueue<String>(10);
    private static BlockingQueue<String> ToServerMessage = new ArrayBlockingQueue<String>(10);
    private boolean connected = false;
    private volatile BlockingQueue<Tasks> tasks = new ArrayBlockingQueue<Tasks>(5);
    private String IP;
    private int port = 10001; 
    private BlockingQueue<String> ToAnotherClient = new ArrayBlockingQueue<String>(10);
    private BlockingQueue<String> FromAnotherClient = new ArrayBlockingQueue<String>(10); 
    private BlockingQueue<String> update = new ArrayBlockingQueue<String>(10);
    private ArrayList<String> username = new ArrayList<>();
    
    private ConcurrentLinkedQueue<String> onlineUserInfo = new ConcurrentLinkedQueue<String>();
    private ConcurrentLinkedQueue<String> offlineUserInfo = new ConcurrentLinkedQueue<String>();
    
    private HashSet<String> connectedUsers = new HashSet<>();
    
    @Override
    public void start(Stage window) throws Exception {
        // 启动的时候首先进入登录的界面
    	while(!IfPortUsed.available(port)) {
    		port = RandomInteger.randInt(10000,60000);
    	} 
    	System.out.println("port" + port);
    	SendFile.setMyport(port);
    	ReceiveFile.setPort(port);
    	FXMLLoader login = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = login.load();
        Scene loginScene = new Scene(root);
        LoginWindow loginController = login.getController();
        window.resizableProperty().setValue(Boolean.FALSE);
        window.setScene(loginScene);
        window.show();

        
        FXMLLoader signup = new FXMLLoader(getClass().getResource("SignupWindow.fxml"));
        root = signup.load();
        Scene signupScene = new Scene(root);
        SignUpWindow signupController = signup.getController();
        
        FXMLLoader modify = new FXMLLoader(getClass().getResource("ModifyPwdWindow.fxml"));
        root = modify.load();
        Scene modifyScene = new Scene(root); 
        ModifyWindow modifyController = modify.getController();
        
        FXMLLoader chat = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        root = chat.load();
        MainWindow chatController = chat.getController();
        Scene chatScene = new Scene(root);
        System.out.println("ChatController" + chatController);
        System.out.println((MainWindow)chat.getController());
        ReceiveFile.setChat(chatController);
        SendFile.setChat(chatController);
        
        client = new Client(ToServerMessage, FromServerMessage,tasks,IP,port,
        		ToAnotherClient,FromAnotherClient,onlineUserInfo,offlineUserInfo,
        		username,connectedUsers,loginController,chatController,update);
        client.setClient(client);
        chatController.setInfo(ToServerMessage, ToAnotherClient);
        
        loginController.start(window, chatScene,
                ToServerMessage,
                client,connected,username,chatController); 
        
        signupController.start(window, loginScene,
                ToServerMessage,client,connected);
        
        modifyController.start(window, loginScene,ToServerMessage,FromServerMessage,client, connected);
        
        ChangeWindow change = new ChangeWindow(window, loginScene, 
        		signupScene, modifyScene, chatScene,loginController,
        		modifyController,signupController,chatController,
        		 tasks,FromServerMessage);
        change.listen();
        client.setChange(change);
        
        //LoginWindow.listen1();
        //ChangeWindow.listen();
    }

    @Override
    public void stop(){
        client.setExited();
        FromServerMessage.add("ok");
        tasks.add(Tasks.ok);
        ToServerMessage.add("ok");
    }

    public static void main(String[] args) {
        launch(args);
    }


}


