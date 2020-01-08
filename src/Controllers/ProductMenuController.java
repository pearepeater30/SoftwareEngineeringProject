package Controllers;

import Model.CurrentLogin;
import Model.Products;
import Tools.SQLiteConnect;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductMenuController {
  /**Pane used for Current scene*/
  @FXML
  private GridPane productPanel;
  /**TableView and Columns used to store productName, productPrice and productDescription Products in Products Table*/
  @FXML
  private TableView<Products> tableProductList;
  @FXML
  private TableColumn<Products, String> productName;
  @FXML
  private TableColumn<Products, Number> productPrice;
  @FXML
  private TableColumn<Products, String> productDescription;
  @FXML
  private Text mainTitle;
  @FXML
  private Text errorMsg;
  @FXML
  private TextField searchBar;

  private String storedProductName = null;

  private double storedProductPrice = 0;

  private int storedProductID = 0;

  private int storedProductStock = 0;

  /**Creating a obeservablelist object which allows listeners to detect changes to it*/
  ObservableList<Products> ProductList = FXCollections.observableArrayList();

  public ProductMenuController() {
    super();
  }

  /**
   * @throws SQLException
   * Functions to be executed upon loading of scene
   */
  @FXML
  private void initialize() throws SQLException {
    setProductView();
    this.mainTitle.setText("Welcome " + CurrentLogin.getInstance().getFirstName() + " "
            + CurrentLogin.getInstance().getLastName());
    tableProductList.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue) -> storeValues(newValue));
    filterProduct();
  }


  /**
   * method used to populate the TableView in this Menu with products which can be added to basket and or wishlist
   * @throws SQLException
   */
  @FXML
  private void setProductView()throws SQLException {
    /**Setting up connection*/
    Connection conn = SQLiteConnect.ConnectDB();
    /**Creating prepared statement to use for populating table with products*/
    PreparedStatement getProduct = conn.prepareStatement("SELECT PRODUCTID, PRODUCTNAME, PRODUCTPRICE, PRODUCTSTOCK, " +
            "PRODUCTDESCRIPTION, PRODUCTUSERID FROM Product");
    /**Storing results from above statement*/
    ResultSet rs = getProduct.executeQuery();
    /**Adding all results retrieved to the list by creating Products objects and inserting them into the ObservableList
     * I had to use Javafx's Property data type as it allows them to be updated in TableView model.
     * */
    while (rs.next()) {
      this.ProductList.add(new Products(new SimpleIntegerProperty(rs.getInt("PRODUCTID")),
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
    this.productDescription.setCellValueFactory(cellData -> cellData.getValue().productDescriptionProperty());
    /**
     * setting the TableView to display items retrieved from the SQL query.
     */
    this.tableProductList.setItems(ProductList);
    conn.close();
  }

  /**
   * Method used to enter AccountMenu
   * @param event
   * @throws IOException
   */
  @FXML
  private void enterAccountMenu(ActionEvent event) throws IOException{
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/AccountMenu.fxml"));
    this.productPanel.getChildren().setAll(pane);
  }

  /**
   * Method used to enter Wishlist Menu
   * @param event
   * @throws IOException
   */
  @FXML
  private void enterWishlistMenu(ActionEvent event) throws IOException{
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/WishlistMenu.fxml"));
    this.productPanel.getChildren().setAll(pane);
  }

  /**
   * Method used to enter Basket Menu
   * @param event
   * @throws IOException
   */
  @FXML
  private void enterBasketMenu(ActionEvent event) throws IOException{
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/BasketMenu.fxml"));
    this.productPanel.getChildren().setAll(pane);
  }

  /**
   * Method used to enter Order Menu
   * @param event
   * @throws IOException
   */
  @FXML
  private void enterOrderMenu(ActionEvent event) throws IOException{
    GridPane pane = FXMLLoader.load(this.getClass().getResource("/FXML/OrderMenu.fxml"));
    this.productPanel.getChildren().setAll(pane);
  }


  /**
   * Method used to logout currently logged in user
   * @param event
   * @throws IOException
   */
  @FXML
  private void logout(ActionEvent event) throws IOException{
    Connection conn;
    try{
      /**Setting up Connection to Database*/
      conn = SQLiteConnect.ConnectDB();
      /**get firstname, lastname, userID and userType from CurrentLogin singleton*/
      String firstNameUserSession = CurrentLogin.getInstance().getFirstName();
      String lastNameUserSession = CurrentLogin.getInstance().getLastName();
      int userIDUserSession = CurrentLogin.getInstance().getUserID();
      String userTypeUserSession = CurrentLogin.getInstance().getUserType();
      /**Setting PreparedStatement to currently logged in user from USERSESSION table*/
      PreparedStatement logout = conn.prepareStatement("DELETE FROM USERSESSION " +
              "WHERE FIRSTNAME=? and LASTNAME=? and USERID=? and USERTYPE=?");
      /**Inserting value into the query to delete from USERSESSION*/
      logout.setString(1, firstNameUserSession);
      logout.setString(2,lastNameUserSession);
      logout.setInt(3,userIDUserSession);
      logout.setString(4,userTypeUserSession);
      /**Execute update*/
      logout.executeUpdate();
      /**Reset all values of CurrentLogin singleton*/
      CurrentLogin.getInstance().setFirstName(null);
      CurrentLogin.getInstance().setLastName(null);
      CurrentLogin.getInstance().setUserID(0);
      CurrentLogin.getInstance().setUserType(null);
      /**Close Connection*/
      conn.close();
      /**Take user back to Login Scene*/
      GridPane pane = FXMLLoader.load(getClass().getResource("/FXML/Login.fxml"));
      this.productPanel.getChildren().setAll(pane);
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Method to add user to Cart
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
      try{
        /**Setup Connection to Database*/
        conn = SQLiteConnect.ConnectDB();
        /**Setting up PreparedStatement to Insert selected Product to Cart*/
        PreparedStatement addProductToCart = conn.prepareStatement("INSERT INTO " +
                "Cart(CARTPRODUCTNAME, CARTPRODUCTPRICE, CARTPRODUCTPRODUCTID, CARTUSERID)" +
                "VALUES (?,?,?,?)");
        /**Inserting values of selected productname, productprice, productID and Currently logged in user's userID*/
        addProductToCart.setString(1,this.storedProductName);
        addProductToCart.setDouble(2,this.storedProductPrice);
        addProductToCart.setInt(3,this.storedProductID);
        addProductToCart.setInt(4,CurrentLogin.getInstance().getUserID());
        /**Executing update*/
        addProductToCart.executeUpdate();
        this.errorMsg.setFill(Color.GREEN);
        this.errorMsg.setText("Product Added to Basket");
        this.errorMsg.setOpacity(1.0);
        /**Closing Connection*/
        conn.close();
      }
      catch (Exception e){
        e.printStackTrace();
      }
    }

  }

  /**
   * Adds products to wishlist
   * @throws SQLException
   */
  @FXML
  private void addToWishlist() throws SQLException{
    Connection conn;
    if (storedProductName.equals(null) || storedProductPrice == 0 ||storedProductID == 0.0){
      this.errorMsg.setFill(Color.RED);
      this.errorMsg.setText("No product selected");
      this.errorMsg.setOpacity(1.0);
    }
    else{
      try{
        /**Setting up Connection to Database*/
        conn = SQLiteConnect.ConnectDB();
        /**Setting up PreparedStatement to Insert selected product to Wishlist table*/
        PreparedStatement addProductToCart = conn.prepareStatement("INSERT INTO " +
                "Wishlist(wishlistproductid, wishlistproductname, wishlistproductprice, wishlistuserid)" +
                "VALUES (?,?,?,?)");
        /**Inserting values of selected productname, productprice, productID and Currently logged in user's userID*/
        addProductToCart.setInt(1,this.storedProductID);
        addProductToCart.setString(2,this.storedProductName);
        addProductToCart.setDouble(3, this.storedProductPrice);
        addProductToCart.setInt(4,CurrentLogin.getInstance().getUserID());
        /**Execute Update*/
        addProductToCart.executeUpdate();
        this.errorMsg.setFill(Color.GREEN);
        this.errorMsg.setText("Product Added to Wishlist");
        this.errorMsg.setOpacity(1.0);
        /**Close Connection*/
        conn.close();
      }
      catch (Exception e){
        e.printStackTrace();
      }
    }

  }
  /**
   * @param product
   * storing values of the currently selected product.
   */
  private void storeValues(Products product){
    this.storedProductID = product.getProductID();
    this.storedProductName = product.getProductName();
    this.storedProductPrice = product.getProductPrice();
    this.storedProductStock = product.getProductStock();
  }

  /**Method used for searching for a specific product*/
  private void filterProduct(){
    /**Creating a filtered List consisting of items in the ProductList, setting the predicate to true as initially all
     * products should be displayed
     */
    FilteredList<Products> productsFilteredList = new FilteredList<>(this.ProductList, p -> true);
    /**Set a listener for the search bar to change the value of the predicate to the value of what is in the searchbar*/
    searchBar.textProperty().addListener((observable,oldValue,newValue) -> {
      productsFilteredList.setPredicate(products ->{
        if (newValue == null || newValue.isEmpty()){
          return true;
        }
        /**store the name in the search bar*/
        String lowerCaseFilter = newValue.toLowerCase();

        /**checks if the products in the filteredList match that of the value in the search bar*/
        if (products.getProductName().toLowerCase().contains(lowerCaseFilter)) {
          return true;
        }
        return false;
      });
    });

    /**Insert the Filtered List in a sortedList*/
    SortedList<Products> sortedList = new SortedList<>(productsFilteredList);

    /**Bind the sorted list comparator to the table*/
    sortedList.comparatorProperty().bind(this.tableProductList.comparatorProperty());

    /**Set the table to that of the sortedList*/
    this.tableProductList.setItems(sortedList);
  }
}
