package Controllers;

import Model.CreditCard;
import Model.CurrentLogin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.SQLException;

public class PaymentOptionController {

  /**
   * Pane for CurrentScene
   */
  @FXML
  private GridPane paymentOptionsMenuPanel;
  /**TextArea used to display CreditCard Details*/
  @FXML
  private TextArea ccBox;

  //public no-args constructor
  public PaymentOptionController() {
    super();
  }

  /**
   * Functions in method will be executed upon scene initialization
   * @throws SQLException
   */
  @FXML
  private void initialize()throws SQLException{
    this.ccBox.setEditable(false);
    this.setCcBox();
  }

  /**
   * Return to previous Scene
   * @param event
   * @throws IOException
   */
  @FXML
  private void backAction(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/AccountMenu.fxml"));
    this.paymentOptionsMenuPanel.getChildren().setAll(pane);
  }

  /**
   * Method used to enter EditPaymentOptions
   * @param event
   * @throws IOException
   */
  @FXML
  private void enterEditPaymentOptions(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/EditPaymentOptions.fxml"));
    this.paymentOptionsMenuPanel.getChildren().setAll(pane);
  }

  /**
   * Method calls toString method to display Credit Card
   * @throws SQLException
   */
  @FXML
  private void setCcBox() throws SQLException {
    this.ccBox.setText(CreditCard.toString(CurrentLogin.getInstance().getUserID()));
  }
}
