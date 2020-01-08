package Controllers;

import Model.Address;
import Model.CurrentLogin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.SQLException;

public class AddressMenuController {

  /**
   * The Current Panel used for scene changes
   */
  @FXML
  private GridPane addressMenuPanel;
  @FXML
  private TextArea addressBox;

  //public no-args controller
  public AddressMenuController() {
  }

  @FXML
  private void initialize()throws SQLException{
    this.addressBox.setEditable(false);
    setAddressBox();
  }

  /**
   * Sets scene to EditAddressMenuController, where users can change their address
   * @param event
   * @throws IOException
   */
  @FXML
  private void enterChangeAddressMenu(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/EditAddressMenu.fxml"));
    this.addressMenuPanel.getChildren().setAll(pane);
  }

  /**
   * Goes back to AccountMenu
   * @param event
   * @throws IOException
   */
  @FXML
  private void backAction(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/AccountMenu.fxml"));
    this.addressMenuPanel.getChildren().setAll(pane);
  }

  /**
   * Displays address of currently logged in user
   * @throws SQLException
   */
  @FXML
  private void setAddressBox() throws SQLException {
    this.addressBox.setText(Address.toString(CurrentLogin.getInstance().getUserID()));
  }

}
