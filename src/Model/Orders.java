package Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Orders {
  /**private variables storing orderID, orderDate, productID and productName of an Orders object*/
  private SimpleIntegerProperty orderID;
  private SimpleStringProperty orderDate;
  private SimpleIntegerProperty productID;
  private SimpleStringProperty productName;

  /**Constructor for Orders object*/
  public Orders(SimpleIntegerProperty orderID, SimpleStringProperty orderDate, SimpleIntegerProperty productID, SimpleStringProperty productName) {
    this.orderID = orderID;
    this.orderDate = orderDate;
    this.productID = productID;
    this.productName = productName;
  }

  /**Getters for the variables of the Orders object*/
  public int getOrderID() {
    return this.orderID.get();
  }

  public SimpleIntegerProperty orderIDProperty() {
    return this.orderID;
  }

  public String getOrderDate() {
    return this.orderDate.get();
  }

  public SimpleStringProperty orderDateProperty() {
    return this.orderDate;
  }

  public int getProductID() {
    return this.productID.get();
  }

  public SimpleIntegerProperty productIDProperty() {
    return this.productID;
  }

  public String getProductName() {
    return this.productName.get();
  }

  public SimpleStringProperty productNameProperty() {
    return this.productName;
  }
}
