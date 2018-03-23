package wxs.org.Client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class IfPortUsed {
	public static void main(String[] args) {
		System.out.println(available(10001));
	}
	public static boolean available(int port) {
	    if (port < 0 || port > 60000) {
	        throw new IllegalArgumentException("Invalid start port: " + port);
	    }

	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	    } catch (IOException e) {
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }

	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
	            }
	        }
	    }

	    return false;
	}
}
