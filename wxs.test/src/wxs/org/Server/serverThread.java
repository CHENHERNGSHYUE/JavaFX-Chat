package wxs.org.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import wxs.org.Db.Database;
import wxs.org.encry.DesEncrypter;
import wxs.org.encry.Encryption;

public class serverThread implements Runnable{
	
	private static Socket client = null;
	private String user;
	private Database db;
	public static void main(String[] argv) throws Exception {
		ServerSocket server = new ServerSocket(20006);  
        Socket cl = server.accept(); 
        client = cl;
	}
    public serverThread(Socket client){  
        this.client = client;  
    }  
      
    public void run() {  
        try{    
            PrintStream out = new PrintStream(client.getOutputStream());   
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));  
            boolean flag =true; 
            db = new Database();
            String[] login = new String[6];
            String[] signin = new String[4];
            String[] modify = new String[4]; 
            String[] port = new String[3];
            while(flag){  
                //get the message from client
                String str =  buf.readLine();  
                
                //Some operations that server must do
                if(str != null && str.length() >= 2) {
                	int num = Character.getNumericValue((str.charAt(0)));
                    int index = Character.getNumericValue((str.charAt(1)));
                    System.out.println("num = " + num + "index = " + index + " " + str);
                    switch (num) { 
    				//login message
                    case 0:
                    	login[index] = DesEncrypter.encrybyDEC(str.substring(2));
                    	if(index == 3) {
                    		if(db.insert(login, true)) {
                    			System.out.println("sign up successfully!");
                    			db.insert_offline(login[0]);
                    			out.println("sign up successfully!");
                    		}
                    		else {
                    			System.out.println("sign up unsuccessfully!");
                    			out.println("sign up unsuccessfully!");
                    		}
                    	}
    					break;
    				//signin message
                    case 1:
                    	signin[index] = DesEncrypter.encrybyDEC(str.substring(2));
                    	if(index == 3) {
                    		user = signin[0];
                    		signin[index] = DesEncrypter.encrybyDEC(str.substring(2));
                    		//System.out.println(signin[1]); 
                    		ArrayList<String> res = db.search(signin[0]);
                    		//System.out.println(res.size() == 0 || signin[1] res.get(1));
                    		if(res.size() == 0 || !signin[1].equals(res.get(1))) {
                    			out.println("sign in unsuccessfully!");
                    			System.out.println("Incorrect username or password.");
                    		}
                    		else {
                    			db.update(signin, false);
                    			db.exchange(signin[0], true);
                    			out.println("sign in successfully!");
                    			System.out.println("Sign in successfully!");	
                    			//send the offline and online users 
                    			ArrayList<String> offline = db.get("offline");
                    			//System.out.println(offline);
                    			for(String s : offline) {
                    				String string = DesEncrypter.dencrybyDEC(s);
                    				//System.out.println(string);
                    				out.println("00" + string);
                    			}
                    			ArrayList<String> online = db.get("online");
                    			for(String s : online) {
                    				String string = DesEncrypter.dencrybyDEC(s);
                    				//System.out.println(string);
                    				out.println("01" + string);
                    			}
                    			out.println("end");
                    			ArrayList<ArrayList<String>> mess = db.getMessage(user);
                    			db.deleteMessage(user);
                    			System.out.println("offline messgae" + mess.size());
                    			while(mess.size()!= 0) {
                    				ArrayList<String> list = mess.remove(0);
                    				String toSend = DesEncrypter.dencrybyDEC(list.get(1));
                    				System.out.println(DesEncrypter.dencrybyDEC(list.get(3)));
                    				String[] message = list.get(3).split("@@");
                    				System.out.println("server thread test offline message:" + message.length);
                    				for(String mes : message) {
                    					System.out.println("server thread test offline message:" + DesEncrypter.dencrybyDEC(mes));
                    					out.println(toSend + ":" + DesEncrypter.dencrybyDEC(mes));
                    				}
                    			}
                    			out.println("12end");
                    		}
                    	}
                    	break;
                    //modify password message
                    case 2:
                    	modify[index] = DesEncrypter.encrybyDEC(str.substring(2));
                    	if(index == 3) {
                    		ArrayList<String> reStrings = db.search(modify[0]);
                    		String[] res = {modify[0],modify[3]};
                    		//judge if the usrname exist
                    		if (reStrings.size() != 0) {
                    			//judge if the answer is right
                    			System.out.println(DesEncrypter.dencrybyDEC(reStrings.get(3)));
                    			if(reStrings.get(3).equals(modify[2])) {
                    				db.update(res, true);
                    				System.out.println("Change the password successfully!");
                    				out.println("Change the password successfully!");
                    			}
                    			else {
                    				System.out.println("Incorrect answer!");
                    				out.println("Incorrect answer!");
                    			}
                    		}
                    		else {
                    			out.println("Incorrect username!");
                    		}
                    	}
                    	break;
                    case 3:
                    	String[] strings = str.substring(2).split(":");
                    	String string = DesEncrypter.encrybyDEC(strings[0]);
                    	if(db.get("online").contains(string)) {
                    		ArrayList<String> reStrings = db.search(string);
                    		out.println(DesEncrypter.dencrybyDEC(reStrings.get(4)));
                    		out.println(DesEncrypter.dencrybyDEC(reStrings.get(5)));
                    	}
                    	else {
                    		String[] mes = {DesEncrypter.encrybyDEC(strings[1]),string,
                        			DesEncrypter.encrybyDEC(str.substring(4+strings[0].length()+strings[1].length()))}; 
                        	db.update(mes);
                        	out.println("11offline");
                    	}
                    	break;
                    //close the scoket
                    case 4:
                    	System.out.println(user + "offline");
                    	//user = DesEncrypter.encrybyDEC(str.substring(2));
                    	db.exchange(user, false);
                    	//client.close();
                    	flag = false;
                    	break;
                    //store the offline message
                    case 5:
                    	String[] s = str.substring(2).split(":");
                    	String[] mes = {DesEncrypter.encrybyDEC(s[0]),DesEncrypter.encrybyDEC(s[1]),
                    			DesEncrypter.encrybyDEC(str.substring(4+s[0].length()+s[1].length()))}; 
                    	db.update(mes);
                    	break;
                    case 6:
                    	String userId = DesEncrypter.encrybyDEC(str.substring(2));
                    	System.out.println("userid = " + userId);
                    	ArrayList<String> reStrings = db.search(userId);
                		if(reStrings.size() == 0) {
                			System.out.println("Not find the user!");
                			out.print("Not find the user!");
                		}
                		else {
                			System.out.println("@12" + DesEncrypter.dencrybyDEC(reStrings.get(2)));
                			out.println("@12" + DesEncrypter.dencrybyDEC(reStrings.get(2)));
                		}
                		break;
                    case 7:
                    	ArrayList<String> offline = db.get("offline");
            			//System.out.println(offline);
            			for(String s1 : offline) {
            				String string1 = DesEncrypter.dencrybyDEC(s1);
            				System.out.println(string1);
            				out.println("00" + string1);
            			}
            			ArrayList<String> online = db.get("online");
            			for(String s1 : online) {
            				String string1 = DesEncrypter.dencrybyDEC(s1);
            				System.out.println(string1);
            				out.println("01" + string1);
            			}
            			out.println("end");
                    	break;
    				default:
    					break;
    				}  

                }
            }  
            out.close();  
            //client.close();  
        }catch(Exception e){  
        	db.exchange(user, false);
            e.printStackTrace();  
        }  
    }  
}
