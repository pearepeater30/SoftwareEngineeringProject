package Controllers;

import Model.Address;
import Model.CreditCard;
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
import java.sql.*;

public class BasketMenuController {
  /**Pane for current Scene*/
  @FXML
  private GridPane basketMenuPanel;
  /**Table and Table Column which store all products in Cart*/
  @FXML
  private TableView<Products> productTable;
  @FXML
  private TableColumn<Products, String> productName;
  @FXML
  private TableColumn<Products, Number> productPrice;
  /**Text used to display errors*/
  @FXML
  private Text errorMsg;
  /**Text used to display the Total Price of items in Cart*/
  @FXML
  private Text totalPriceText;
  /**Variables used to store productname, productprice and productID of selected Item on the productTable*/
  private String storedProductName;

  private double storedProductPrice;

  private int storedProductID;

  private int storedProductStock;

  //public no-args constructor
  public BasketMenuController() {
    super();
  }

  /**
   * Functions that are executed immediately upon initialization of scene
   * @throws SQLException
   */
  @FXML
  private void initialize() throws SQLException{
    loadBasket();
    this.productTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> storeValues(newValue));
  }

  /**
   * Function to populate table with entries from Cart Table
   * @throws SQLException
   */
  @FXML
  private void loadBasket()throws SQLException {
    double totalPrice = 0.0;
    /**Setting up connection*/
    Connection conn = SQLiteConnect.ConnectDB();
    /**Creating a obeservablelist object which allows listeners to detect changes to it*/
    ObservableList<Products> ProductList = FXCollections.observableArrayList();
    /**Creating prepared statement to use for populating table with products*/
    PreparedStatement getProduct = conn.prepareStatement("SELECT CARTPRODUCTNAME, CARTPRODUCTPRICE, CARTPRODUCTPRODUCTID, CARTUSERID " +
            "FROM Cart WHERE CARTUSERID = ?");
    getProduct.setInt(1, CurrentLogin.getInstance().getUserID());
    /**Storing results from above statement*/
    ResultSet rs = getProduct.executeQuery();
    /**Adding all results retrieved to the list by creating Products objects and inserting them into the ObservableList
     * I had to use Javafx's Property data type as it allows them to be updated in TableView model.
     * */
    while (rs.next()) {
      ProductList.add(new Products(new SimpleIntegerProperty(rs.getInt("CARTPRODUCTPRODUCTID")),
              new SimpleStringProperty(rs.getString("CARTPRODUCTNAME")),
              new SimpleDoubleProperty(rs.getDouble("CARTPRODUCTPRICE")),
              new SimpleIntegerProperty(0),
              new SimpleStringProperty(null),
              new SimpleIntegerProperty(rs.getInt("CARTUSERID"))));

      totalPrice += rs.getDouble("CARTPRODUCTPRICE");
    }
    /**Setting each value for each column with CellValueFactory. CellValueFactory is the only way in which to
     * populate a single cell in a TableView*/
    this.productName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
    this.productPrice.setCellValueFactory(cellData -> cellData.getValue().productPriceProperty());
    /**
     * setting the TableView to display items retrieved from the SQL query.
     */
    this.productTable.setItems(ProductList);
    /**Setting the totalPrice of all items in the cart*/
    this.totalPriceText.setText("Basket Total: $" + (totalPrice));
    conn.close();
  }

  /**
   * Method to store values upon clicking a row in the Table
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
   * Returns to previous scene
   */
  @FXML
  private void backAction(ActionEvent event) throws IOException{
    GridPane pane = FXMLLoader.load(getClass().getResource("/FXML/ProductMenu.fxml"));
    this.basketMenuPanel.getChildren().setAll(pane);
  }

  /**
   * Method for removing items from cart
   * @param event
   * @throws SQLException
   */
  @FXML
  private void removeFromCart(ActionEvent event) throws SQLException{
    Connection conn;
    /**Checks value of currently stored values*/
    if (this.storedProductName.equals(null) || this.storedProductPrice == 0 || this.storedProductID == 0.0){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("No product selected");
      this.errorMsg.setOpacity(1.0);
    }
    /**If there are values stored*/
    else{
      try{
        /**Setup connection to Database*/
        conn = SQLiteConnect.ConnectDB();
        /**Setting up PreparedStatement to delete selected Items*/
        PreparedStatement addProductToCart = conn.prepareStatement("DELETE FROM Cart WHERE CARTPRODUCTPRODUCTID = ? and CARTUSERID = ?");
        /**Inserting values for the productID of item desired to be removed and the current logged in user into the PreparedStatement*/
        addProductToCart.setInt(1,this.storedProductID);
        addProductToCart.setInt(2,CurrentLogin.getInstance().getUserID());
        /**Execute Statement*/
        addProductToCart.executeUpdate();
        this.errorMsg.setFill(Color.GREEN);
        this.errorMsg.setText("Product Removed from Basket");
        this.errorMsg.setOpacity(1.0);
        /**Close Connection*/
        conn.close();
      }
      catch (Exception e){
        e.printStackTrace();
      }
      /**Repopulate Table with rows from Cart*/
      loadBasket();
    }

  }

  /**
   * Method used to checkout. This clears the Cart of the User currently logged in and reduces the stock of a
   * Product by how many there were in the user's cart
   * @param event
   * @throws SQLException
   */
  @FXML
  private void checkOut(ActionEvent event) throws SQLException{
    /**Setting up connection to database*/
    Connection conn = SQLiteConnect.ConnectDB();
    /**Setting PreparedStatement to select the productID of all items in the Cart for Product Removal later on*/
    PreparedStatement findProductkeysinBasket = conn.prepareStatement("SELECT CARTPRODUCTPRODUCTID " +
            "from Cart where CARTUSERID = ?");
    /**Setting PreparedStatement to see if the user has a credit card associated with their account*/
    PreparedStatement findCreditCard = conn.prepareStatement("SELECT * FROM CreditCard where CCUSERID = ?");
    /**Setting PreparedStatement to see if there are any products*/
    PreparedStatement findStockforCart = conn.prepareStatement("SELECT PRODUCTSTOCK FROM Product WHERE PRODUCTID " +
            "IN (SELECT CARTPRODUCTPRODUCTID FROM Cart WHERE CARTUSERID= ?)");
    /**Set the Value for PreparedStatement findProductkeysinBasket CARTUSERID to the Current logged in user's ID*/
    findProductkeysinBasket.setInt(1,CurrentLogin.getInstance().getUserID());
    /**Set the value for PreparedStatement find CreditCard CCUSERID to the Current logged in user's ID*/
    findCreditCard.setInt(1,CurrentLogin.getInstance().getUserID());
    /**Set the value for PreparedStatement find Stock for products in Card to the Current logged in user's ID*/
    findStockforCart.setInt(1,CurrentLogin.getInstance().getUserID());
    /**store the keys in ResultSet*/
    ResultSet productkeysinBasket = findProductkeysinBasket.executeQuery();
    /**store rows found in CreditCard in ResultSet*/
    ResultSet creditCardFound = findCreditCard.executeQuery();
    /**store stocks for each product in cart in a ResultSet*/
    ResultSet stockInCart = findStockforCart.executeQuery();
    /**Get Current time*/
    java.util.Date dt = new java.util.Date();
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String currentTime = sdf.format(dt);
    /**set boolean variable to see if number of stock */
    boolean zeroStockItems = false;
    /**Initialize the itemStock variable*/
    int itemStock;
    /**Checks if the stock of all produts in the Cart is zero*/
    while (stockInCart.next()) {
      /**Get ProductStock Column*/
      itemStock = stockInCart.getInt("PRODUCTSTOCK");
      /**If stock of one item in cart is zero, set zeroStockItems flag*/
      if (itemStock == 0) {
        zeroStockItems = true;
      }
    }
    /**If no Credit Card associated with user is found, then display error message*/
    if (!creditCardFound.isBeforeFirst()){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("No Credit Card added to account!");
      this.errorMsg.setOpacity(1.0);
    }
    /**If there are no rows in the Cart, then display error message*/
    else if(!productkeysinBasket.isBeforeFirst()){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("Add Some products in the basket first!");
      this.errorMsg.setOpacity(1.0);
    }
    /**Check the stock of items in the user's cart*/
    else{
      if (zeroStockItems) {
        this.errorMsg.setFill(Color.RED);
        this.errorMsg.setText("One or more Items have zero stock!");
        this.errorMsg.setOpacity(1.0);
        }
      else{
        /**Setting up PreparedStatement to create order*/
        PreparedStatement createOrder = conn.prepareStatement("INSERT INTO Orders(ORDERID, ORDERDATE, " +
                "ORDERPAYMENTMETHOD, ORDERADDRESS, ORDERUSERID) VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        /**Setting values for the OrderID, OrderDate, OrderPaymentMethod, OrderAddress and the OrderUserID*/
        /**OrderID set as null as column is auto-incrementing*/
        createOrder.setString(1,null);
        createOrder.setString(2,(currentTime));
        createOrder.setString(3, CreditCard.toString(CurrentLogin.getInstance().getUserID()));
        createOrder.setString(4,Address.toString(CurrentLogin.getInstance().getUserID()));
        createOrder.setInt(5,CurrentLogin.getInstance().getUserID());
        /**Execute Query*/
        createOrder.executeUpdate();
        /**Get generated keys to create Junction Tables*/
        ResultSet rs = createOrder.getGeneratedKeys();
        /**Storing order ID*/
        int orderKey = rs.getInt(1);
        /**While there are product IDs in the ResultSet update the stock value*/
        while(productkeysinBasket.next()){
          /**Get the Stock of the current productID in the ResultSet*/
          PreparedStatement getStock = conn.prepareStatement("SELECT PRODUCTSTOCK FROM Product " +
                  "WHERE PRODUCTID = ?");
          /**Insert ProductID into PreparedStatement*/
          getStock.setInt(1,productkeysinBasket.getInt("CARTPRODUCTPRODUCTID"));
          /**Store stock of current Product*/
          ResultSet oldStockSet = getStock.executeQuery();
          /**Creating Junction Table*/
          PreparedStatement insertIntoOrder = conn.prepareStatement("INSERT INTO " +
                  "Orders_ProductID (ORDERID, PRODUCTID) VALUES (?,?)");
          /**Inserting value of Order ID and the current ProductID*/
          insertIntoOrder.setInt(1,orderKey);
          insertIntoOrder.setInt(2,productkeysinBasket.getInt("CARTPRODUCTPRODUCTID"));
          /**Execute Update of creating Junction Table*/
          insertIntoOrder.executeUpdate();
          /**store oldStockValue*/
          int oldStockValue = oldStockSet.getInt("PRODUCTSTOCK");
          /**PreparedStatement used to update the Stock of the Current Product*/
          PreparedStatement updateStock = conn.prepareStatement("UPDATE Product SET PRODUCTSTOCK = ? " +
                  "WHERE PRODUCTID = ?");
          /**Inserting value of oldStockValue subtracted by 1 and the Product ID of the current Product*/
          updateStock.setInt(1,oldStockValue - 1);
          updateStock.setInt(2,productkeysinBasket.getInt("CARTPRODUCTPRODUCTID"));
          /**Execute update of updatedstock*/
          updateStock.executeUpdate();
        }
        /**After all Products have been inserted into junction table and have stock reduced, delete all rows from Cart*/
        PreparedStatement emptyCart = conn.prepareStatement("DELETE FROM Cart WHERE CARTUSERID = ?");
        /**Insert values of Current Logged in user into above PreparedStatement*/
        emptyCart.setInt(1,CurrentLogin.getInstance().getUserID());
        /**Execute Update*/
        emptyCart.executeUpdate();
        /**Repopulate Table*/
        loadBasket();
        this.errorMsg.setFill(Color.GREEN);
        this.errorMsg.setText("Checkout Complete!");
        this.errorMsg.setOpacity(1.0);
        /**Close Connection*/
      }
    }
    conn.close();
  }

}
