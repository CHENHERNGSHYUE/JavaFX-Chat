package wxs.org.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.BlockingQueue;

import wxs.org.login.ChangeWindow;
import wxs.org.login.LoginWindow;
import wxs.org.login.MainWindow;
import wxs.org.login.Tasks;

public class Client {
	//the message sending to server	
	private BlockingQueue<String> ToServer;
	//the message received from server
    private BlockingQueue<String> FromServer;
    //the task that need to be done
    private BlockingQueue<Tasks> tasks;
    //the ip of the other client that this one is going to connect  
    private String IP;
    private int port;
    //if the connection is still alive 
    private volatile static ArrayList<Boolean> exited = new ArrayList<>();
    //the message to another client
    private BlockingQueue<String> ToAnotherClient;
    //the messafe from another client 
	private BlockingQueue<String> FromAnotherClient;
	//the online user 
	private ConcurrentLinkedQueue<String> onlineUserInfo;
	//the offline user
    private ConcurrentLinkedQueue<String> offlineUserInfo;
    //store the users that have been connect to this client
    private ArrayList<String> users = new ArrayList<String>();
    //this client name
    private ArrayList<String> myname;
    
    private LoginWindow loginController;
    private MainWindow chatController;
    Client client;
    private HashSet<String> connectedUsers;
    private ChangeWindow change;
    
    private BlockingQueue<String> update;
	
	public void setChange(ChangeWindow change) {
		this.change = change;
	}


	public void setClient(Client client) {
		this.client = client;
	}
	
	
	public Client(BlockingQueue<String> ToServer,
			BlockingQueue<String> FromServer,
			BlockingQueue<Tasks> tasks,String IP,int port,
			BlockingQueue<String> ToAnotherClient,
			BlockingQueue<String> FromAnotherClient,
			ConcurrentLinkedQueue<String> onlineUserInfo,
			ConcurrentLinkedQueue<String> offlineUserInfo,
			ArrayList<String> myname,
			HashSet<String> connectedUsers,LoginWindow loginController,
			MainWindow chatController,BlockingQueue<String> update) {
		this.ToServer = ToServer;
		this.FromServer = FromServer;
		this.tasks = tasks;
		this.IP = IP;
		this.port = port;
		this.ToAnotherClient = ToAnotherClient;
		this.FromAnotherClient = FromAnotherClient;
		this.onlineUserInfo = onlineUserInfo;
		this.offlineUserInfo = offlineUserInfo;
		this.myname = myname;
		this.connectedUsers = connectedUsers;
		exited.add(false);
		this.loginController = loginController;
		this.chatController = chatController;
		this.update = update;
	}
	
	public void startConServer() {
		ServerSocket server;
		try {
			server = new ServerSocket(port);
			System.out.println("connecting to server.");
			new Thread(new ConnectionToServer(ToServer, FromServer, tasks, port, 
					onlineUserInfo, offlineUserInfo,client,connectedUsers,
					ToAnotherClient,FromAnotherClient,exited,server,
					change,chatController,update)).start();
			System.out.println("running the monitor.");
			new Thread(new ClientMonitor(ToAnotherClient,FromAnotherClient,
					myname,connectedUsers,exited,server,chatController)).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startConClient(String IP,int port,String name) {
		System.out.println("Clien:" + name + "\n" + connectedUsers);
		if(!(users.contains(IP) || connectedUsers.contains(name))) {
			System.out.println("conneting to another client" + IP);
			new ConnectionToClient(ToAnotherClient,FromAnotherClient,IP,port,myname,connectedUsers,exited,chatController).start();
			users.add(IP); 
		}
		else {
			System.out.println("Having connected to this client.");
		}
	}

	public void setExited() {
		// TODO Auto-generated method stub
		exited.clear();
		exited.add(true);
	}

}
