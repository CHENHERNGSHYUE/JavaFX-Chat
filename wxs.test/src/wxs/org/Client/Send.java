package wxs.org.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

public class Send implements Runnable{
	private static Socket socket;
	private static BlockingQueue<String> toAnotherClient;
	private ArrayList<Boolean> offline; 
	private ArrayList<String> username;
	private ArrayList<String> myname;
	private HashSet<String> connectedUsers;
	public Send(Socket socket,BlockingQueue<String> toAnotherClient,
			ArrayList<String> username,ArrayList<String> myname,
			HashSet<String> connectedUsers,ArrayList<Boolean> exited) {
		this.socket = socket;
		this.toAnotherClient = toAnotherClient;
		this.username = username;
		this.myname = myname; 
		this.connectedUsers = connectedUsers;
		this.offline = exited;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			//System.out.println("Send" + socket);
			PrintStream out = new PrintStream(socket.getOutputStream());
			System.out.println("send myname " + (myname.size() == 0 ? "no" : myname.get(0)));
			out.println(myname.size() == 0 ? null : myname.get(0));
			System.out.println("send start.");
			while(!offline.get(0)) {
				String name = username.size() == 0 ? null : username.get(0);
				String line = toAnotherClient.take();
				if(line.equals("@01exit")) {
					toAnotherClient.add(line);
					out.println("@01Shutdown");
					break;
				}
				String toSend = line.split(":")[1];
				//System.out.println("Send" + line);
				if(line.startsWith(name + ":")) {
					//System.out.println("Send" + line);
					out.println(line.substring(name.length() + 1));
					System.out.println("Send" + line + "myname is " + name);
				}
				else {
					toAnotherClient.add(line);
				}
				
			}
			System.out.println("send end");
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
