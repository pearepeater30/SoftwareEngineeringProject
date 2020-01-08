package Controllers;

import Tools.SQLiteConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Tools.EncryptionModule;


public class CreateAccountController {
  /**The Current Panel used for scene changes*/
  @FXML
  private GridPane createAccountPanel;
  /**Text field for email used for new Account*/
  @FXML
  private TextField emailfield;
  /**Password field for password used for new Account*/
  @FXML
  private PasswordField passwordfield;
  /**Text field for first name used for new Account*/
  @FXML
  private TextField fnamefield;
  /**Text field for last name used for new Account*/
  @FXML
  private TextField lnamefield;
  /**Text used to display errors*/
  @FXML
  private Text errorMsg;
  /**Observable List used to store usertypes to then transfer to userType*/
  private ObservableList list = FXCollections.observableArrayList();
  /**Choice Box used to display userType*/
  @FXML
  private ChoiceBox<String> userType;

  // Add a public no-args constructor
  public  CreateAccountController() {
    super();
  }

  /**
   * Functions here are executed upon initialization of scene
   */
  @FXML
  private void initialize()
  {
    loadData();
  }


  /**
   * Populating dropdown menu with items
   */
  private void loadData(){
    this.list.removeAll(list);
    String a = "Buyer";
    String b = "Seller";
    String c= "Administrator";
    this.list.addAll(a,b,c);
    this.userType.getItems().addAll(list);

  }

  /**
   * Return to previous scene
   * @param event
   * @throws IOException
   */
  @FXML
  private void backAction(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/Login.fxml"));
    this.createAccountPanel.getChildren().setAll(pane);

  }

  /**
   * Method used to create a new Account
   * @param event
   * @throws SQLException
   */
  @FXML
  private void createAccount(ActionEvent event)throws SQLException{
    Connection conn;
    /**If any mandatory fields are empty, then errorMsg is set to indicate that some fields are empty*/
    if(this.emailfield.getText().isEmpty() || this.passwordfield.getText().isEmpty()
            || this.fnamefield.getText().isEmpty() || this.lnamefield.getText().isEmpty()
            || this.userType.getItems().isEmpty()) {
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("One or more mandatory fields have not been filled.");
      this.errorMsg.setOpacity(1.0);
    }
    /**If firstnamefield and/or lastnamefield contains any characters aside from letters, then errorMsg is set to indicate that they must not contain
     * other characters*/
    else if (!this.fnamefield.getText().matches("[a-zA-Z]+")
            || !this.lnamefield.getText().matches("[a-zA-Z]+")){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("First Name or Last Name must not contain numbers or other characters");
      this.errorMsg.setOpacity(1.0);
    }
    else{
      try{
        /**Setup Connection*/
        conn = SQLiteConnect.ConnectDB();
        /**Create PreparedStatement Query to see if there are existing emails*/
        PreparedStatement checkUsers = conn.prepareStatement("SELECT USERNAME FROM User WHERE USERNAME = ? ");
        /**Insert value into PreparedStatement*/
        checkUsers.setString(1,this.emailfield.getText());
        /**Store Query results into ResultSet*/
        ResultSet rs = checkUsers.executeQuery();
        /**Checks if there are any existing emails for other accounts*/
        if (rs.next()){
          String usedUsername = rs.getString("USERNAME");
          /**if so, errorMsg is displayed showing the user to use a different email.*/
          if(emailfield.getText().equals(usedUsername)){
            this.errorMsg.setFill(Color.RED);
            this.errorMsg.setText("Email already used, please change email");
            this.errorMsg.setOpacity(1.0);
          }
        }

        else{
          /**Encrypt the password in passwordField*/
          String encryptedpassword = EncryptionModule.Encrypt(this.passwordfield.getText());
          //testing purposes
          String decryptedpassword = EncryptionModule.Decrypt(encryptedpassword);
          System.out.println("your encrypted password is: "+ encryptedpassword);
          System.out.println("your decrypted password is: " + decryptedpassword);
          /**Creating Prepared Statement*/
          PreparedStatement stmt = conn.prepareStatement("INSERT INTO USER (USERNAME, PASSWORD, USERID, FIRSTNAME, LASTNAME, USERTYPE) " +
                  "VALUES (?,?,?,?,?,?)");
          /**Inserting values into prepared statement*/
          stmt.setString(1,this.emailfield.getText());
          stmt.setString(2,encryptedpassword);
          stmt.setString(3,null);
          stmt.setString(4,this.fnamefield.getText());
          stmt.setString(5,this.lnamefield.getText());
          stmt.setString(6,this.userType.getValue());
          /**executing prepared statement*/
          stmt.executeUpdate();
          /**Set errorMsg to indicate that an account has successfully been created*/
          this.errorMsg.setFill(Color.GREEN);
          this.errorMsg.setText("Account Successfully Created!");
          this.errorMsg.setOpacity(1.0);
        }
        /**Closing connection*/
        conn.close();
      }
      /**catch SQLException*/
      catch(SQLException e){
        e.printStackTrace();
      }
    }
  }
}
