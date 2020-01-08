package Controllers;

import Model.CurrentLogin;
import Tools.EncryptionModule;
import Tools.SQLiteConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordMenuController {

  /**The Current Panel used for scene changes*/
  @FXML
  private GridPane changePasswordMenuPanel;
  /**PasswordField used to store previous password*/
  @FXML
  private PasswordField oldPasswordField;
  /**PasswordField used to store new password*/
  @FXML
  private PasswordField newPasswordField;
  /**PasswordField used to confirm new password*/
  @FXML
  private PasswordField confirmPasswordField;
  /**Error Message stored as a variable*/
  @FXML
  private Text errorMsg;

  //public no-args Constructor
  public ChangePasswordMenuController() {
    super();
  }
  /**
   * Returns to previous Screen. Depending on the Account type, the user will be returned to different Account menus
   * @param event
   * @throws IOException
   */
  @FXML
  private void backAction(ActionEvent event) throws IOException {
    switch (CurrentLogin.getInstance().getUserType()){
      case "Buyer":
        GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/AccountMenu.fxml"));
        this.changePasswordMenuPanel.getChildren().setAll(pane);
        break;
      case "Administrator":
        pane = FXMLLoader.load(this.getClass().getResource("/FXML/AdministratorAccountMenu.fxml"));
        this.changePasswordMenuPanel.getChildren().setAll(pane);
        break;
      case "Seller":
        pane = FXMLLoader.load(this.getClass().getResource("/FXML/SellerAccountMenu.fxml"));
        this.changePasswordMenuPanel.getChildren().setAll(pane);
        break;
    }
  }

  /**
   * @param event
   * @throws IOException
   * @throws SQLException
   * Method used to change a users password.
   */
  @FXML
  private void changePassword(ActionEvent event) throws IOException, SQLException {
    /**Setup Connection*/
    Connection conn = SQLiteConnect.ConnectDB();
    /**Creating Query as Prepared Statement*/
    PreparedStatement stmt = conn.prepareStatement("SELECT PASSWORD FROM USER WHERE USERID = ?");
    /**Inserting values into prepared statement*/
    stmt.setInt(1, CurrentLogin.getInstance().getUserID());
    /**Storing results from query in a ResultSet*/
    ResultSet rs = stmt.executeQuery();
    /**Retrieve hashed password from PASSWORD column of User*/
    String retrievedpassword = rs.getString("PASSWORD");
    /**If Any field is empty, errorMsg is set to indicate that some fields are not filled*/
    if (this.oldPasswordField.getText().isEmpty() || this.newPasswordField.getText().isEmpty()
            || this.confirmPasswordField.getText().isEmpty()) {
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("One or more mandatory fields have not been filled.");
      this.errorMsg.setOpacity(1.0);
    }
    /**If newPasswordField matches oldPasswordField, then errorMsg is set to indicate that they match*/
    else if (this.newPasswordField.getText().matches(this.oldPasswordField.getText())){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("New Password Cannot be old password");
      this.errorMsg.setOpacity(1.0);
    }
    /**If newPasswordField does not match confirmPasswordField, errorMsg is set to indicate they do not match*/
    else if (!this.confirmPasswordField.getText().equals(this.newPasswordField.getText())){
        this.errorMsg.setFill(Color.RED);
        this.errorMsg.setText("Confirmed password does not match with new password");
        this.errorMsg.setOpacity(1.0);
    }

    /**If password in oldPasswordField does not match retrieved password, errorMsg is set to indicate they do not match*/
    else if (!EncryptionModule.Decrypt(retrievedpassword).equals(oldPasswordField.getText())){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("Old password does not match");
      this.errorMsg.setOpacity(1.0);
    }

    else{
      /**encrypt the new password*/
      String encryptedpassword = EncryptionModule.Encrypt(this.newPasswordField.getText());
      /**Setting up PreparedStatement*/
      PreparedStatement stmt2 = conn.prepareStatement("UPDATE USER SET PASSWORD = ? WHERE USERID = ?");
      /**Insert values into prepared Statment*/
      stmt2.setString(1, encryptedpassword);
      stmt2.setInt(2, CurrentLogin.getInstance().getUserID());
      /**Execute prepared statement*/
      stmt2.executeUpdate();
      /**Indicate successful password change*/
      this.errorMsg.setFill(Color.GREEN);
      this.errorMsg.setText("Password Successfully changed!");
      this.errorMsg.setOpacity(1.0);
    }
    /**Close Connection*/
    conn.close();
  }
}

