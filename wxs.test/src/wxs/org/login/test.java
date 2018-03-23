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
import wxs.org.Client.ConnectionToServer;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.*;

public class test extends Application{
	public static void main(String[] args) throws Exception {
		launch();
    }

	@Override
	public void start(Stage window) throws Exception {
		// TODO Auto-generated method stub
		FXMLLoader modify = new FXMLLoader(getClass().getResource("ModifyPWDWindow.fxml"));
        Parent root = modify.load();
        Scene modifyScene = new Scene(root);
        ModifyWindow modifyController = modify.getController();
        window.resizableProperty().setValue(Boolean.FALSE);
        window.setScene(modifyScene);
        window.show();
	}
}
