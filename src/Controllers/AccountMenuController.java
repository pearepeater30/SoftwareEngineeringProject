package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class AccountMenuController {

  /**
   * The Current Panel used for scene changes
   */
  @FXML
  private GridPane accountMenuPanel;

  //public no-args constructor
  public AccountMenuController() {
    super();
  }

  /**
   * Sets scene to AddressMenuController
   * @param event
   * @throws IOException
   */
  @FXML
  private void enterChangeAddressMenu(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/AddressMenu.fxml"));
    this.accountMenuPanel.getChildren().setAll(pane);
  }

  /**
   * Sets scene to ChangePasswordMenu
   * @param event
   * @throws IOException
   */
  @FXML
  private void enterChangePasswordMenu(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/ChangePasswordMenu.fxml"));
    this.accountMenuPanel.getChildren().setAll(pane);
  }

  /**
   * Sets scene to PaymentOptionsMenu
   * @param event
   * @throws IOException
   */
  @FXML
  private void enterPaymentOptionsMenu(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/PaymentOptionsMenu.fxml"));
    this.accountMenuPanel.getChildren().setAll(pane);
  }

  /**
   * Sets scene to ProductMenu
   * @param event
   * @throws IOException
   */
  @FXML
  private void backAction(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/ProductMenu.fxml"));
    this.accountMenuPanel.getChildren().setAll(pane);
  }
}
