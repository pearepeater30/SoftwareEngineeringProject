package Model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Products {
  /**private variables storing productID, productName, productPrice, productStock, productDescription
   * and productUserID of an Orders object*/
  private SimpleIntegerProperty productID;
  private SimpleStringProperty productName;
  private SimpleDoubleProperty productPrice;
  private SimpleIntegerProperty productStock;
  private SimpleStringProperty productDescription;
  private SimpleIntegerProperty productUserID;

  /**Constructor for Products object*/
  public Products(SimpleIntegerProperty productID, SimpleStringProperty productName, SimpleDoubleProperty productPrice,
                  SimpleIntegerProperty productStock, SimpleStringProperty productDescription, SimpleIntegerProperty productUserID) {
    super();
    this.productID = productID;
    this.productName = productName;
    this.productPrice = productPrice;
    this.productStock = productStock;
    this.productDescription = productDescription;
    this.productUserID = productUserID;
  }

  /**Getters for the variables of the Products object*/
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

  public double getProductPrice() {
    return this.productPrice.get();
  }

  public SimpleDoubleProperty productPriceProperty() {
    return this.productPrice;
  }

  public int getProductStock() {
    return this.productStock.get();
  }

  public SimpleIntegerProperty productStockProperty() {
    return this.productStock;
  }

  public String getProductDescription() {
    return this.productDescription.get();
  }

  public SimpleStringProperty productDescriptionProperty() {
    return this.productDescription;
  }

  public int getProductUserID() {
    return this.productUserID.get();
  }

  public SimpleIntegerProperty productUserIDProperty() {
    return this.productUserID;
  }
}