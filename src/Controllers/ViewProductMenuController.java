package Controllers;

import Model.CurrentLogin;
import Tools.SQLiteConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ViewProductMenuController {
  /**Pane for the Scene*/
  @FXML
  private GridPane viewProductPanel;
  /**ListView used to display products */
  @FXML
  private ListView yourProductList;
  /**Textfield used to enter the productID when updating the stock*/
  @FXML
  private TextField productIDField;
  /**Textfield used to enter the stock to be added to a product*/
  @FXML
  private TextField stockField;
  /**Text used to display message*/
  @FXML
  private Text errorMsg;

  //public no-args constructor
  public ViewProductMenuController() {
    super();
  }

  /**
   * Functions that are executed upon initialization of scene
   * @throws SQLException
   */
  @FXML
  private void initialize()throws SQLException{
    setProductView();
  }

  /**
   * Populate TableList with products that Current Logged in Seller sells
   * @throws SQLException
   */
  private void setProductView()throws SQLException {
    /**Setting up Connection to Database*/
    Connection conn = SQLiteConnect.ConnectDB();
    /**Setting up PreparedStatement to select all product's owned by current logged in Seller*/
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Product WHERE PRODUCTUSERID = ?");
    /**Inserting Currently logged in seller's userID into the PreparedStatement*/
    stmt.setInt(1, CurrentLogin.getInstance().getUserID());
    /**Storing Results in ResultSet*/
    ResultSet rs = stmt.executeQuery();
    /**Storing results found in ResultSet into ArrayList*/
    List<String> results = new ArrayList<String>();
    /**Iterating through each row in the ResultSet*/
    while(rs.next()){
      /**add results to the list*/
      results.add(rs.getString("PRODUCTNAME") + ", Product ID: " + rs.getString("PRODUCTID") + ", Product Stock: " + rs.getString("PRODUCTSTOCK"));
    }
    /**Inserting ArrayList into an ObservableList*/
    ObservableList ProductList = FXCollections.observableArrayList(results);
    /**Inserting ObservableList into the Listview*/
    this.yourProductList.setItems(ProductList);
    /**Close Connection to Database*/
    conn.close();
  }

  /**
   * Method to Update the stock of a specific product
   * @throws SQLException
   */
  @FXML
  private void updateStock()throws SQLException{
    /**Checks if productIDField and stockField are empty, if so, display error message*/
    if(this.productIDField.getText().isEmpty() || this.stockField.getText().isEmpty()) {
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("One or more mandatory fields are not filled in");
      this.errorMsg.setOpacity(1.0);
    }
    else{
      /**Set up Connection to Database*/
      Connection conn = SQLiteConnect.ConnectDB();
      /**Setting up PreparedStatement to get the current Stock of product selected*/
      PreparedStatement getCurrentStock = conn.prepareStatement("SELECT * FROM Product " +
              "WHERE PRODUCTID=? and PRODUCTUSERID = ?  ");
      /**Setting up PreparedStatement to update Stock of product selected*/
      PreparedStatement updateStock = conn.prepareStatement("UPDATE Product Set PRODUCTSTOCK = ? " +
              "WHERE PRODUCTID = ? and PRODUCTUSERID = ?");
      /**Insert productID and Currently logged in Seller's userID into getCurrentStock*/
      getCurrentStock.setInt(1,Integer.parseInt(this.productIDField.getText()));
      getCurrentStock.setInt(2,CurrentLogin.getInstance().getUserID());
      /**Storing Products found in ResultSet*/
      ResultSet CurrentStock = getCurrentStock.executeQuery();
      /**if no products found, display error message*/
      if(!CurrentStock.isBeforeFirst()){
        this.errorMsg.setFill(Color.RED);
        this.errorMsg.setText("No Products found");
        this.errorMsg.setOpacity(1.0);
      }

      else{
        /**Get thee quantity of stock of product before*/
        int oldStock = CurrentStock.getInt("PRODUCTSTOCK");
        /**Insert values of stock to be added summed with the stock before into update statement*/
        updateStock.setInt(1,Integer.parseInt(this.stockField.getText())+oldStock);
        /**Insert value of productID to the update Statement*/
        updateStock.setInt(2,Integer.parseInt(this.productIDField.getText()));
        /**Insert Currently logged in user's userID*/
        updateStock.setInt(3,CurrentLogin.getInstance().getUserID());
        /**Execute update*/
        updateStock.executeUpdate();
        this.errorMsg.setFill(Color.GREEN);
        this.errorMsg.setText("Updated Product Stock");
        this.errorMsg.setOpacity(1.0);
      }
      /**Close Connection*/
      conn.close();
      setProductView();
    }

  }

  /**
   * Return to previous scene
   * @throws IOException
   */
  @FXML
  private void backAction() throws IOException {
    GridPane pane = FXMLLoader.load(getClass().getResource("/FXML/SellerAccountMenu.fxml"));
    viewProductPanel.getChildren().setAll(pane);
  }
}
