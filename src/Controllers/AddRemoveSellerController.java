package Controllers;

import Tools.SQLiteConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddRemoveSellerController {
  /**Pane for the Scene*/
  @FXML
  private GridPane addRemoveSellerPanel;
  /**Text for displaying errors*/
  @FXML
  private Text errorMsg;
  /**Text field for entering the username of the seller that the Admin desires to add/remove*/
  @FXML
  private TextField usernameField;

  //public no-args Consturctor
  public AddRemoveSellerController(){
    super();
  }


  /**
   * Functions that are executed upon initialization of scene
   */
  @FXML
  private void initialize() {
  }

  /**
   * Returns to previous scene
   */
  @FXML
  private void backAction(ActionEvent event)throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/AdministratorAccountMenu.fxml"));
    this.addRemoveSellerPanel.getChildren().setAll(pane);
  }

  /**
   * method for adding seller to list of approved sellers
   * @param event
   * @throws SQLException
   */
  @FXML
  private void addSeller(ActionEvent event) throws SQLException{
    /**checks if the username field is empty, if so, then display error message*/
    if(this.usernameField.getText().isEmpty()){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("Please enter username!");
      this.errorMsg.setOpacity(1.0);
    }
    else{
      /**Setup Connection*/
      Connection conn = SQLiteConnect.ConnectDB();
      /**Setting up PreparedStatement to see if the user of the username entered exists*/
      PreparedStatement existingUsers = conn.prepareStatement("SELECT USERID, USERTYPE " +
              "FROM User " +
              "WHERE USERNAME = ?");
      /**Inserting values into preparedStatement*/
      existingUsers.setString(1,this.usernameField.getText());
      /**Storing results from query into ResultSet*/
      ResultSet rs = existingUsers.executeQuery();
      /**If there are rows in ResultSet, more queries are able to be made*/
      if(rs.next()){
        /**Check if the User is an active seller by comparing UserIDs*/
        int existingActiveSeller = 0;
        /**Retrieving UserID Column from previous PreparedStatement*/
        int newactiveSellerUserID = rs.getInt("USERID");
        /**Retrieving the UserType from previous PreparedStatement*/
        String newactiveSellerUserType = rs.getString("USERTYPE");
        /**Setting up PreparedStatement to see if the Seller being approved has already been approved*/
        PreparedStatement existingActiveSellers = conn.prepareStatement("SELECT ACTIVESELLERSUSERID " +
                "FROM ActiveSellers" +
                " WHERE ACTIVESELLERSUSERID = ?");
        /**Inserting values into above preparedStatement with the UserID of the user that is to be added*/
        existingActiveSellers.setInt(1,newactiveSellerUserID);
        /**Stored Result from query in ResultSet*/
        ResultSet activeSellers = existingActiveSellers.executeQuery();
        /**If statement used in case there are no results retrieved from the database to prevent error thrown*/
        if(activeSellers.next()){
          existingActiveSeller = activeSellers.getInt("ACTIVESELLERSUSERID");
        }
        /**If the usertype of the account is not a Seller, an erro message is displayed*/
        if(newactiveSellerUserType.equals("Administrator") || newactiveSellerUserType.equals("Buyer")){
          this.errorMsg.setFill(Color.RED);
          this.errorMsg.setText("Buyers and Admins cannot be added!");
          this.errorMsg.setOpacity(1.0);
        }
        /**
         * If an existing UserID in the database matches the UserID of the Account that desires to be added,
         * an error message is thrown
         */
        else if(existingActiveSeller == newactiveSellerUserID){
          this.errorMsg.setFill(Color.RED);
          this.errorMsg.setText("User already added to Active Sellers");
          this.errorMsg.setOpacity(1.0);
        }
        /**If all criteria are met, insert the Seller that is desired to be approved into ActiveSellers*/
        else{
          PreparedStatement insertActiveSeller = conn.prepareStatement("INSERT INTO ActiveSellers VALUES (?)");
          insertActiveSeller.setInt(1,newactiveSellerUserID);
          insertActiveSeller.executeUpdate();
          this.errorMsg.setFill(Color.GREEN);
          this.errorMsg.setText("User added to active sellers!");
          this.errorMsg.setOpacity(1.0);
        }
      }
      else{
        this.errorMsg.setFill(Color.RED);
        this.errorMsg.setText("User not found, please re-enter valid username!");
        this.errorMsg.setOpacity(1.0);
      }
      /**Close Connection*/
      conn.close();
    }
  }

  /**
   * Method used to remove Seller from Active Seller list
   * @param event
   * @throws SQLException
   */
  @FXML
  private void removeSeller(ActionEvent event) throws SQLException{
    /**Checks if usernamefield is empty, if so, then display error message*/
    if(this.usernameField.getText().isEmpty()){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("Please enter username!");
      this.errorMsg.setOpacity(1.0);
    }
    /**Setup Connection to Database*/
    Connection conn = SQLiteConnect.ConnectDB();
    /**Setting up PreparedStatement to see if the user of the username exists*/
    PreparedStatement existingUsers = conn.prepareStatement("SELECT USERID FROM User WHERE USERNAME = ?");
    /**Inserting values into the PreparedStatement*/
    existingUsers.setString(1,usernameField.getText());
    /**Store query in ResultSet*/
    ResultSet rs = existingUsers.executeQuery();
    /**If there are rows in ResultSet, more queries are able to be made*/
    if(rs.next()) {
      /**Store UserID from previous query*/
      int activeSellerUserID = rs.getInt("USERID");
      /**Setting up PreparedStatement to see if UserID exists in ActiveSellers*/
      PreparedStatement findInActiveSellers = conn.prepareStatement("SELECT * FROM ActiveSellers WHERE ACTIVESELLERSUSERID = ?");
      /**Inserting values into PreparedStatement*/
      findInActiveSellers.setInt(1,activeSellerUserID);
      /**Storing results from query into ResultSet*/
      ResultSet foundInActiveSellers = findInActiveSellers.executeQuery();
      /**If no results found, then display error message*/
      if(!foundInActiveSellers.isBeforeFirst()){
        this.errorMsg.setFill(Color.RED);
        this.errorMsg.setText("User not in Active Sellers!");
        this.errorMsg.setOpacity(1.0);
      }
      /**If all criteria met, then delete user from ActiveSellers*/
      else {
        /**Find all products that belong to the user to be deleted*/
        PreparedStatement selectProductsTobeDeleted = conn.prepareStatement("SELECT PRODUCTID FROM Product " +
                "WHERE PRODUCTUSERID = ?");
        /**Insert values into above query*/
        selectProductsTobeDeleted.setInt(1,activeSellerUserID);
        /**Preparing Statements to delete Seller from ActiveSellers*/
        PreparedStatement deleteSeller = conn.prepareStatement("DELETE FROM ActiveSellers " +
                "WHERE ACTIVESELLERSUSERID = ?");
        /**Preparing Statements to delete Products sold by ActiveSeller*/
        PreparedStatement deleteProducts = conn.prepareStatement("DELETE FROM Product WHERE PRODUCTUSERID = ?");
        /**Storing all ProductIDs of products to be deleted in ResultSet*/
        ResultSet retrievedProductKeys = selectProductsTobeDeleted.executeQuery();
        /**For products to be deleted, also remove them from Customer's carts*/
        if (retrievedProductKeys.next()){
          PreparedStatement deleteProductsFromCarts = conn.prepareStatement("DELETE FROM Cart " +
                  "WHERE CARTPRODUCTPRODUCTID = ?");
          deleteProductsFromCarts.setInt(1, retrievedProductKeys.getInt("PRODUCTID"));
          deleteProductsFromCarts.executeUpdate();
        }
        /**Insert value into statement to delete Seller*/
        deleteSeller.setInt(1, activeSellerUserID);
        /**Insert value into statement to delete Products owned by Seller to be deleted*/
        deleteProducts.setInt(1,activeSellerUserID);
        /**Executing query to delete Seller*/
        deleteSeller.executeUpdate();
        /**Executing query to delete Products owned by Seller to be deleted*/
        deleteProducts.executeUpdate();
        this.errorMsg.setFill(Color.GREEN);
        this.errorMsg.setText("User removed from active sellers!");
        this.errorMsg.setOpacity(1.0);

      }
    }
    else{
      /**If not users found, an error message is displayed*/
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("User not found, please re-enter valid username!");
      this.errorMsg.setOpacity(1.0);
    }
    /**Close Connection*/
    conn.close();
  }

}
