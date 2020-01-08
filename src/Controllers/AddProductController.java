package Controllers;

import Model.CurrentLogin;
import Tools.SQLiteConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddProductController {
  /**Pane for the Scene*/
  @FXML
  private GridPane addRemoveProductPanel;
  /**TextFields for the name, price, stock and description of the product desired to be added in*/
  @FXML
  private TextField productNameField;
  @FXML
  private TextField productPriceField;
  @FXML
  private TextField currentStockField;
  @FXML
  private TextArea descriptionField;
  /**Text for displaying messages*/
  @FXML
  private Text errorMsg;


  //pubic no-args controller
  public AddProductController(){
    super();
  }

  /**
   * Functions that are executed upon initialization of scene
   */
  @FXML
  private void initialize(){
  }

  /**
   * @param event
   * @throws IOException
   * return to previous scene
   */
  @FXML
  private void backAction(ActionEvent event)throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/SellerAccountMenu.fxml"));
    this.addRemoveProductPanel.getChildren().setAll(pane);
  }

  /**
   * Method for adding product to the marketplace as a seller
   * @param event
   * @throws SQLException
   */
  @FXML
  private void addProduct(ActionEvent event) throws SQLException{
    /**
     * Checking if the fields are empty
     */
    if(this.productNameField.getText().isEmpty() || this.productPriceField.getText().isEmpty() ||
            this.currentStockField.getText().isEmpty() || this.descriptionField.getText().isEmpty()){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("One or more mandatory fields were not filled in");
      this.errorMsg.setOpacity(1.0);
    }
    /**
     * checking if the price field and the stock field does not contain any letters
     */
    else if(!this.productPriceField.getText().matches("[0-9]*\\.?[0-9]*") || !this.currentStockField.getText().matches("[0-9]*")){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("only numbers and decimal points are accepted for Price and stock");
      this.errorMsg.setOpacity(1.0);
    }
    /**
     * if criteria meet, prepare check for if the Seller is a ActiveSeller
     */
    else{
      /**Setup connection to database*/
      Connection conn = SQLiteConnect.ConnectDB();
      /**Setup preparestatement to see whether the Seller currently logged in is able to sell*/
      PreparedStatement checkinActiveSellers = conn.prepareStatement("SELECT * FROM ActiveSellers WHERE ACTIVESELLERSUSERID=?");
      /**Inserting values in preparedstatement*/
      checkinActiveSellers.setInt(1,CurrentLogin.getInstance().getUserID());
      /**Storing results in ResultSet*/
      ResultSet rs = checkinActiveSellers.executeQuery();
      /**if there are no results then display error message*/
      if(!rs.next()){
        this.errorMsg.setFill(Color.RED);
        this.errorMsg.setText("You are not an approved Seller, until an admin approves you, you cannot sell");
        this.errorMsg.setOpacity(1.0);
      }
      /**if user is found, then insert product into Product Table*/
      else{
        /**Setting up PreparedStatement*/ /***/
        PreparedStatement insertProduct = conn.prepareStatement("INSERT INTO Product(PRODUCTID, PRODUCTNAME, PRODUCTPRICE, " +
                "PRODUCTSTOCK, PRODUCTDESCRIPTION, PRODUCTUSERID) VALUES (?,?,?,?,?,?)");
        /**Inserting values into above PreparedStatement*/
        insertProduct.setString(1,null);
        insertProduct.setString(2,this.productNameField.getText());
        insertProduct.setDouble(3,Double.parseDouble(this.productPriceField.getText()));
        insertProduct.setInt(4,Integer.parseInt(this.currentStockField.getText()));
        insertProduct.setString(5,this.descriptionField.getText());
        insertProduct.setInt(6, CurrentLogin.getInstance().getUserID());
        insertProduct.executeUpdate();
        /**Displaying message showing inserting product was successful*/
        this.errorMsg.setFill(Color.GREEN);
        this.errorMsg.setText("Product added to your selling products!");
        this.errorMsg.setOpacity(1.0);
      }
      /**Closing Connection*/
      conn.close();
    }
  }
}

