package Controllers;

import Model.CurrentLogin;
import Tools.EncryptionModule;
import Tools.SQLiteConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
  /**Pane for current Scene*/
  @FXML
  private GridPane loginPanel;
  @FXML
  /** Fields for username(email) and password*/
  private TextField emailfield;
  @FXML
  private PasswordField passwordfield;
  /**Text used to display messages*/
  @FXML
  private Text errorMsg;

  // Add a public no-args constructor
  public LoginController() {
    super();
  }

  /**
   * all methods that needs to be executed during initialization is listed in this method
   */
  @FXML
  private void initialize() {
  }

  /**
   * Method for transitioning the window to the window to create account
   * @param event
   * @throws IOException
   */
  @FXML
  private void enterCreateAccount(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/CreateAccount.fxml"));
    this.loginPanel.getChildren().setAll(pane);
  }

  /**
   * Method for logging in.
   * @param event
   * @throws IOException
   */
  @FXML
  private void login(ActionEvent event) throws IOException {
    Connection conn;
    /**Check if the required fields are empty*/
    if (this.emailfield.getText().isEmpty() || this.passwordfield.getText().isEmpty()) {
      this.errorMsg.setText("One or more mandatory fields have not been filled.");
      this.errorMsg.setOpacity(1.0);
    }
    else {
      try {
        /**Setup connection to Database*/
        conn = SQLiteConnect.ConnectDB();
        /**Preparing statement to see if there is such a account that matches the entered email and password.*/
        PreparedStatement findExistingUser = conn.prepareStatement("Select * from User where USERNAME=?");
        /**Get text from each field.*/
        findExistingUser.setString(1, this.emailfield.getText());
        /**Storing result in ResultSet to check if there are any matches*/
        ResultSet existingUsers = findExistingUser.executeQuery();
        if(existingUsers.next()){
          /**Get Encrypted Password string from Resultset*/
          String encryptedPassword = existingUsers.getString("PASSWORD");
          /**Deccrypt password retrieved from ResultSet*/
          String decryptedPassword = EncryptionModule.Decrypt(encryptedPassword);
          /**if the password in the text field matches the decrypted password, begin storing login details in
           * USERSESSION Table and CurrentLogin Singleton*/
          if (this.passwordfield.getText().equals(decryptedPassword)){
            /**get firstname, lastname, userID and userType to store in USERSESSION table in SQL.*/
            String firstNameUserSession = existingUsers.getString("FIRSTNAME");
            String lastNameUserSession = existingUsers.getString("LASTNAME");
            int userIDUserSession = existingUsers.getInt("USERID");
            String userTypeUserSession = existingUsers.getString("USERTYPE");
            /**store usersession variables in CurrentLogin singleton*/
            CurrentLogin.getInstance().setFirstName(firstNameUserSession);
            CurrentLogin.getInstance().setLastName(lastNameUserSession);
            CurrentLogin.getInstance().setUserID(userIDUserSession);
            CurrentLogin.getInstance().setUserType(userTypeUserSession);
            //store usersession in SQL Database for persistence
            PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO USERSESSION" +
                    "(FIRSTNAME, LASTNAME, USERID, USERTYPE) VALUES (?,?,?,?)");
            stmt2.setString(1, firstNameUserSession);
            stmt2.setString(2,lastNameUserSession);
            stmt2.setInt(3,userIDUserSession);
            stmt2.setString(4,userTypeUserSession);
            //testing purrposes
            System.out.println(CurrentLogin.getInstance().getFirstName());
            System.out.println(CurrentLogin.getInstance().getLastName());
            System.out.println(CurrentLogin.getInstance().getUserID());
            System.out.println(CurrentLogin.getInstance().getUserType());
            /**Executing update*/
            stmt2.executeUpdate();
            /**Load Menu depending on usertype*/
            switch (CurrentLogin.getInstance().getUserType()){
              case "Buyer":
                GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/ProductMenu.fxml"));
                this.loginPanel.getChildren().setAll(pane);
                break;
              case "Administrator":
                pane = FXMLLoader.load(this.getClass().getResource("/FXML/AdministratorAccountMenu.fxml"));
                this.loginPanel.getChildren().setAll(pane);
                break;
              case "Seller":
                pane = FXMLLoader.load(this.getClass().getResource("/FXML/SellerAccountMenu.fxml"));
                this.loginPanel.getChildren().setAll(pane);
                break;
            }

          }
        }
        /**if no results are found then output error message*/
        if(!existingUsers.next()) {
          this.errorMsg.setText("Incorrect username and/or password");
          this.errorMsg.setOpacity(1.0);
        }
        conn.close();
      }
      catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
