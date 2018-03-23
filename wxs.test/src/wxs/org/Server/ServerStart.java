package wxs.org.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerStart {
	public static void main(String argv[]){  
        //�������20006�˿ڼ����ͻ��������TCP����  
        ServerSocket server;
		try {
			server = new ServerSocket(20000);
			Socket client = null;  
	        boolean f = true;  
	        while(f){  
	            //wait a new connection 
	            client = server.accept();  
	            System.out.println("Connect to server successfully!");  
	            //start a new thread for a new client
	            new Thread(new serverThread(client)).start();  
	        }  
	        server.close();  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
    }
}
