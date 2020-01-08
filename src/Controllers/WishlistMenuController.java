package Controllers;

import Model.CurrentLogin;
import Model.Products;
import Tools.SQLiteConnect;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WishlistMenuController {
  /**Pane for current Scene*/
  @FXML
  private GridPane wishlistMenuPanel;
  /**Table and Columns used to display Product Names and Product Prices from Wishlist Table*/
  @FXML
  private TableView<Products> productTable;
  @FXML
  private TableColumn<Products, String> productName;
  @FXML
  private TableColumn<Products, Number> productPrice;
  /**Text used to display messages*/
  @FXML
  private Text errorMsg;

  private String storedProductName = null;

  private double storedProductPrice = 0;

  private int storedProductID = 0;

  private int storedProductStock = 0;

  //public no-args constructor
  public WishlistMenuController() {
    super();
  }

  /**
   * Functions to be executed upon initialization of scene
   * @throws SQLException
   */
  @FXML
  private void initialize()throws SQLException{
    loadWishlist();
    this.productTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> storeValues(newValue));
  }

  /**
   * Method used to populate Table with values from Wishlist Table
   * @throws SQLException
   */
  @FXML
  private void loadWishlist()throws SQLException {
    /**Setting up connection*/
    Connection conn = SQLiteConnect.ConnectDB();
    /**Creating a obeservablelist object which allows listeners to detect changes to it*/
    ObservableList<Products> ProductList = FXCollections.observableArrayList();
    /**Creating prepared statement to use for populating table with products*/
    PreparedStatement getProduct = conn.prepareStatement("SELECT PRODUCTID, PRODUCTNAME, PRODUCTPRICE, PRODUCTSTOCK, " +
            "PRODUCTDESCRIPTION, PRODUCTUSERID " +
            "FROM Product " +
            "WHERE PRODUCTID IN " +
            "(SELECT WISHLISTPRODUCTID FROM Wishlist WHERE WISHLISTUSERID = ?)");
    getProduct.setInt(1, CurrentLogin.getInstance().getUserID());
    /**Storing results from above statement*/
    ResultSet rs = getProduct.executeQuery();
    /**Adding all results retrieved to the list by creating Products objects and inserting them into the ObservableList
     * I had to use Javafx's Property data type as it allows them to be updated in TableView model.
     * */
    while (rs.next()) {
      ProductList.add(new Products(new SimpleIntegerProperty(rs.getInt("PRODUCTID")),
              new SimpleStringProperty(rs.getString("PRODUCTNAME")),
              new SimpleDoubleProperty(rs.getDouble("PRODUCTPRICE")),
              new SimpleIntegerProperty(rs.getInt("PRODUCTSTOCK")),
              new SimpleStringProperty(rs.getString("PRODUCTDESCRIPTION")),
              new SimpleIntegerProperty(rs.getInt("PRODUCTUSERID"))));
    }
    /**Setting each value for each column with CellValueFactory. CellValueFactory is the only way in which to
     * populate a single cell in a TableView*/
    this.productName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
    this.productPrice.setCellValueFactory(cellData -> cellData.getValue().productPriceProperty());
    /**
     * setting the TableView to display items retrieved from the SQL query.
     */
    this.productTable.setItems(ProductList);
    conn.close();
  }


  /**
   * Method used to store values of items selected in the Table
   * @param product
   */
  private void storeValues(Products product){
    if(product.getProductName().equals(null) || product.getProductID() == 0 || product.getProductPrice() == 0.0){
    }
    else {
      this.storedProductID = product.getProductID();
      this.storedProductName = product.getProductName();
      this.storedProductPrice = product.getProductPrice();
      this.storedProductStock = product.getProductStock();
    }

  }

  /**
   * Method used to add products selected in the Wishlist Table to cart
   * @throws SQLException
   */
  @FXML
  private void addToCart() throws SQLException{
    Connection conn;
    if (storedProductName.equals(null) || storedProductPrice == 0 ||storedProductID == 0.0){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("No product selected");
      this.errorMsg.setOpacity(1.0);
    }
    else if (this.storedProductStock == 0){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("Product has zero stock");
      this.errorMsg.setOpacity(1.0);
    }
    else{
      /**Setting up Connection to Database*/
      conn = SQLiteConnect.ConnectDB();
      /**Setting up PreparedStatement to add Product to Cart*/
      PreparedStatement addProductToCart = conn.prepareStatement("INSERT INTO " +
              "Cart(CARTPRODUCTNAME, CARTPRODUCTPRICE, CARTPRODUCTPRODUCTID, CARTUSERID)" +
              "VALUES (?,?,?,?)");
      /**Insert Values of productName, productPrice and productID and Currently logged in user's userID into the
       * PreparedStatement*/
      addProductToCart.setString(1,this.storedProductName);
      addProductToCart.setDouble(2,this.storedProductPrice);
      addProductToCart.setInt(3,this.storedProductID);
      addProductToCart.setInt(4,CurrentLogin.getInstance().getUserID());
      /**Execute Update*/
      addProductToCart.executeUpdate();
      this.errorMsg.setFill(Color.GREEN);
      this.errorMsg.setText("Product Added to Basket");
      this.errorMsg.setOpacity(1.0);
      /**Close Connection*/
      conn.close();
    }
  }

  /**
   * @param event
   * @throws SQLException
   * Method for removing items from cart
   */
  @FXML
  private void removeFromWishlist(ActionEvent event) throws SQLException {
    Connection conn;
    /**Checks value of currently stored values*/
    if (this.storedProductName.equals(null) || this.storedProductPrice == 0 || this.storedProductID == 0.0) {
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("No product selected");
      this.errorMsg.setOpacity(1.0);
    }
    /**If there are values stored*/
    else {
      try {
        /**Setup connection to Database*/
        conn = SQLiteConnect.ConnectDB();
        /**Setting up PreparedStatement to delete selected Items*/
        PreparedStatement addProductToCart = conn.prepareStatement("DELETE FROM Wishlist WHERE WISHLISTPRODUCTID = ? and WISHLISTUSERID = ?");
        /**Inserting values for the productID of item desired to be removed and the current logged in user into the PreparedStatement*/
        addProductToCart.setInt(1, this.storedProductID);
        addProductToCart.setInt(2, CurrentLogin.getInstance().getUserID());
        /**Execute Statement*/
        addProductToCart.executeUpdate();
        this.errorMsg.setFill(Color.GREEN);
        this.errorMsg.setText("Product Removed from Wishlist");
        this.errorMsg.setOpacity(1.0);
        /**Close Connection*/
        conn.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      /**Repopulate Table with rows from Wishlist*/
      loadWishlist();
    }
  }


  /**
   * @param event
   * @throws IOException
   * Return to previous scene
   */
  @FXML
  private void backAction(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/ProductMenu.fxml"));
    this.wishlistMenuPanel.getChildren().setAll(pane);
  }
}
