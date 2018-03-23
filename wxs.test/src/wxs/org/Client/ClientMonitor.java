package wxs.org.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

import wxs.org.Server.serverThread;
import wxs.org.login.MainWindow;
import wxs.org.login.Tasks;
//monitor if there is a connection request
public class ClientMonitor implements Runnable{
	private static BlockingQueue<String> ToAnotherClient;
	private static BlockingQueue<String> FromAnotherClient;
	private ArrayList<String> username = new ArrayList<String>();
	private ArrayList<String> myname;
	private ArrayList<Boolean> exited;
	private HashSet<String> connectedUsers;
	private ServerSocket server;
	private MainWindow ChatController;
	
	public ClientMonitor(BlockingQueue<String> ToAnotherClient,
			BlockingQueue<String> FromAnotherClient,
			ArrayList<String> myname,HashSet<String> connectedUsers,
			ArrayList<Boolean> exited,ServerSocket server,
			MainWindow ChatController) {
		ClientMonitor.ToAnotherClient = ToAnotherClient;
		ClientMonitor.FromAnotherClient = FromAnotherClient;
		this.myname = myname;
		this.connectedUsers = connectedUsers;
		this.exited = exited;
		this.server = server;
		this.ChatController = ChatController;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			System.out.println("client monitor start");
			System.out.println("monitor" + exited);
			Socket client = null; 
			while(!exited.get(0)) {
				//wait a new connection 
	            client = server.accept();   
	            System.out.println("Connect to server successfully!");
	            //start thread for sending and receiveing the message
	            ArrayList<Boolean> list = new ArrayList<>();
	            list.add(false);
	            new Thread(new Send(client, ToAnotherClient,username,myname,connectedUsers,list)).start();
	        	new Thread(new Receive(client, FromAnotherClient,username,connectedUsers,list,ChatController)).start();
			}
			System.out.println("client monitor end");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}  
        
	}

}
