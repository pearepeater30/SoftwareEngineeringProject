package Model;

import Tools.EncryptionModule;
import Tools.SQLiteConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreditCard {
  /**private static variables storing creditCardNumber, creditCardExpMonth, creditCardExpYear, creditCardSecurityCode
   * and creditCardProvider of a Address object*/
  private static String creditCardNumber;
  private static String creditCardExpMonth;
  private static String creditCardExpYear;
  private static String creditCardSecurityCode;
  private static String creditCardProvider;



  /**
   * @param userID
   * @return
   * @throws SQLException
   * toString method used to display address of the current logged in user
   */
  public static String toString(Integer userID)throws SQLException {
    /**Setting up Connection to Database*/
    Connection conn = SQLiteConnect.ConnectDB();
    /**Setting up preparedStatement to select CreditCard where the userID belongs to the currently logged in user's userID*/
    PreparedStatement insertAddress = conn.prepareStatement("SELECT " +
            "CCENCRYPTEDCARDNO,CCEXPMONTH,CCEXPYEAR,CCENCRYPTEDSECURITYCODE,CCPROVIDER FROM CreditCard WHERE CCUSERID = ?");
    /**Insert Currently logged in user taken as a parameter*/
    insertAddress.setInt(1, userID);
    /**Store Results from query as ResultSet*/
    ResultSet rs = insertAddress.executeQuery();
    /**Checking if there are results in the database*/
    if (rs.isBeforeFirst()) {
      /**Creating a new CreditCard object with rows from the ResultSet*/
      creditCardNumber = rs.getString("CCENCRYPTEDCARDNO");
      creditCardExpMonth = rs.getString("CCEXPMONTH");
      creditCardExpYear = rs.getString("CCEXPYEAR");
      creditCardSecurityCode = rs.getString("CCENCRYPTEDSECURITYCODE");
      creditCardProvider = rs.getString("CCPROVIDER");
      conn.close();
      return ("Credit Card Number: " + EncryptionModule.Decrypt(creditCardNumber) + "\n" + "Credit Card Expiration Date: "
              + creditCardExpMonth + "/" + creditCardExpYear + "\n" + "Credit Card Security Code: "
              + EncryptionModule.Decrypt(creditCardSecurityCode) + "\n" + "Credit Card Provider: " + creditCardProvider);
    }
    else {
      conn.close();
      /**Return with no Credit Card found*/
      return ("No CreditCard Found");
    }
  }
}
