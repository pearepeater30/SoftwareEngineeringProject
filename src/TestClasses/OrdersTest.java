package TestClasses;

import Model.Orders;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrdersTest {

  @Test
  public void constructionTest(){
    Orders order = new Orders(new SimpleIntegerProperty(1),new SimpleStringProperty("2019-05-13 17:15:29"), new SimpleIntegerProperty(2), new SimpleStringProperty("TestProduct"));
    assertEquals(1,order.getOrderID());
    assertEquals("2019-05-13 17:15:29", order.getOrderDate());
    assertEquals(2,order.getProductID());
    assertEquals("TestProduct",order.getProductName());
  }

}