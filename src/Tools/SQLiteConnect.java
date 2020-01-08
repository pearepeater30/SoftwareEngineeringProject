package Tools;

import java.sql.*;

/**
 * Class for setting up connection to Database
 */
public class SQLiteConnect {
  Connection conn = null;
  public static Connection ConnectDB(){
    try{
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection("jdbc:sqlite:src/projectdb.db");
      return conn;
    }
    catch (Exception e){
      e.printStackTrace();
      return null;
    }
  }
}
