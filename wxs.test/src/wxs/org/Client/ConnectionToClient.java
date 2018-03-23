package wxs.org.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

import wxs.org.login.MainWindow;

//
public class ConnectionToClient{
	private BlockingQueue<String> ToAnotherClient;
    private BlockingQueue<String> FromAnotherClient;
    private String IP;
    private int port;
    private ArrayList<String> username = new ArrayList<String>();
    private ArrayList<String> myname;
    
    private static Socket socket;
	private  OutputStream output;
	private BufferedReader read;
	
	private HashSet<String> connectedUsers;
	private MainWindow ChatController;
    
	private ArrayList<Boolean> exited; 
    public ConnectionToClient(BlockingQueue<String> ToAnotherClient,
    		BlockingQueue<String> FromAnotherClient,
    		String IP,int port,ArrayList<String> myname,
    		HashSet<String> connectedUsers,
    		ArrayList<Boolean> exited,MainWindow ChatController) {
    	this.ToAnotherClient = ToAnotherClient;
    	this.FromAnotherClient = FromAnotherClient;
    	this.IP = IP;
    	this.port = port;
    	this.myname = myname;
    	this.connectedUsers = connectedUsers;
    	this.exited = exited;
    	this.ChatController = ChatController;
    	try {
    		ConnectionToClient.socket = new Socket(IP,port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public ConnectionToClient(Socket socket) {
    	this.socket = socket;
    }
    public void start() {
    	ArrayList<Boolean> list = new ArrayList<>();
    	list.add(false);
    	System.out.println("Connection to client this.username" + username);
    	new Thread(new Send(socket, ToAnotherClient,username,myname,connectedUsers,list)).start();
    	System.out.println("new connection" + socket);
    	new Thread(new Receive(socket, FromAnotherClient,username,connectedUsers,list,ChatController)).start();
    }
}
