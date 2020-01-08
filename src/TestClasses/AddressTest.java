package TestClasses;

import Model.Address;
import Tools.SQLiteConnect;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.assertEquals;

public class AddressTest {

  /**Variable to store the userID*/
  private int userID = 0;

  /**
   * Creates a user and creates an address associated with the user
   * @throws SQLException
   */
  public void setupConnection() throws SQLException{
    /**Create Connection*/
    Connection conn = SQLiteConnect.ConnectDB();

    /**Setting up PreparedStatement to Create new User*/
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO USER " +
            "(USERNAME, PASSWORD, USERID, FIRSTNAME, LASTNAME, USERTYPE) " +
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
    System.out.println("Created User ID = "+this.userID);

    /**Setting up PreparedStatement to Create new Address*/
    PreparedStatement insertAddress = conn.prepareStatement("INSERT INTO Address" +
            "(ADDRESSSTREETNAME1, ADDRESSSTREETNAME2, ADDRESSSTREETNAME3, " +
            "ADDRESSCITY, ADDRESSPOSTCODE, ADDRESSID, ADDRESSUSERID ) VALUES " +
            "(?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
    /**Inserting values into prepared statement*/
    insertAddress.setString(1, "Manor Park");
    insertAddress.setString(2, "University of Surrey");
    insertAddress.setString(3, null);
    insertAddress.setString(4, "Guildford");
    insertAddress.setString(5, "GU2 7XH");
    insertAddress.setString(6, null);
    insertAddress.setInt(7,userID);
    /**Execute update*/
    insertAddress.executeUpdate();
    /**Close Connection*/
    conn.close();
  }

  /**
   * Method to delete all stuff generated from this test class
   * @throws SQLException
   */
  @After
  public void afterClass() throws SQLException {
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
    assertEquals("Manor Park" + "\n" + "University of Surrey" + "\n" + null + "\n" + "Guildford" + "\n" + "GU2 7XH", Address.toString(userID));
  }


}