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

public class AdministratorAccountMenuController {
  /**Current Scene Panel used for changing scene*/
  @FXML
  private GridPane administratorMenuPanel;
  /**Title stored as a variable*/
  @FXML
  private Text mainTitle;

  //public no-args constructor
  public AdministratorAccountMenuController(){
    super();
  }

  /**
   * Functions that are executed the moment this scene is loaded
   */
  @FXML
  private void initialize()
  {
    this.mainTitle.setText("Welcome " + CurrentLogin.getInstance().getFirstName() + " " + CurrentLogin.getInstance().getLastName());
  }

  /**
   * logout method used to remove currently logged in user from USERSESSION Table in DB and Singleton CurrentLogin
   * @param event
   * @throws IOException
   */
  @FXML
  private void logout(ActionEvent event) throws IOException {
    Connection conn;
    try{
      /**Initialize Connection*/
      conn = SQLiteConnect.ConnectDB();
      /**Get First name, Last name, User ID and User type from CurrentLogin singleton*/
      String firstNameUserSession = CurrentLogin.getInstance().getFirstName();
      String lastNameUserSession = CurrentLogin.getInstance().getLastName();
      int userIDUserSession = CurrentLogin.getInstance().getUserID();
      String userTypeUserSession = CurrentLogin.getInstance().getUserType();
      /**Delete row from USERSESSION with values retrieved from the CurrentLogin singleton with Prepared Statement*/
      PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM USERSESSION " +
              "WHERE FIRSTNAME=? and LASTNAME=? and USERID=? and USERTYPE=?");
      /**setting the values in the preparedStatement with values from singleton*/
      stmt2.setString(1, firstNameUserSession);
      stmt2.setString(2,lastNameUserSession);
      stmt2.setInt(3,userIDUserSession);
      stmt2.setString(4,userTypeUserSession);
      /**Execute Query*/
      stmt2.executeUpdate();
      /**Set CurrentLogin values as null*/
      CurrentLogin.getInstance().setFirstName(null);
      CurrentLogin.getInstance().setLastName(null);
      CurrentLogin.getInstance().setUserID(0);
      CurrentLogin.getInstance().setUserType(null);
      /**close connection*/
      conn.close();
      /**sets screen to Login screen*/
      GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/Login.fxml"));
      this.administratorMenuPanel.getChildren().setAll(pane);
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }

  /**
   * sets scene as AddRemoveSeller scene
   * @param event
   * @throws IOException
   */
  @FXML
  private void enterAddRemoveSeller(ActionEvent event)throws IOException{
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/AddRemoveSeller.fxml"));
    this.administratorMenuPanel.getChildren().setAll(pane);
  }

  /**
   * sets scene as ChangePasswordMenu
   * @param event
   * @throws IOException
   */
  @FXML
  private void enterPasswordChange(ActionEvent event)throws IOException{
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/ChangePasswordMenu.fxml"));
    this.administratorMenuPanel.getChildren().setAll(pane);
  }
}
