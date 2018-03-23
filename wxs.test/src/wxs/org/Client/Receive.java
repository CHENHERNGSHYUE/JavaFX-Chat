package wxs.org.Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Shadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wxs.org.fileTransfer.ReceiveFile;
import wxs.org.login.MainWindow;
import wxs.org.login.Site;
import wxs.org.login.testView;

import java.util.concurrent.BlockingQueue;

public class Receive implements Runnable{
	private static Socket socket;
	private static BlockingQueue<String> FromAnotherClient;
	private ArrayList<Boolean> offline; 
	private ArrayList<String> username;
	
	private HashSet<String> connectedUsers;
	private MainWindow chatController;
	private BlockingQueue<String> ToServer;
	public Receive(Socket socket,BlockingQueue<String> FromAnotherClient,
			ArrayList<String> username,HashSet<String> connectedUsers,
			ArrayList<Boolean> exited,MainWindow chatController) {
		this.socket = socket;
		this.FromAnotherClient = FromAnotherClient;
		this.username = username;
		this.offline = exited;
		this.connectedUsers = connectedUsers;
		this.chatController = chatController;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String name = read.readLine();
			System.out.println("Receive the name " + name);
			username.add(name);
			connectedUsers.add(name);
			System.out.println("Receive " + name + " start.");
			while(!offline.get(0)) {
				String line = read.readLine();
				System.out.println("Receive" + line);
				if(line == null || line.equals("@01shutdown")) {
					break;
				}
				else if (line.endsWith("@123S")) {
					System.out.println("receive the file");
					String string[] = line.split(":");
					String mess = read.readLine();
					System.out.println("receive :" + mess);
					String[] mes = mess.split(":");
					ReceiveFile.receive(Integer.parseInt(mes[mes.length - 1]),string[string.length - 1].substring(0, string[string.length - 1].length() - 5));
					continue;
				}
				if(MainWindow.toSend != null && line.startsWith(MainWindow.toSend)) {
					Platform.runLater(()->chatController.show(line,Site.LEFT));
				}
				else {
					ArrayList<HBox> queue;
					if((queue = chatController.map.get(name)) != null){
						queue.add(chatController.createHbox(line, Site.LEFT));
						chatController.map.put(name,queue);
						//System.out.println("name" + name + "queue" + queue);
					}
					else {
						queue = new ArrayList<>();
						queue.add(chatController.createHbox(line, Site.LEFT));
						chatController.map.put(name,queue);
						//System.out.println("name" + name + "queue" + queue);
					}
				};
			}
			offline.clear();
			offline.add(true);
			socket.close();
			//System.out.println("receive end");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} 
		System.out.println("Receive end.");
	}
}
