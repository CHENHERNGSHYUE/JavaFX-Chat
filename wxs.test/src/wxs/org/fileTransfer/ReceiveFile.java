package wxs.org.fileTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javafx.application.Platform;
import wxs.org.login.MainWindow;


public class ReceiveFile {
	//write stream
	private static FileOutputStream fStream;
	//the next sequence number
	private static int nextSequenceNumber = 0;
	//the sender's address
	private static InetAddress address;
	//the sender's port
	private static int myport;
	static float start=0;
	static float end=0;
	private static String IP;
	private static MainWindow chat;
	static int speed;
	private static int count = 0;
	
	public static void setChat(MainWindow chat) {
		ReceiveFile.chat = chat;
	}
	public static void setIP(String iP) {
		IP = iP;
	}
	private static int SendCount = 0;
	static long time = 0;
	
	
	public static void setPort(int port) {
		ReceiveFile.myport = port;
	}
	static float timeTaken, timeTakenDecompress;
	public static void main(String[] args) {
		//receive();
	}
	public static void receive(int port,String filename) {
		try {
			DatagramSocket socket = new DatagramSocket(myport);
			System.out.println("receive file start with port = " + myport + "IP = " + IP + "send port = " + port);
			File file = new File("F:\\junior\\network\\" + filename);
			file.createNewFile();
			PrintWriter writer = new PrintWriter("F:\\junior\\network\\res.csv","UTF-8");
			fStream = new FileOutputStream(file);
			address = InetAddress.getByName( "192.168.217.131" );
			byte[] recData = new byte[ 1416 ];
			byte[] ACK = new byte[4];
			start=System.currentTimeMillis();
			
			while(true) {
				if(time == 0) {
					time = System.currentTimeMillis();
				}
				long now = System.currentTimeMillis();
				/*System.out.println(now);
				System.out.println(SendCount);*/
				SendCount ++;
				if(now - time > 100) {
					count ++;
					time = now;
					speed = SendCount * 1416 / (1000 * 100); 
					Platform.runLater(()->chat.speed.setText(speed + "Mb/s"));
					writer.println(speed);
					System.out.println(speed + "Mb/s");
					SendCount = 0;
				}
				DatagramPacket recPacket = new DatagramPacket( recData, recData.length );
				socket.setSoTimeout( 5000 );
				socket.receive( recPacket );
				System.out.println("Receive the packet " + recPacket);
				int recACK = getSequenceNumber(recData);
				ACK = getACK(recACK);
				DatagramPacket ACKPacket = new DatagramPacket(ACK,ACK.length,address,port);
				socket.send(ACKPacket);
				if( recACK == nextSequenceNumber) {
					nextSequenceNumber ++;
					//judge if the transfer is over
					fStream.write(getData(recPacket.getData()));
					if(!isend(recData)) {
						break;
					}
				}
			}
			end = System.currentTimeMillis();
			//System.out.println("end = " + end + " start = " + start + (long)364630 / (end - start) + "Mb/s");
			Platform.runLater(()->chat.speed.setText(364630 / (count * 100) + "Mb/s"));
			writer.println(364630 / (count * 100)); 
			fStream.close();
			socket.close();
			writer.close();
			System.out.println("Receive file end.");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static int getSequenceNumber(byte[] bytes) {
		return bytes[3] & 0xFF | (bytes[2] & 0xFF) << 8 |
				(bytes[1] & 0xFF) << 16 | (bytes[0] & 0xFF) << 24; 
	}
	public static boolean isend(byte[] bytes) {
		return bytes[4] == 1 && bytes[5] == 1 && bytes[6] == 1 && bytes[7] == 1;
	}
	public static byte[] getData(byte[] bytes) {
		byte[] res = new byte[1408];
		for(int i = 8;i < bytes.length;i ++) {
			res[i - 8] = bytes[i];
		}
		return res;
	}
	public static byte[] getACK(int nextSequenceNumber) {
		byte[] ACK = new byte[4];
		ACK[0] = (byte) ((nextSequenceNumber >> 24 ) & 0xFF);
		ACK[1] = (byte) ((nextSequenceNumber >> 16 ) & 0xFF);
		ACK[2] = (byte) ((nextSequenceNumber >> 8 ) & 0xFF);
		ACK[3] = (byte) ((nextSequenceNumber) & 0xFF);
		return ACK;
	}
}
