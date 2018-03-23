package wxs.org.Server;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import wxs.org.encry.DesEncrypter;

import org.omg.CORBA.FREE_MEM;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author hubachelar
 *
 * 尽可能的利用已有界面:
 *  1. loginWindow 很有三�?�场�?
 *      1. 登录界面, 提交和反馈信�?        signup login
 *      2. 验证密保                     确定 取消
 *      3. 设置密保阶段 Security Question : 确定, 取消
 *
 *  2. 创建线程仅仅在此类中间创�?, �?以创建之后的
 *
 *  3. 服务器的消息如何传�?�出�?
 */
public class LoginWindow implements Initializable {

    /**
     * @author martin
     * 首先搞清楚的问题就是, 对于收到的消息客户机的处理是是什�?:
     *  1. 收到普�?�的消息,添加到listview 中间
     *  2. 收到服务器的消息, 修改界面
     *  3. 由于使用了双向的管道, �?有的消息都是究竟处理
     *  4. 为什么界面需要开出新的线程进行消息的处理: 因为只要出现线程的阻�?,那么就是�?要使用新的线�?
     *  5. 各自持有自己的线�?, 关闭该线程的事情使用线程编号 �? 消息处理解决,而不是使用传递参数的方法, 也就�?
     *  主线程可以不持有�?有的线程的资�?
     */

    @FXML
    private Label theme;

    @FXML
    private Label notification;

    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXButton signUpButton;

    @FXML
    private JFXTextField userID;

    @FXML
    private JFXButton loginButton;

    private Stage mainWindow;
    private Scene mainScene;

    public void set(Stage mainWindow,Scene mainScene) {
    	this.mainWindow = mainWindow;
    	this.mainScene = mainScene;
    }
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }


    public void login(MouseEvent mouseEvent) {
        
        try {
        	String passwords = password.getText();
            String userId = userID.getText();

            if(!checkKV(userId, passwords)) return;
			if(DesEncrypter.encrybyDEC(userId).equals("lZ1IUnbUVug=") &&
					DesEncrypter.encrybyDEC(passwords).equals("G+hJV4mmVqxdWSOFShHh3w==")) {
				Platform.runLater(()->mainWindow.setScene(mainScene));
			}
			else {
				notification.setText("Incorrect password or userid");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void signUp(MouseEvent mouseEvent) {
        String passwords = password.getText();
        String userId = userID.getText();

    }

    private boolean checkKV(String userId, String passwords){
        if (!userId.matches("[a-zA-Z0-9]*")) {
            notification.setText("Illegal character in password");
            return false;
        }
        if(userId.length() > 20){
            notification.setText("username is too long !");
            return false;
        }else if(userId.length() < 3){
            notification.setText("username is too short");
            return false;
        }

        if(passwords.length() > 50){
            notification.setText("Password is too long !");
            return false;
        }else if(passwords.length() < 3){
            notification.setText("Password is too short !");
            return false;
        }
        return true;
    }

}


