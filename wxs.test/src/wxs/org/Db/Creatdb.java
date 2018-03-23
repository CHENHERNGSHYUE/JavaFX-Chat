package wxs.org.Db;

import java.sql.*;

public class Creatdb {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost/";

   //  Database credentials
   static final String USER = "root";
   static final String PASS = "123456";
   
   public void create(String dbname) {
   //public static void main(String[] args) {
   Connection conn = null;
   Statement stmt = null;
   try{
      //Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //Open a connection
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);

      //Execute a query
      System.out.println("Creating database...");
      stmt = conn.createStatement();
      
      //create the database
      String sql = "CREATE DATABASE IF NOT EXISTS " + dbname;
      stmt.executeUpdate(sql);
      
      //connect to the new database
      conn = DriverManager.getConnection(DB_URL + dbname, USER, PASS);
      stmt = conn.createStatement();
      
      //create a table to restore usrinfo
      sql = "CREATE TABLE IF NOT EXISTS USRINFO " +
              "(usrid VARCHAR(255) not NULL, " +
              " psw VARCHAR(255), " + 
              " question VARCHAR(255), " + 
              " answer VARCHAR(255), " + 
              "ip VARCHAR(255), " + 
    		  "port VARCHAR(255), " +
              " PRIMARY KEY ( usrid ))"; 
      stmt.executeUpdate(sql);
      
      //create a table to restore offline message
      sql = "CREATE TABLE IF NOT EXISTS MESSAGE " +
    		  "(id VARCHAR(255) not NULL, " + 
    		  " sender VARCHAR(255), " +
    		  " receiver VARCHAR(255), " + 
    		  " mes VARCHAR(2000), " + 
    		  "PRIMARY KEY ( id ))";
      stmt.executeUpdate(sql);
      
      //create a table to restore online user
      sql = "CREATE TABLE IF NOT EXISTS ONLINE " +
    		  "(usrid VARCHAR(255), " +
    		  "PRIMARY KEY (usrid))";
      stmt.execute(sql);
      
      //create a table to restore offline user
      sql = "CREATE TABLE IF NOT EXISTS OFFLINE " +
    		  "(usrid VARCHAR(255), " +
    		  "PRIMARY KEY (usrid))";
      stmt.execute(sql);
      
      System.out.println("Database created successfully...");
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
}//end main
}//end JDBCExample