package wxs.org.Db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import wxs.org.encry.DesEncrypter;
import wxs.org.encry.Encryption;


public class Database {
	int count = 0;
	int online_id = 0;
	int offline_id = 0;
	int online_num = 0; 
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/chat";

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "123456";
	Connection conn ;
	Statement stmt ;
	public Database() {
		Creatdb db = new Creatdb();
		db.create("CHAT");
	}
	
	public ArrayList<ArrayList<String>> get(){
		ArrayList<ArrayList<String>> res = new ArrayList<>();
		PreparedStatement pstmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = "select * from usrinfo";
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
	    	ResultSet rs = pstmt.executeQuery();
	    	while(rs.next()) {
	    		ArrayList<String> res1 = new ArrayList<>();
	    		res1.add(rs.getString("usrid"));
	    		res1.add(rs.getString("ip"));
	    		res1.add(rs.getString("port"));
	    		res.add(res1);
	    	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	//insert an information,chose = true insert usrinfo
	public boolean insert(String[] str,boolean chose) {
	    PreparedStatement pstmt = null;
	    int res = 0; 
	    try {
	    	
	    	if(chose) {
	    		if(search(str[0]).size() != 0) {
	    			return false;
	    		}
	    		Class.forName(JDBC_DRIVER);
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
				stmt = conn.createStatement();
	    		String sql = "insert into usrinfo (usrid,psw,question,answer,ip,port) values(?,?,?,?,?,?)";
	    		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		        for(int i = 1;i < 7;i ++) {
		        	pstmt.setString(i,str[i - 1]);
		        }
		    }
	        res = pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return res == 1;
	}
	//update the information
	public void update(String[] str,boolean chose) {
		PreparedStatement pstmt = null;
		String sql = "";
	    try {
	    	Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
	    	if(chose) {
	    		sql = "update usrinfo set psw='" + str[1] + "' where usrid='" + str[0] + "'";	
		    }
	    	else {
	    		sql = "update usrinfo set ip = '" + str[2] + "' ,port='" + str[3] + "' where usrid='" + str[0] + "'";
	    	}
	    	pstmt = (PreparedStatement) conn.prepareStatement(sql);
	        pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	//update the mesaage
	public void update(String[] str) {
		PreparedStatement pstmt = null;
		String sql = "";
	    try {
			ArrayList<String> res = search(str);
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			if(res.size() == 0) {
				sql = "insert into message (id,sender,receiver,mes) values (?,?,?,?)";
				pstmt = (PreparedStatement) conn.prepareStatement(sql);
	    		pstmt.setInt(1, count);
	    		count ++;
		        for(int i = 1;i <= 3;i ++) {
		        	pstmt.setString(i+1,str[i-1]);
		        }
			}
			else {
				sql = "update message set mes='" + res.get(3) + "@@" + str[2] + "' where sender='" + str[0] + "' and receiver='" + str[1] + "'";
				System.out.println(sql);
				pstmt = (PreparedStatement) conn.prepareStatement(sql);
			}
	        pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	//search the usrinfo
	public ArrayList<String> search(String str) { 
		PreparedStatement pstmt = null;
		ArrayList<String> res = new ArrayList<String>();
	    try {
	    	Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = "select * from usrinfo where usrid='" + str + "'";
    		pstmt = (PreparedStatement) conn.prepareStatement(sql);
    		// execute select SQL stetement
    		ResultSet rs = pstmt.executeQuery();
    		if(rs.next()) {
    			res.add(rs.getString("usrid"));
    			res.add(rs.getString("psw"));
    			res.add(rs.getString("question"));
    			res.add(rs.getString("answer"));
    			res.add(rs.getString("ip"));
    			res.add(rs.getString("port"));
    		}
	        //pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return res;
	}
	
	//search the offline message which receivor is str
	public ArrayList<ArrayList<String>> getMessage(String string){
		PreparedStatement pstmt = null;
		ArrayList<ArrayList<String>> res1 = new ArrayList<ArrayList<String>>();
		ArrayList<String> res = new ArrayList<String>();
	    try {
	    	Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = "select * from message where receiver='" + string + "'";
	    	pstmt = (PreparedStatement) conn.prepareStatement(sql);
	    	ResultSet rs = pstmt.executeQuery();
	    	while(rs.next()) {
	    		res.add(rs.getString("id"));
	    		res.add(rs.getString("sender"));
	    		res.add(rs.getString("receiver"));
	    		System.out.println("database :" + DesEncrypter.dencrybyDEC(rs.getString("mes")));
	    		res.add(rs.getString("mes"));
	    		res1.add(res);
	    		res = new ArrayList<>();
	    	}
	    }catch(Exception e) {
	        e.printStackTrace();
	    }	
	    return res1;
	}
	
	//search the offline message
	public ArrayList<String> search(String[] str) {
		PreparedStatement pstmt = null;
		ArrayList<ArrayList<String>> res1 = new ArrayList<ArrayList<String>>();
		ArrayList<String> res = new ArrayList<String>();
	    try {
	    	Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			if(str.length != 1) {
				String sql = "select * from message where sender ='" + str[0] + "' and receiver='" + str[1] + "'";
		    	pstmt = (PreparedStatement) conn.prepareStatement(sql);
		    	ResultSet rs = pstmt.executeQuery();
		    	if(rs.next()) {
		    		res.add(rs.getString("id"));
		    		res.add(rs.getString("sender"));
		    		res.add(rs.getString("receiver"));
		    		res.add(rs.getString("mes"));
		    	}
			}
			else {
				String sql = "select * from message where receiver='" + str[0] + "'";
		    	pstmt = (PreparedStatement) conn.prepareStatement(sql);
		    	ResultSet rs = pstmt.executeQuery();
		    	if(rs.next()) {
		    		res.add(rs.getString("id"));
		    		res.add(rs.getString("sender"));
		    		res.add(rs.getString("receiver"));
		    		res.add(rs.getString("mes"));
		    		res1.add(res);
		    	}
			}
	        //pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return res;
	}
	//delete an information
	public void delete(String str,boolean chose) {
	    String sql = "";
	    PreparedStatement pstmt = null;
	    try {
	    	Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
	    	if(chose) {
	    		sql = "delete from usrinfo where usrid='" + str + "' ";
	    	}
	    	else {
	    		sql = "delete from message where usrid='" + str + "' ";
	    		//count --;
	    	}
	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
	        pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	//update the table of online and inline 
	public void exchange(String user,boolean online) {
		String sql = "";
	    PreparedStatement pstmt = null;
	    try {
	    	Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
	    	if(online) {
	    		online_num ++;
	    		sql = "delete from offline where usrid='" + user + "' ";
	    		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		        pstmt.executeUpdate();
		        sql = "insert into online (usrid) values(?)";
		   	}
	    	else {
	    		online_num --;
	    		sql = "delete from online where usrid='" + user + "' ";
	    		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		        pstmt.executeUpdate();
		        sql = "insert into offline (usrid) values(?)";
	    	}
	    	pstmt = (PreparedStatement) conn.prepareStatement(sql);
    		pstmt.setString(1,user);
    		pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	//insert the offline when sign up
	public void insert_offline(String user) {
		String sql = "";
	    PreparedStatement pstmt = null;
	    try {
	    	Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
	    	online_num ++;
	        sql = "insert into offline (usrid) values(?)";
	    	pstmt = (PreparedStatement) conn.prepareStatement(sql);
    		pstmt.setString(1,user);
    		pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	//get the online and offline users
	public ArrayList<String> get(String name){
		PreparedStatement pstmt = null;
		ArrayList<String> res = new ArrayList<String>();
	    try {
	    	Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = "select * from " + name;
	    	pstmt = (PreparedStatement) conn.prepareStatement(sql);
	    	ResultSet rs = pstmt.executeQuery();
	    	//System.out.println("rs" + rs.first());
	    	while(rs.next()) {
	    		res.add(rs.getString("usrid"));
	    		//res = new ArrayList<String>();
	    	}
	        pstmt.close();
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return res;
	}
	
	public void deleteMessage(String string) {
		String sql = "";
	    PreparedStatement pstmt = null;
	    try {
	    	Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
	    	online_num ++;
	        sql = "delete from message where receiver='" + string + "'";
	    	pstmt = (PreparedStatement) conn.prepareStatement(sql);
    		pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static void main(String[] args) {
		Database db = new Database();
		try {
			String[] strings={DesEncrypter.encrybyDEC("aaa"),
					DesEncrypter.encrybyDEC("bbb"),
					DesEncrypter.encrybyDEC("ccc")};
			db.update(strings);
			db.update(strings);
			ArrayList<ArrayList<String>> res = db.getMessage(DesEncrypter.encrybyDEC("bbb"));
			String string[] = res.get(0).get(3).split("@@");
			for(String string2 : string)
				System.out.println(DesEncrypter.dencrybyDEC(string2));
		} catch (Exception e){
			// TODO Auto-generated catch block
		e.printStackTrace();}
		
	}
}
