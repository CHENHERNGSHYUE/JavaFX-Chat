package wxs.org.Client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.SSLContext;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.BlockingQueue;

import wxs.org.fileTransfer.ReceiveFile;
import wxs.org.fileTransfer.SendFile;
import wxs.org.login.ChangeWindow;
import wxs.org.login.LoginWindow;
import wxs.org.login.MainWindow;
import wxs.org.login.Tasks;

public class ConnectionToServer implements Runnable{
	private BlockingQueue<String> ToServer;
    private BlockingQueue<String> FromServer;
    private volatile BlockingQueue<Tasks> tasks;
    private BlockingQueue<String> ToAnotherClient;
    private BlockingQueue<String> FromAnotherClient;
    
    private int port;
    private volatile static ArrayList<Boolean> exited;
    
    private ConcurrentLinkedQueue<String> onlineUserInfo;
    private ConcurrentLinkedQueue<String> offlineUserInfo;
    
    private Client client;
    
    private HashSet<String> connectedUsers;
    
    private ServerSocket server;
    
    private ChangeWindow change;
    
    private MainWindow chatController;
    
    private BlockingQueue<String> update;
    
    private String filePath;
    
	public ConnectionToServer(BlockingQueue<String> ToServer,
    		BlockingQueue<String> FromServer,
    		BlockingQueue<Tasks> tasks,int port,
    		ConcurrentLinkedQueue<String> onlineUserInfo,
    		ConcurrentLinkedQueue<String> offlineUserInfo,
    		Client client,HashSet<String> connectedUsers,
    		BlockingQueue<String> ToAnotherClient,
    		BlockingQueue<String> FromAnotherClient,ArrayList<Boolean> exited,
    		ServerSocket server,ChangeWindow change,
    		MainWindow chatController,BlockingQueue<String> update) {
    	this.ToServer = ToServer;
    	this.FromServer = FromServer;
    	this.tasks = tasks;
    	this.port = port;
    	this.onlineUserInfo = onlineUserInfo;
    	this.offlineUserInfo = offlineUserInfo;
    	this.client = client;
    	this.connectedUsers = connectedUsers;
    	this.ToAnotherClient = ToAnotherClient;
    	this.FromAnotherClient = FromAnotherClient;
    	this.exited = exited;
    	this.server = server;
    	this.change = change; 
    	this.chatController = chatController;
    	this.update = update;
    }
    
	private static Socket socket;
	private  PrintStream output;
	
	//initial the socket and start a receiving thread
	public void run() {
		try {
			socket = new Socket("10.14.126.69",20000);
			output = new PrintStream(socket.getOutputStream());
			/*ReceiveFile.setIP(socket.getRemoteSocketAddress().toString().split(":")[0].substring(1));*/
			ReceiveFile.setIP("10.14.126.69");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//get the message from server or another client
			BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			while(!exited.get(0)) {
				String message;
				try {
					message = ToServer.take();
					if(message.equals("insert")) {
						/*send("12" + socket.getRemoteSocketAddress().toString().split(":")[0].substring(1));*/
						send("12" + "10.14.126.69");
						message = "13" + port;
						send(message);
					}
					else if(message.startsWith("33")){
						send(message);
						String IP = reader.readLine();
						//if this client is online
						if(!IP.equals("11offline")) {
							String sport = reader.readLine();
							System.out.println("Connection t server:IP " + IP + "PORT:" + sport);
							client.startConClient(IP, Integer.parseInt(sport),message.substring(2).split(":")[0]);
							ToAnotherClient.add(message.substring(2));
							if(message.endsWith("@123S")) {//开始传输文件
								String[] strings = message.substring(2).split(":");
								System.out.println(strings[0] + ":" + strings[1] + ":" + port);
								ToAnotherClient.add(strings[0] + ":" + strings[1] + ":" + port);
								filePath = ToServer.take();
								System.out.println(filePath);
								SendFile.send(sport,IP,filePath);
							}
						}
						
					}
					else {
						//System.out.println("normal message" + message);
						send(message);
					}
					if((message.startsWith("03") || message.startsWith("13") ||
							message.startsWith("23")) && (line = reader.readLine()) != null) {
						//print the message
						System.out.println(line + " " + Tasks.get(line));
						Tasks task = Tasks.get(line);
						//FromServer.add(line);
						//get the online user and offline user
						if(message.startsWith("13")) { 
							if(task == Tasks.sign_in) {
								boolean flag = true;
								while(flag) {
									line = reader.readLine();
									if(line.equals("end")) {
										flag = false;
									}
									else {
										if(line.startsWith("00")) {
											chatController.addOffline(line.substring(2));
										}
										else {
											chatController.addOnline(line.substring(2));
										}
									}
								}
								Platform.runLater(() -> chatController.setUserInfo());
								//receive the offline message
								while(true) {
									line = reader.readLine();
									System.out.println("connection to server of receiving offline message" + line);
									FromAnotherClient.add(line);
									if(line.equals("12end")) {
										break;
									}
									else {
										chatController.add(line);
									}
								}
							}
						}
						tasks.put(task);
						System.out.println("put task" + tasks);
					}
					else if (message.startsWith("60")) {
						line = reader.readLine();
						if(line.equals("Not find the user!")) {
							FromServer.add("@12un_show");
						}
						else {
							FromServer.add("@12" + line.substring(3));
						}
					}
					else if (message.startsWith("70")) {
						boolean flag = true,f = false;
						while(flag) {
							line = reader.readLine();
							if(!f) {
								tasks.put(Tasks.refresh);
								f = true;
							}
							if(line.equals("end")) {
								flag = false;
							}
							else {
								if(line.startsWith("00")) {
									chatController.addOffline(line.substring(2));
								}
								else {
									chatController.addOnline(line.substring(2));
								}
							}
						}
						System.out.println("offline = " +  offlineUserInfo + " online= " + onlineUserInfo);
						//System.out.println("offline" + chatController.offlineuserListView.getItems());
						//update.add("update");
						//System.out.println("update of server" + update);
						Platform.runLater(() -> chatController.setUserInfo());
						
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace(); 
				}
			}
			send("43exit");
			tasks.put(Tasks.ok);
			ToAnotherClient.add("@01exit");
			System.out.println("connection to server end");
			server.close();
			update.add("end");
		}catch(IOException ex){
			//print the io exception
			System.out.println(ex);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//send a line of message to server or another client
	public void send(String text) {
        try {
        	output.write((text + "\r\n").getBytes());
        	//send the message at once rather than put them in the buffer
        	output.flush();
        }catch(IOException ex) {
        	System.out.println(ex);
        }
    }
}
