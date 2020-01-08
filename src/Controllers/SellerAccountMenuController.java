package Controllers;

import Model.CurrentLogin;
import Tools.SQLiteConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SellerAccountMenuController {
  /**Pane for the Scene*/
  @FXML
  private GridPane sellerMenuPanel;
  /**Title stored as a variable*/
  @FXML
  private Text mainTitle;

  /**
   * Functions that are executed upon initialization of scene
   */
  @FXML
  private void initialize()
  {
    mainTitle.setText("Welcome " + CurrentLogin.getInstance().getFirstName() + " " + CurrentLogin.getInstance().getLastName());
  }

  public SellerAccountMenuController(){
  }

  /**
   * Method used to logout user
   * @param event
   * @throws IOException
   */
  @FXML
  private void logout(ActionEvent event) throws IOException {
    Connection conn;
    try{
      /**Setting up Connection to Database*/
      conn = SQLiteConnect.ConnectDB();
      /**get firstname, lastname, userID and userType from CurrentLogin singleton*/
      String firstNameUserSession = CurrentLogin.getInstance().getFirstName();
      String lastNameUserSession = CurrentLogin.getInstance().getLastName();
      int userIDUserSession = CurrentLogin.getInstance().getUserID();
      String userTypeUserSession = CurrentLogin.getInstance().getUserType();
      /**Setting PreparedStatement to currently logged in user from USERSESSION table*/
      PreparedStatement logout = conn.prepareStatement("DELETE FROM USERSESSION " +
              "WHERE FIRSTNAME=? and LASTNAME=? and USERID=? and USERTYPE=?");
      /**Inserting value into the query to delete from USERSESSION*/
      logout.setString(1, firstNameUserSession);
      logout.setString(2,lastNameUserSession);
      logout.setInt(3,userIDUserSession);
      logout.setString(4,userTypeUserSession);
      /**Execute update*/
      logout.executeUpdate();
      /**Reset all values of CurrentLogin singleton*/
      CurrentLogin.getInstance().setFirstName(null);
      CurrentLogin.getInstance().setLastName(null);
      CurrentLogin.getInstance().setUserID(0);
      CurrentLogin.getInstance().setUserType(null);
      /**Close Connection*/
      conn.close();
      /**Take user back to Login Scene*/
      GridPane pane = FXMLLoader.load(getClass().getResource("/FXML/Login.fxml"));
      this.sellerMenuPanel.getChildren().setAll(pane);
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Enter AddRemoveProduct Scene
   * @throws IOException
   */
  @FXML
  private void enterAddRemoveProduct() throws IOException{
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/AddProduct.fxml"));
    this.sellerMenuPanel.getChildren().setAll(pane);
  }

  /**
   * Enter ChangePassword Scene
   * @throws IOException
   */
  @FXML
  private void enterChangePassword() throws IOException{
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/ChangePasswordMenu.fxml"));
    this.sellerMenuPanel.getChildren().setAll(pane);
  }

  /**
   * Enter RemoveProduct Scene
   * @throws IOException
   */
  @FXML
  private void enterRemoveProduct() throws IOException{
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/RemoveProductMenu.fxml"));
    this.sellerMenuPanel.getChildren().setAll(pane);
  }

  /**
   * Enter ViewProduct Scene
   * @throws IOException
   */
  @FXML
  private void enterViewProduct() throws IOException{
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/ViewProductMenu.fxml"));
    this.sellerMenuPanel.getChildren().setAll(pane);
  }

}
