package Controllers;

import Model.CurrentLogin;
import Model.Orders;
import Tools.SQLiteConnect;
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

import java.io.IOException;
import java.sql.*;

public class OrderMenuController {

  /**Pane for Current Scene*/
  @FXML
  private GridPane orderMenuPanel;
  /**TableView and Columns used to display orderID, orderDate, productID, productName from Order Table*/
  @FXML
  private TableView<Orders> orderTable;
  @FXML
  private TableColumn<Orders, Number> orderID;
  @FXML
  private TableColumn<Orders, String> orderDate;
  @FXML
  private TableColumn<Orders, Number> productID;
  @FXML
  private TableColumn<Orders, String> productName;

  /**ObservableList used to store Orders objects which are then added to the TableView*/
  private ObservableList<Orders> ordersList = FXCollections.observableArrayList();

  //public no-args constructor
  public OrderMenuController() {
    super();
  }

  /**
   * Functions that are executed upon scene initialization
   * @throws SQLException
   */
  @FXML
  private void initialize()throws SQLException{
    loadList();
  }

  /**
   * Populates the Table with items from the Order Table
   * @throws SQLException
   */
  @FXML
  private void loadList() throws SQLException{
    /**Setting up connection*/
    Connection conn = SQLiteConnect.ConnectDB();
    /**Creating prepared statement to use for populating table with products*/
    PreparedStatement getOrders = conn.prepareStatement("SELECT " +
            "Orders.ORDERID, Orders.ORDERDATE, Orders_ProductID.PRODUCTID, Product.PRODUCTNAME " +
            "FROM Orders INNER JOIN Orders_ProductID " +
            "ON Orders.ORDERID = Orders_ProductID.ORDERID " +
            "LEFT OUTER JOIN Product " +
            "ON Orders_ProductID.PRODUCTID = Product.PRODUCTID " +
            "WHERE ORDERUSERID = ?");
    getOrders.setInt(1, CurrentLogin.getInstance().getUserID());
    /**Storing results from above statement*/
    ResultSet rs = getOrders.executeQuery();
    /**Adding all results retrieved to the list by creating Products objects and inserting them into the ObservableList
     * I had to use Javafx's Property data type as it allows them to be updated in TableView model.
     * */
    while (rs.next()) {
      this.ordersList.add(new Orders(new SimpleIntegerProperty(rs.getInt("ORDERID")),
              new SimpleStringProperty(rs.getString("ORDERDATE")),
              new SimpleIntegerProperty(rs.getInt("PRODUCTID")),
              new SimpleStringProperty(rs.getString("PRODUCTNAME"))));
    }
    /**Setting the orderID, orderDate, productID and productName for each column with CellValueFactory. CellValueFactory is the only way in which to
     * populate a single cell in a TableView*/
    this.orderID.setCellValueFactory(cellData -> cellData.getValue().orderIDProperty());
    this.orderDate.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());
    this.productID.setCellValueFactory(cellData -> cellData.getValue().productIDProperty());
    this.productName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
    /**
     * setting the TableView to display items retrieved from the SQL query.
     */
    this.orderTable.setItems(this.ordersList);
    conn.close();
  }

  /**
   * Return to previous Scene
   * @param event
   * @throws IOException
   */
  @FXML
  private void backAction(ActionEvent event) throws IOException {
    GridPane pane = FXMLLoader.load(getClass().getResource("/FXML/ProductMenu.fxml"));
    this.orderMenuPanel.getChildren().setAll(pane);
  }
}
