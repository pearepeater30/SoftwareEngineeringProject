package Model;

import Tools.SQLiteConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Address {
  /**private static variables storing streetnames, city and postcode of a Address object*/
  private static String addressStreetname1;
  private static String addressStreetname2;
  private static String addressStreetname3;
  private static String addressCity;
  private static String addressPostcode;

  /**
   * toString method used to display address of the current logged in user
   * @param userID
   * @return
   * @throws SQLException
   */
  public static String toString(Integer userID)throws SQLException {
    /**Setting up Connection to Database*/
    Connection conn = SQLiteConnect.ConnectDB();
    /**Setting up preparedStatement to select Address where the userID belongs to the currently logged in user's userID*/
    PreparedStatement insertAddress = conn.prepareStatement("SELECT ADDRESSSTREETNAME1, " +
            "ADDRESSSTREETNAME2, ADDRESSSTREETNAME3, ADDRESSCITY, ADDRESSPOSTCODE FROM Address WHERE ADDRESSUSERID = ?");
    /**Insert Currently logged in user taken as a parameter*/
    insertAddress.setInt(1,userID);
    /**Store Results from query as ResultSet*/
    ResultSet rs = insertAddress.executeQuery();
    /**Creating a new address object with rows from the ResultSet*/
    if (rs.isBeforeFirst()){
      addressStreetname1 = rs.getString("ADDRESSSTREETNAME1");
      addressStreetname2 = rs.getString("ADDRESSSTREETNAME2");
      addressStreetname3 = rs.getString("ADDRESSSTREETNAME3");
      addressCity = rs.getString("ADDRESSCITY");
      addressPostcode = rs.getString("ADDRESSPOSTCODE");
      /**Close Connection*/
      conn.close();
      return (addressStreetname1 + "\n" + addressStreetname2 + "\n" + addressStreetname3 + "\n" + addressCity + "\n" + addressPostcode);
    }
    /**If no address found*/
    else{
      conn.close();
      /**Return No address found*/
      return("No Address found");
    }
  }
}
