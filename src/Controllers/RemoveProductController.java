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

public class RemoveProductController {
  /**Pane for current scene*/
  @FXML
  private GridPane removeProductsPanel;
  /**Text Field used to enter productID to be removed*/
  @FXML
  private TextField productIDField;
  /**Text used to display messages*/
  @FXML
  private Text errorMsg;

  //public no-args constructor
  public RemoveProductController() {
    super();
  }

  /**
   * Functions that are executed upon initialization of scene
   */
  @FXML
  private void initialize(){
  }

  /**
   * Return to previous scene
   * @param event
   * @throws IOException
   */
  @FXML
  private void backAction(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/SellerAccountMenu.fxml"));
    this.removeProductsPanel.getChildren().setAll(pane);
  }

  /**
   * Method used to remove product
   * @param event
   * @throws SQLException
   */
  @FXML
  private void removeProduct(ActionEvent event) throws SQLException {
    /**Check if produtIDField is empty, if so, then display error message*/
    if(productIDField.getText().isEmpty()){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("Please enter product ID!");
      this.errorMsg.setOpacity(1.0);
    }
    /**Setup Connection to Database*/
    Connection conn = SQLiteConnect.ConnectDB();
    /**Setting up Prepared Statement to see if product exists in Product Table*/
    PreparedStatement existingProduct = conn.prepareStatement("SELECT PRODUCTID FROM Product WHERE PRODUCTID = ? and PRODUCTUSERID = ?");
    /**Inserting ProductID and Current Logged in user's UserID of product*/
    existingProduct.setInt(1,Integer.parseInt(this.productIDField.getText()));
    existingProduct.setInt(2, CurrentLogin.getInstance().getUserID());
    /**Storing the results in a ResultSet*/
    ResultSet rs = existingProduct.executeQuery();
    if(rs.isBeforeFirst()) {
      /**Setting up PreparedStatement to delete product from Product*/
      PreparedStatement deleteProduct = conn.prepareStatement("DELETE FROM Product WHERE PRODUCTID = ?");
      /**Inserting ProductID of product to be deleted to PreparedStatement*/
      deleteProduct.setInt(1, Integer.parseInt(this.productIDField.getText()));
      /**Execute Update*/
      deleteProduct.executeUpdate();
      this.errorMsg.setFill(Color.GREEN);
      this.errorMsg.setText("Product removed!");
      this.errorMsg.setOpacity(1.0);
    }
    /**If no results found, then display error message*/
    else{
      errorMsg.setFill(Color.RED);
      errorMsg.setText("Product not found, please re-enter product ID");
      errorMsg.setOpacity(1.0);
    }
    /**Closing Connection*/
    conn.close();
  }

}

