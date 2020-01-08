package Controllers;

import Model.CurrentLogin;
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

public class EditAddressMenuController {

  /**The Current Panel used for scene changes*/
  @FXML
  private GridPane editAddressMenuPanel;
  /**Text field used to store streetname1*/
  @FXML
  private TextField streetName1Field;
  /**Text field used to store streetname2*/
  @FXML
  private TextField streetName2Field;
  /**Text field used to store streetname3*/
  @FXML
  private TextField streetName3Field;
  /**Text field used to store the city*/
  @FXML
  private TextField cityField;
  /**Text field used to store postcode*/
  @FXML
  private TextField postcodeField;
  /**Text field used to store streetname1*/
  @FXML
  private Text errorMsg;

  //public no-args constructor
  public EditAddressMenuController() {
    super();
  }

  /**
   * Returns to previous scene
   * @param event
   * @throws IOException
   */
  @FXML
  private void backAction(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/AddressMenu.fxml"));
    this.editAddressMenuPanel.getChildren().setAll(pane);
  }

  /**
   * Method used to update Address, depending on if the user already has an address, a new one may be created.
   * If use already has an address, the old one will be updated
   * @param event
   * @throws SQLException
   *
   */
  @FXML
  private void createAddress(ActionEvent event) throws SQLException {
    /**If any mandatory fields are empty, erroMsg is set to indicate that some fields are not filled*/
    if (this.streetName1Field.getText().isEmpty() || this.streetName2Field.getText().isEmpty()
            || this.cityField.getText().isEmpty() || this.postcodeField.getText().isEmpty()) {
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("One or more mandatory fields have not been filled.");
      this.errorMsg.setOpacity(1.0);
    }
    else {
      /**Setup Connection*/
      Connection conn = SQLiteConnect.ConnectDB();
      /**Setup PreparedStatement to see if a specific user has an address stored*/
      PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Address WHERE ADDRESSUSERID = ?");
      /**Insert values into PreparedStatement*/
      stmt.setInt(1, CurrentLogin.getInstance().getUserID());
      ResultSet rs = stmt.executeQuery();
      /**if not, create a new address row in DB*/
      if (!rs.next()) {
        /**Setup PreparedStatement*/
        PreparedStatement newAddress = conn.prepareStatement("INSERT INTO Address" +
                "(ADDRESSSTREETNAME1, ADDRESSSTREETNAME2, ADDRESSSTREETNAME3, " +
                "ADDRESSCITY, ADDRESSPOSTCODE, ADDRESSID, ADDRESSUSERID ) VALUES " +
                "(?,?,?,?,?,?,?)");
        /**Insert values into PreparedStatement*/
        newAddress.setString(1, this.streetName1Field.getText());
        newAddress.setString(2, this.streetName2Field.getText());
        newAddress.setString(3, this.streetName3Field.getText());
        newAddress.setString(4, this.cityField.getText());
        newAddress.setString(5, this.postcodeField.getText());
        newAddress.setString(6, null);
        newAddress.setInt(7, CurrentLogin.getInstance().getUserID());
        newAddress.executeUpdate();
        /**set errorMsg to indicate that a new address is created*/
        this.errorMsg.setFill(Color.GREEN);
        this.errorMsg.setText("New Address created!");
        this.errorMsg.setOpacity(1.0);

      }
      /**If an address is found, update Address for logged in user*/
      else {
        /**Setup PreparedStatement*/
        PreparedStatement oldAddress = conn.prepareStatement("UPDATE Address " +
                "SET ADDRESSSTREETNAME1 = ?, ADDRESSSTREETNAME2 = ?, ADDRESSSTREETNAME3 = ?, ADDRESSCITY = ?, ADDRESSPOSTCODE = ?" +
                " WHERE ADDRESSUSERID = ?");
        /**Insert values into PreparedStatement*/
        oldAddress.setString(1, this.streetName1Field.getText());
        oldAddress.setString(2, this.streetName2Field.getText());
        oldAddress.setString(3, this.streetName3Field.getText());
        oldAddress.setString(4, this.cityField.getText());
        oldAddress.setString(5, this.postcodeField.getText());
        oldAddress.setInt(6, CurrentLogin.getInstance().getUserID());
        oldAddress.executeUpdate();
        /**Set errorMsg to indicate address has been updated*/
        this.errorMsg.setFill(Color.GREEN);
        this.errorMsg.setText("Address Updated!");
        this.errorMsg.setOpacity(1.0);
      }
      /**close connection*/
      conn.close();
    }
  }
}
