package wxs.org.fileTransfer;

import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.xml.stream.events.StartDocument;

import javafx.application.Platform;
import wxs.org.login.MainWindow;


public class SendFile{
	//window's size
	private static int windowSize = 30;
	//the window
	private static ArrayList<DatagramPacket> windows = new ArrayList<>();
	// current sequence number
	private static int currentSequenceNumber = 0;
	// next sequence number
	private static int nextSequenceNumber = 0;
	// wrong ACK times
	private static int wrongACK = 0;
	private static FileInputStream fStream;
	static InetAddress address;
	static DatagramSocket socket;
	static boolean end = false;
	private static int port;
	
	private static int myport;
	private static MainWindow chat;
	static int speed;
	
	
	public static void setChat(MainWindow chat) {
		SendFile.chat = chat;
	}

	public static void setMyport(int myport) { 
		SendFile.myport = myport;
	}

	static int SendCount = 0;
	static long time = 0;
	
	public static void main(String[] argv) {
		//send();
	}
	
	public static void send(String ports ,String IP,String filePath) {
		try {
			port = Integer.parseInt(ports);
			socket = new DatagramSocket(myport);
			System.out.println("send file start with receiver's port =" + port + "my port is " + myport);
			File file = new File(filePath);
			address = InetAddress.getByName( IP );
			fStream = new FileInputStream(file);
			//add the first windowsize packet to windows
			initWindow();
			//send the packets in windows
			sendPacket();
			byte[] ackBytes = new byte[ 4 ];
			DatagramPacket ackPacket = new DatagramPacket( ackBytes, ackBytes.length );
			
			while(!end) {
				try {
					socket.setSoTimeout( 5000 );
					socket.receive( ackPacket );
					exmaineACK(ackBytes);
				}catch(SocketTimeoutException e){
					//time out, resend the packages
					if(windows.size() != 0) {
						sendPacket();
					}
					else {
						end = true;
					}
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
			//socket.send(packet);
			fStream.close();
			socket.close();
			System.out.println("Send file end.");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void initWindow() {
		try
		{
			for ( int i=0; i<windowSize; i++)
			{
				if ( fStream.available() > 0 )
				{
					byte[] fileBytes = new byte[ 1408 ];
					fStream.read( fileBytes );
					byte[] bytes = setSequenceNumber(fileBytes,fStream.available() > 0);
					DatagramPacket packet = 
							new DatagramPacket( bytes, bytes.length, address, port);
					windows.add(packet);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] setSequenceNumber(byte[] fileBytes,boolean flag) {
		byte[] res = new byte[1416];
		res[3] = (byte) (nextSequenceNumber & 0xFF);
		res[2] = (byte) ((nextSequenceNumber >> 8) & 0xFF);
		res[1] = (byte) ((nextSequenceNumber >> 16) & 0xFF);
		res[0] = (byte) ((nextSequenceNumber >> 24) & 0xFF);
		//System.out.println("nextSequenceNumber:" + nextSequenceNumber);
		for(int i = 4;i < 8;i ++) {
			res[i] = (byte) (flag ? 1 : 0);
		}
		nextSequenceNumber ++;
		for(int i = 0;i < fileBytes.length;i ++) {
			res[i + 8] = fileBytes[i];
		}
		return res;
	}
	
	public static void sendPacket() {
		int len = windows.size();
		for(int i = 0;i < len;i ++) {
			if(time == 0) {
				time = System.currentTimeMillis();
			}
			long now = System.currentTimeMillis();
			SendCount ++;
			if(now - time > 100) {
				time = now;
				speed = SendCount * 1416 / (1000 * 100); 
				Platform.runLater(()->chat.speed.setText(speed + "Mb/s"));
				System.out.println(speed + "Mb/s");
				SendCount = 0;
			}
			try {
				socket.send(windows.get(i));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private static void exmaineACK(byte[] ackBytes) {
		int ACK = bytesToInt(ackBytes);
		//System.out.println("Receive the ACK:" + ACK);
		if(ACK == currentSequenceNumber) {
			currentSequenceNumber ++;
			moveWindow();
		}
		else {
			//receive three replicated ack,then resend all packages
			if((wrongACK ++ ) > 3) {
				wrongACK = 0;
				sendPacket();
			}
			
		}
	}
	
	private static int bytesToInt(byte[] bytes) {
		return bytes[3] & 0xFF | (bytes[2] & 0xFF) << 8 |
				(bytes[1] & 0xFF) << 16 | (bytes[0] & 0xFF) << 24; 
	}
	
	//remove the first package, add a package and send it 
	private static void moveWindow() {
		windows.remove(0);
		try {
			if ( fStream.available() > 0 )
			{
				byte[] fileBytes = new byte[ 1408 ];
				fStream.read( fileBytes );
				byte[] bytes = setSequenceNumber(fileBytes,fStream.available() > 0);
				DatagramPacket packet = 
						new DatagramPacket( bytes, bytes.length, address, port);
				windows.add(packet);
				//System.out.println("send the package whose sequence number is " + (nextSequenceNumber - 1));
				socket.send(packet);
				SendCount ++;
				long now = System.currentTimeMillis();
				SendCount ++;
				if(now - time > 100) {
					time = now;
					speed = SendCount * 1416 / (1000 * 100); 
					Platform.runLater(()->chat.speed.setText(speed + "Mb/s"));
					System.out.println(speed + "Mb/s");
					SendCount = 0;
				}
			}
/*			else {
				end = true;
			}*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
