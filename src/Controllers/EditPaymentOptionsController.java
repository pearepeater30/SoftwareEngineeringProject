package Controllers;

import Model.CurrentLogin;
import Tools.EncryptionModule;
import Tools.SQLiteConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditPaymentOptionsController {
  /**Set Current Pane to Current scene*/
  @FXML
  private GridPane editPaymentOptionPanel;
  /**TextFields for the Credit Card Number, Security Field, and Credit Card Provider*/
  @FXML
  private TextField creditCardField;
  @FXML
  private TextField securityCodeField;
  @FXML
  private TextField cardProviderField;
  /**Text used to display errors*/
  @FXML
  private Text errorMsg;
  /**A Choicebox used to hold the months for Credit Card expiration*/
  @FXML
  private ChoiceBox<String> expiryMonth;
  /**A Choicebox used to hold the years for the Credit Card expiration*/
  @FXML
  private ChoiceBox<Integer> expiryYear;
  /**ObservableLists that load the expiration months and years into the Choiceboxs*/
  private ObservableList monthList = FXCollections.observableArrayList();
  private ObservableList yearList = FXCollections.observableArrayList();

  //public no-args Constructor
  public EditPaymentOptionsController() {
    super();
  }

  /**
   * Functions that are executed upon the scene loading
   */
  @FXML
  private void initialize()
  {
    loadData();
  }

  /**
   * Loads the expiry months and expiry years into the ObservableList
   */
  private void loadData(){
    this.monthList.removeAll(this.monthList);
    this.yearList.removeAll(this.yearList);
    this.monthList.addAll("January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December");
    this.yearList.addAll(2019,2020,2021,2022,2023,2024,2025,2026,2027,2028,2029,2030);
    this.expiryMonth.getItems().addAll(this.monthList);
    this.expiryYear.getItems().addAll(this.yearList);

  }

  /**
   * Returns to previous Scene
   * @param event
   * @throws IOException
   */
  @FXML
  private void backAction(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/PaymentOptionsMenu.fxml"));
    this.editPaymentOptionPanel.getChildren().setAll(pane);
  }

  /**
   * Method that allows the user to change their payment method
   * @param event
   * @throws SQLException
   */
  @FXML
  private void changePayment(ActionEvent event) throws SQLException {
    /**If any TextFields are empty, display error message*/
    if (this.creditCardField.getText().isEmpty() || this.securityCodeField.getText().isEmpty() ||
            this.expiryMonth.getValue().isEmpty() || this.expiryYear.getItems().isEmpty() ||
            this.cardProviderField.getText().isEmpty()) {
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("One or more mandatory fields have not been filled in");
      this.errorMsg.setOpacity(1.0);
    }
    /**If the credit card number has letters, display error message*/
    else if (!this.creditCardField.getText().matches("[0-9]*") || !this.securityCodeField.getText().matches("[0-9]*")){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("Only numbers can be entered for Credit Card or Security Code");
      this.errorMsg.setOpacity(1.0);
    }
    else {
      /**Setup Connection to database*/
      Connection conn = SQLiteConnect.ConnectDB();
      /**Setting up PreparedStatement to see if there are Credit Cards associated to the currently logged in user*/
      PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CreditCard WHERE CCUSERID = ?");
      /**Insert Currently logged in user's UserID into the PreparedStatement above*/
      stmt.setInt(1, CurrentLogin.getInstance().getUserID());
      /**Store result from PreparedStatement above in ResultSet*/
      ResultSet existingCreditCard = stmt.executeQuery();
      /**if there are no reults from the query, then create a new CreditCard row for user*/
      if (!existingCreditCard.isBeforeFirst()) {
        /**Setting up PreparedStatement to insert new CreditCard into Database*/
        PreparedStatement newCard = conn.prepareStatement("INSERT INTO CreditCard" +
                "(CCENCRYPTEDCARDNO, CCEXPMONTH, CCEXPYEAR, CCENCRYPTEDSECURITYCODE, CCPROVIDER, CCID, CCUSERID) " +
                "VALUES (?,?,?,?,?,?,?)");
        /**Inserting encrypted CreditCard Number, expiry Month and Year, Encrypted security code, cardprovider
         * and Current logged in User's user ID. CCID  is left as null as it is auto-incrementing*/
        newCard.setString(1, EncryptionModule.Encrypt(this.creditCardField.getText()));
        newCard.setString(2, this.expiryMonth.getValue());
        newCard.setInt(3, this.expiryYear.getValue());
        newCard.setString(4, EncryptionModule.Encrypt(this.securityCodeField.getText()));
        newCard.setString(5, this.cardProviderField.getText());
        newCard.setString(6, null);
        newCard.setInt(7, CurrentLogin.getInstance().getUserID());
        /**Execute Update*/
        newCard.executeUpdate();
        this.errorMsg.setFill(Color.GREEN);
        this.errorMsg.setText("New Credit Card created!");
        this.errorMsg.setOpacity(1.0);

      }
      /**If there are existing Cards found in the Database, update credit card details*/
      else {
        /**Setting up PreparedStatement to update the CreditCard in Database*/
        PreparedStatement updateCard = conn.prepareStatement("UPDATE CreditCard " +
                "SET CCENCRYPTEDCARDNO = ?, CCEXPMONTH = ?, CCEXPYEAR = ?, CCENCRYPTEDSECURITYCODE = ?, CCPROVIDER = ?" +
                " WHERE CCUSERID = ?");
        /**Inserting encrypted CreditCard Number, expiry Month and Year, Encrypted security code, cardprovider
         * and Current logged in User's user ID. CCID  is left as null as it is auto-incrementing*/
        updateCard.setString(1, EncryptionModule.Encrypt(this.creditCardField.getText()));
        updateCard.setString(2, this.expiryMonth.getValue());
        updateCard.setInt(3, this.expiryYear.getValue());
        updateCard.setString(4, EncryptionModule.Encrypt(this.securityCodeField.getText()));
        updateCard.setString(5, this.cardProviderField.getText());
        updateCard.setInt(6, CurrentLogin.getInstance().getUserID());
        /**Executing update*/
        updateCard.executeUpdate();
        this.errorMsg.setFill(Color.GREEN);
        this.errorMsg.setText("Credit Card Updated!");
        this.errorMsg.setOpacity(1.0);
      }
      /**Close Connection*/
      conn.close();
    }

  }
}
