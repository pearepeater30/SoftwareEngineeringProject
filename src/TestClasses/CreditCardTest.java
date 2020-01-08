package TestClasses;

import Model.CreditCard;
import Tools.EncryptionModule;
import Tools.SQLiteConnect;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class CreditCardTest {

  /**Variable to store the userID*/
  private int userID = 0;

  /**
   * Creates a user and creates a Credit Card associated with the user
   * @throws SQLException
   */
  public void setupConnection() throws SQLException {
    /**Create Connection*/
    Connection conn = SQLiteConnect.ConnectDB();

    /**Setting up PreparedStatement to Create new User*/
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO USER (USERNAME, PASSWORD, USERID, FIRSTNAME, LASTNAME, USERTYPE) " +
            "VALUES (?,?,?,?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
    /**Inserting values into prepared statement*/
    stmt.setString(1,"testuser");
    stmt.setString(2,"password");
    stmt.setString(3,null);
    stmt.setString(4,"testfirstname");
    stmt.setString(5,"testlastname");
    stmt.setString(6,"testusertype");
    /**Execute update*/
    stmt.executeUpdate();

    /**Get userID from the insert statement we just performed*/
    ResultSet userIDResultSet = stmt.getGeneratedKeys();
    /**Store keys generated*/
    this.userID = userIDResultSet.getInt(1);
    /**Display the userID*/
    System.out.println("Created User ID = "+ this.userID);

    /**Setting up PreparedStatement to Create new Credit Card*/
    PreparedStatement insertCreditCard = conn.prepareStatement("INSERT INTO CreditCard" +
            "(CCENCRYPTEDCARDNO, CCEXPMONTH, CCEXPYEAR, CCENCRYPTEDSECURITYCODE, CCPROVIDER, CCID, CCUSERID) " +
            "VALUES (?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
    /**Inserting values into prepared statement*/
    insertCreditCard.setString(1, EncryptionModule.Encrypt("123456789"));
    insertCreditCard.setString(2, "August");
    insertCreditCard.setString(3, "2023");
    insertCreditCard.setString(4, EncryptionModule.Encrypt("123"));
    insertCreditCard.setString(5, "Visa");
    insertCreditCard.setString(6, null);
    insertCreditCard.setInt(7,userID);
    /**Execute update*/
    insertCreditCard.executeUpdate();
    /**Close Connection*/
    conn.close();
  }

  /**
   * Method to delete all stuff generated from this test class
   * @throws SQLException
   */
  @After
  public void tearDown() throws Exception {
    Connection conn = SQLiteConnect.ConnectDB();
    PreparedStatement deleteAddress = conn.prepareStatement("DELETE FROM Address WHERE ADDRESSUSERID = ?");
    PreparedStatement deleteUser = conn.prepareStatement("DELETE FROM USER WHERE USERID = ?");
    deleteUser.setInt(1, userID);
    deleteAddress.setInt(1, userID);
    deleteAddress.executeUpdate();
    deleteUser.executeUpdate();
    System.out.println("AfterClass executed");
    conn.close();
  }

  /**
   * Test class to test the toString method of the Address class
   * @throws SQLException
   */
  @Test
  public void testtoString()throws SQLException {
    setupConnection();
    Assert.assertEquals("Credit Card Number: " + 123456789 + "\n" + "Credit Card Expiration Date: "
            + "August" + "/" + "2023" + "\n" + "Credit Card Security Code: "
            + 123 + "\n" + "Credit Card Provider: " + "Visa", CreditCard.toString(userID));
  }
}