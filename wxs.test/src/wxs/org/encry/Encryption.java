package wxs.org.encry;

import java.security.MessageDigest;

public class Encryption { 
	public static void main(String[] args) {
		try {
			System.out.println(encrybyMD5("hello"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String encrybyMD5(String plainText) throws Exception {    
	    MessageDigest md5 = MessageDigest.getInstance("md5");  
	    byte[] cipherData = md5.digest(plainText.getBytes());  
	    StringBuilder builder = new StringBuilder();  
	    for(byte cipher : cipherData) {  
	        String toHexStr = Integer.toHexString(cipher & 0xff);  
	        builder.append(toHexStr.length() == 1 ? "0" + toHexStr : toHexStr);  
	    }  
	    return builder.toString();  
	}
//	public static String encrybyDES(String plainText) {
//		
//	}
}
